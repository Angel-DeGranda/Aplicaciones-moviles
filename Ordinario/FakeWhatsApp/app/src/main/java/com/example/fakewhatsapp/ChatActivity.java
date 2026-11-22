package com.example.fakewhatsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private static final String EXTRA_CONVERSACION_ID = "conversacion_id";
    private static final String EXTRA_TITULO = "titulo";
    private static final String EXTRA_AVATAR_URL = "avatar_url";
    private static final String EXTRA_OTRO_ID = "otro_perfil_id";

    // Fase 8: tiempos del indicador de escritura y de la última conexión
    private static final long REPETIR_ESCRIBIENDO_MS = 3000;   // cada cuánto aviso "sigo escribiendo"
    private static final long PAUSA_ESCRIBIENDO_MS = 4000;     // sin teclear este tiempo = dejé de escribir
    private static final long CADUCA_ESCRIBIENDO_MS = 6000;    // si no llega otro aviso, se oculta
    private static final long REVISAR_CONTACTO_MS = 30000;

    // Así se abre el chat desde otras pantallas.
    // otroPerfilId: el id de la otra persona en un chat directo (null en grupos)
    public static Intent crearIntent(Context context, String conversacionId,
                                     String titulo, String avatarUrl, String otroPerfilId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CONVERSACION_ID, conversacionId);
        intent.putExtra(EXTRA_TITULO, titulo);
        intent.putExtra(EXTRA_AVATAR_URL, avatarUrl);
        intent.putExtra(EXTRA_OTRO_ID, otroPerfilId);
        return intent;
    }

    // Fase 9: chat que se está viendo ahora (para no notificar sus mensajes). null = ninguno
    public static volatile String conversacionVisible = null;

    private String conversacionId;
    private String otroPerfilId;
    private String miId;

    private RecyclerView rvMensajes;
    private LinearLayoutManager layoutManager;
    private MensajesAdapter adapter;
    private TextView tvVacio;
    private TextView tvSubtitulo;
    private ProgressBar progreso;
    private EditText etMensaje;
    private Button btnEnviar;

    private MensajeService mensajes;
    private EstadoService estado;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private RealtimeClient realtime;
    private boolean historialCargado = false;
    private boolean hayMasAnteriores = true;
    private boolean cargandoAnteriores = false;
    private boolean consultandoNuevos = false;
    private boolean enviando = false;
    private boolean enPrimerPlano = false;

    // Fase 8
    private long ultimoLeidoMarcado = 0;
    private boolean yoEscribiendo = false;
    private long ultimoAvisoEscribiendo = 0;
    private final Map<String, Long> escribiendoHasta = new HashMap<>(); // perfil -> hasta cuándo mostrarlo
    private String textoContacto = "";   // "en línea" / "últ. vez ..." / miembros del grupo

    // Fase 11
    private ImageButton btnFoto;
    private ConversacionService conversaciones;
    private boolean esGrupo;                                        // sin "otro perfil" = grupo
    private final Map<String, String> nombresMiembros = new HashMap<>();
    private final ExecutorService hiloFotos = Executors.newSingleThreadExecutor();
    private boolean enviandoFoto = false;

    // Selector de fotos del sistema (no pide permisos)
    private final ActivityResultLauncher<PickVisualMediaRequest> selectorFoto =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    enviarFoto(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_chat);

        conversacionId = getIntent().getStringExtra(EXTRA_CONVERSACION_ID);
        if (conversacionId == null) {
            Toast.makeText(this, "No se pudo abrir el chat", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        otroPerfilId = getIntent().getStringExtra(EXTRA_OTRO_ID);
        miId = new SessionManager(this).getUserId();

        rvMensajes = findViewById(R.id.rvMensajes);
        tvVacio = findViewById(R.id.tvVacio);
        tvSubtitulo = findViewById(R.id.tvSubtitulo);
        progreso = findViewById(R.id.progreso);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnFoto = findViewById(R.id.btnFoto);
        conversaciones = new ConversacionService(this);
        esGrupo = otroPerfilId == null;

        mensajes = new MensajeService(this);
        estado = new EstadoService(this);

        configurarCabecera();
        ajustarEspacios();

        // Lista de mensajes: empieza pegada al final, como un chat
        adapter = new MensajesAdapter(miId, new MensajesAdapter.Acciones() {
            @Override
            public void onTocarFoto(Mensaje mensaje) {
                VerImagenActivity.abrir(ChatActivity.this, mensaje.getMediaUrl());
            }

            @Override
            public void onMantenerPresionado(Mensaje mensaje) {
                confirmarEliminar(mensaje);
            }
        });
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMensajes.setLayoutManager(layoutManager);
        rvMensajes.setAdapter(adapter);

        // Al subir hasta el principio, cargamos los mensajes anteriores
        rvMensajes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && layoutManager.findFirstVisibleItemPosition() == 0) {
                    cargarAnteriores();
                }
            }
        });

        btnEnviar.setOnClickListener(v -> enviar());
        btnFoto.setOnClickListener(v -> selectorFoto.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));

        // Indicador de escritura: avisamos mientras el usuario teclea
        etMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                alTeclear(s.toString().trim().isEmpty());
            }
        });

        realtime = new RealtimeClient(this, conversacionId, new RealtimeClient.Listener() {
            @Override
            public void onMensajeNuevo(Mensaje mensaje) {
                recibirMensaje(mensaje);
            }

            @Override
            public void onSuscrito() {
                pedirNuevos();
                refrescarLectura();   // pudo cambiar mientras no había conexión
            }

            @Override
            public void onDesconectado() {
                // RealtimeClient reconecta solo; al volver a suscribirse se recupera lo perdido
                escribiendoHasta.clear();
                actualizarSubtitulo();
            }

            @Override
            public void onLecturaCambiada() {
                refrescarLectura();
            }

            @Override
            public void onEscribiendo(String perfilId, boolean escribiendo) {
                otroEscribiendo(perfilId, escribiendo);
            }

            @Override
            public void onMensajeEliminado(long mensajeId) {
                if (adapter.quitar(mensajeId)) {
                    actualizarVacio();
                }
            }
        });

        cargarHistorial();
        if (esGrupo) {
            cargarMiembros();
        }
    }

    // ---------- GRUPOS (Fase 11) ----------
    // Nombres para las burbujas y lista de participantes en la cabecera
    private void cargarMiembros() {
        conversaciones.listarMiembros(conversacionId, new ConversacionService.MiembrosCallback() {
            @Override
            public void onExito(List<Perfil> miembros) {
                nombresMiembros.clear();
                List<String> nombres = new ArrayList<>();
                for (Perfil p : miembros) {
                    nombresMiembros.put(p.getId(), p.getNombreMostrar());
                    if (!p.getId().equals(miId)) {
                        nombres.add(p.getNombreMostrar());
                    }
                }
                nombres.add("Tú");
                textoContacto = android.text.TextUtils.join(", ", nombres);
                adapter.setGrupo(true, nombresMiembros);
                actualizarSubtitulo();
            }

            @Override
            public void onError(String texto) {
                // Sin nombres el chat sigue funcionando; se reintenta al volver a abrirlo
            }
        });
    }

    // ---------- ENVIAR FOTO (Fase 11) ----------
    private void enviarFoto(Uri uri) {
        if (enviandoFoto) return;
        enviandoFoto = true;
        btnFoto.setEnabled(false);
        Toast.makeText(this, "Enviando foto…", Toast.LENGTH_SHORT).show();

        // Reducir y comprimir en un hilo aparte (no congela la pantalla)
        hiloFotos.execute(() -> {
            byte[] jpeg;
            try {
                jpeg = ImagenUtil.prepararFotoChat(this, uri);
            } catch (IOException e) {
                runOnUiThread(() -> fotoTerminada("No se pudo leer la imagen"));
                return;
            }

            mensajes.enviarImagen(conversacionId, jpeg, new MensajeService.MensajeCallback() {
                @Override
                public void onExito(Mensaje enviado) {
                    fotoTerminada(null);
                    adapter.agregarAlFinal(Collections.singletonList(enviado));
                    actualizarVacio();
                    bajarAlFinal();
                    marcarLeido();
                }

                @Override
                public void onError(String texto) {
                    fotoTerminada(texto);
                }
            });
        });
    }

    private void fotoTerminada(String error) {
        enviandoFoto = false;
        btnFoto.setEnabled(true);
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    // ---------- ELIMINAR (Fase 11) ----------
    // Solo se pueden eliminar los mensajes propios ("eliminar para todos")
    private void confirmarEliminar(Mensaje mensaje) {
        if (!mensaje.esMio(miId)) return;

        new AlertDialog.Builder(this)
                .setTitle("¿Eliminar mensaje?")
                .setMessage("Se eliminará para todos en este chat.")
                .setPositiveButton("Eliminar para todos", (d, w) -> eliminar(mensaje))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminar(Mensaje mensaje) {
        mensajes.eliminar(mensaje.getId(), new MensajeService.SimpleCallback() {
            @Override
            public void onExito() {
                if (adapter.quitar(mensaje.getId())) {
                    actualizarVacio();
                }
            }

            @Override
            public void onError(String texto) {
                Toast.makeText(ChatActivity.this, texto, Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------- INTERFAZ ----------
    private void configurarCabecera() {
        TextView tvTitulo = findViewById(R.id.tvTitulo);
        ImageView imgAvatar = findViewById(R.id.imgAvatar);

        tvTitulo.setText(getIntent().getStringExtra(EXTRA_TITULO));

        Glide.with(this)
                .load(getIntent().getStringExtra(EXTRA_AVATAR_URL))
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .fallback(R.drawable.ic_avatar_placeholder)
                .circleCrop()
                .into(imgAvatar);

        // Fase 11: tocar la foto la muestra en grande
        imgAvatar.setOnClickListener(v ->
                VerImagenActivity.abrir(this, getIntent().getStringExtra(EXTRA_AVATAR_URL)));

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }

    // La barra de escritura sube con el teclado y no queda bajo las barras del sistema
    private void ajustarEspacios() {
        View raiz = findViewById(R.id.raiz);
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (vista, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
            vista.setPadding(barras.left, barras.top, barras.right,
                    Math.max(barras.bottom, teclado.bottom));
            return insets;
        });
    }

    // "escribiendo…" tiene prioridad sobre "en línea" / "últ. vez ..."
    private void actualizarSubtitulo() {
        String texto;
        if (!hayAlguienEscribiendo()) {
            texto = textoContacto;
        } else if (esGrupo && escribiendoHasta.size() == 1) {
            // En grupos se dice quién: "Ana está escribiendo…"
            String nombre = nombresMiembros.get(escribiendoHasta.keySet().iterator().next());
            texto = nombre != null ? nombre + " está escribiendo…" : "escribiendo…";
        } else {
            texto = "escribiendo…";
        }
        tvSubtitulo.setText(texto);
        tvSubtitulo.setVisibility(texto.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // ---------- HISTORIAL ----------
    private void cargarHistorial() {
        progreso.setVisibility(View.VISIBLE);

        mensajes.cargarUltimos(conversacionId, new MensajeService.ListaCallback() {
            @Override
            public void onExito(List<Mensaje> lista) {
                progreso.setVisibility(View.GONE);
                historialCargado = true;
                adapter.setMensajes(lista);
                hayMasAnteriores = lista.size() >= MensajeService.TAMANO_PAGINA;
                actualizarVacio();
                bajarAlFinal();
                marcarLeido();
                refrescarLectura();
                conectarRealtime();
            }

            @Override
            public void onError(String texto) {
                progreso.setVisibility(View.GONE);
                Toast.makeText(ChatActivity.this, texto, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarAnteriores() {
        if (cargandoAnteriores || !hayMasAnteriores || adapter.getItemCount() == 0) return;
        cargandoAnteriores = true;

        mensajes.cargarAnteriores(conversacionId, adapter.getPrimerId(),
                new MensajeService.ListaCallback() {
                    @Override
                    public void onExito(List<Mensaje> lista) {
                        cargandoAnteriores = false;
                        hayMasAnteriores = lista.size() >= MensajeService.TAMANO_PAGINA;
                        if (lista.isEmpty()) return;

                        // Guardamos dónde estaba el primer mensaje visible...
                        int posicion = layoutManager.findFirstVisibleItemPosition();
                        View primero = layoutManager.findViewByPosition(posicion);
                        int desplazamiento = primero == null
                                ? 0
                                : primero.getTop() - rvMensajes.getPaddingTop();

                        int agregados = adapter.agregarAlInicio(lista);

                        // ...y lo dejamos en el mismo lugar para que la pantalla no salte
                        layoutManager.scrollToPositionWithOffset(posicion + agregados, desplazamiento);
                    }

                    @Override
                    public void onError(String texto) {
                        cargandoAnteriores = false;
                    }
                });
    }

    // ---------- TIEMPO REAL ----------
    private void conectarRealtime() {
        if (enPrimerPlano && historialCargado) {
            realtime.conectar();
        }
    }

    private void recibirMensaje(Mensaje mensaje) {
        // Si mandó un mensaje, ya no está escribiendo
        if (mensaje.getRemitenteId() != null
                && escribiendoHasta.remove(mensaje.getRemitenteId()) != null) {
            actualizarSubtitulo();
        }

        boolean estabaAlFinal = estaAlFinal();
        int agregados = adapter.agregarAlFinal(Collections.singletonList(mensaje));
        if (agregados > 0) {
            actualizarVacio();
            if (estabaAlFinal) {
                bajarAlFinal();
            }
            marcarLeido();
        }
    }

    // Recupera lo que pudo perderse mientras no había conexión
    private void pedirNuevos() {
        if (consultandoNuevos) return;
        consultandoNuevos = true;

        mensajes.cargarNuevos(conversacionId, adapter.getUltimoId(),
                new MensajeService.ListaCallback() {
                    @Override
                    public void onExito(List<Mensaje> lista) {
                        consultandoNuevos = false;
                        if (lista.isEmpty()) return;

                        boolean estabaAlFinal = estaAlFinal();
                        int agregados = adapter.agregarAlFinal(lista);
                        if (agregados > 0) {
                            actualizarVacio();
                            if (estabaAlFinal) {
                                bajarAlFinal();
                            }
                            marcarLeido();
                        }
                    }

                    @Override
                    public void onError(String texto) {
                        consultandoNuevos = false;
                        if (enPrimerPlano) {
                            handler.postDelayed(ChatActivity.this::pedirNuevos, 3000);
                        }
                    }
                });
    }

    // ---------- RECIBIDO / LEÍDO (Fase 8) ----------
    // Con el chat abierto y a la vista, todo lo que hay en pantalla cuenta como leído
    private void marcarLeido() {
        if (!enPrimerPlano || !historialCargado) return;
        long ultimo = adapter.getUltimoId();
        if (ultimo > ultimoLeidoMarcado) {
            ultimoLeidoMarcado = ultimo;
            estado.marcarLeido(conversacionId, ultimo);
        }
    }

    // Pide hasta dónde recibieron / leyeron los demás y actualiza las palomitas
    private void refrescarLectura() {
        estado.estadoLectura(conversacionId, (entregadoId, leidoId) ->
                adapter.setEstadoLectura(entregadoId, leidoId));
    }

    // ---------- ÚLTIMA CONEXIÓN (Fase 8) ----------
    private final Runnable revisarContacto = new Runnable() {
        @Override
        public void run() {
            if (otroPerfilId == null || !enPrimerPlano) return;
            estado.estadoContacto(otroPerfilId, (enLinea, ultimaConexion) -> {
                textoContacto = enLinea
                        ? "en línea"
                        : FechaUtil.formatearUltimaConexion(ultimaConexion);
                actualizarSubtitulo();
            });
            handler.postDelayed(this, REVISAR_CONTACTO_MS);
        }
    };

    // ---------- INDICADOR DE ESCRITURA (Fase 8) ----------
    // Lado que escribe: avisa al empezar, repite cada 3 s y avisa al parar
    private void alTeclear(boolean cajaVacia) {
        handler.removeCallbacks(dejarDeEscribir);
        if (cajaVacia) {
            dejarDeEscribir.run();
            return;
        }

        long ahora = SystemClock.elapsedRealtime();
        if (!yoEscribiendo || ahora - ultimoAvisoEscribiendo >= REPETIR_ESCRIBIENDO_MS) {
            yoEscribiendo = true;
            ultimoAvisoEscribiendo = ahora;
            realtime.enviarEscribiendo(true);
        }
        handler.postDelayed(dejarDeEscribir, PAUSA_ESCRIBIENDO_MS);
    }

    private final Runnable dejarDeEscribir = () -> {
        if (yoEscribiendo) {
            yoEscribiendo = false;
            realtime.enviarEscribiendo(false);
        }
    };

    // Lado que recibe: se muestra hasta que llegue "dejó de escribir" o pasen 6 s sin avisos
    private void otroEscribiendo(String perfilId, boolean escribiendo) {
        if (escribiendo) {
            escribiendoHasta.put(perfilId, SystemClock.elapsedRealtime() + CADUCA_ESCRIBIENDO_MS);
            handler.removeCallbacks(limpiarEscribiendo);
            handler.postDelayed(limpiarEscribiendo, CADUCA_ESCRIBIENDO_MS);
        } else {
            escribiendoHasta.remove(perfilId);
        }
        actualizarSubtitulo();
    }

    private final Runnable limpiarEscribiendo = () -> {
        actualizarSubtitulo();   // hayAlguienEscribiendo() quita los vencidos
        if (!escribiendoHasta.isEmpty()) {
            handler.postDelayed(this.limpiarEscribiendo, 1000);
        }
    };

    private boolean hayAlguienEscribiendo() {
        long ahora = SystemClock.elapsedRealtime();
        Iterator<Map.Entry<String, Long>> it = escribiendoHasta.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue() < ahora) it.remove();
        }
        return !escribiendoHasta.isEmpty();
    }

    // ---------- ENVIAR ----------
    private void enviar() {
        if (enviando) return;
        String texto = etMensaje.getText().toString().trim();
        if (texto.isEmpty()) return;

        enviando = true;
        btnEnviar.setEnabled(false);

        mensajes.enviarTexto(conversacionId, texto, new MensajeService.MensajeCallback() {
            @Override
            public void onExito(Mensaje enviado) {
                enviando = false;
                btnEnviar.setEnabled(true);

                // Solo vaciamos la caja si no siguieron escribiendo mientras se enviaba
                if (etMensaje.getText().toString().trim().equals(texto)) {
                    etMensaje.setText("");   // esto también avisa "dejó de escribir"
                }

                adapter.agregarAlFinal(Collections.singletonList(enviado));
                actualizarVacio();
                bajarAlFinal();
                marcarLeido();
            }

            @Override
            public void onError(String textoError) {
                // El texto se queda en la caja para poder reintentar
                enviando = false;
                btnEnviar.setEnabled(true);
                Toast.makeText(ChatActivity.this, textoError, Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------- AYUDAS ----------
    private boolean estaAlFinal() {
        int total = adapter.getItemCount();
        return total == 0 || layoutManager.findLastVisibleItemPosition() >= total - 2;
    }

    private void bajarAlFinal() {
        if (adapter.getItemCount() > 0) {
            rvMensajes.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private void actualizarVacio() {
        tvVacio.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    // ---------- CICLO DE VIDA ----------
    @Override
    protected void onResume() {
        super.onResume();
        enPrimerPlano = true;
        conversacionVisible = conversacionId;
        Notificaciones.cancelar(this, conversacionId);   // ya lo está viendo
        conectarRealtime();
        marcarLeido();   // lo que llegó mientras la pantalla no se veía
        handler.removeCallbacks(revisarContacto);
        revisarContacto.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        enPrimerPlano = false;
        if (conversacionId != null && conversacionId.equals(conversacionVisible)) {
            conversacionVisible = null;
        }
        handler.removeCallbacks(revisarContacto);
        handler.removeCallbacks(dejarDeEscribir);
        if (realtime != null) {
            dejarDeEscribir.run();   // si no alcanza a salir, al otro se le oculta solo a los 6 s
            realtime.desconectar();
        }
        escribiendoHasta.clear();
        if (tvSubtitulo != null) actualizarSubtitulo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        hiloFotos.shutdown();
        if (realtime != null) {
            realtime.desconectar();
        }
    }
}
