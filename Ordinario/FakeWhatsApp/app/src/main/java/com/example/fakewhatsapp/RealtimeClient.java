package com.example.fakewhatsapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class RealtimeClient {

    // Todos los métodos se llaman en el hilo principal
    public interface Listener {
        void onMensajeNuevo(Mensaje mensaje);
        void onSuscrito();
        void onDesconectado();
        // Fase 8: otro miembro recibió o leyó mensajes (hay que pedir el estado de lectura)
        void onLecturaCambiada();
        // Fase 8: otro miembro empezó / dejó de escribir
        void onEscribiendo(String perfilId, boolean escribiendo);
        // Fase 11: alguien eliminó un mensaje de este chat
        void onMensajeEliminado(long mensajeId);
    }

    private static final long LATIDO_MS = 25000;
    private static final long MARGEN_TOKEN_S = 20;
    private static final int ESPERA_MAXIMA_S = 30;

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build();

    private final ApiClient api;
    private final SessionManager sesion;
    private final String conversacionId;
    private final String miId;
    private final String topic;
    private final Listener listener;
    private final Handler main = new Handler(Looper.getMainLooper());
    private static final ExecutorService hilo = Executors.newSingleThreadExecutor();
    private final Gson gson = new Gson();

    private WebSocket socket;
    private boolean activo = false;
    private int generacion = 0;
    private int intentos = 0;
    private long contadorRef = 0;
    private String joinRef = "";
    private boolean esperandoLatido = false;
    private boolean unido = false;   // el canal ya aceptó la suscripción

    public RealtimeClient(Context context, String conversacionId, Listener listener) {
        this.api = ApiClient.getInstance(context);
        this.sesion = new SessionManager(context);
        this.conversacionId = conversacionId;
        this.miId = sesion.getUserId();
        this.topic = "realtime:chat-" + conversacionId;
        this.listener = listener;
    }

    // ---------- API PÚBLICA ----------
    public void conectar() {
        if (activo) return;
        activo = true;
        intentos = 0;
        abrir();
    }

    public void desconectar() {
        activo = false;
        main.removeCallbacks(reconectar);
        cerrarSocket();
    }

    // Avisa a los demás miembros que estoy (o dejé de estar) escribiendo.
    // No se guarda en la base: viaja por el canal como "broadcast".
    public void enviarEscribiendo(boolean escribiendo) {
        if (!unido) return;

        JsonObject datos = new JsonObject();
        datos.addProperty("perfil_id", miId);
        datos.addProperty("escribiendo", escribiendo);

        JsonObject payload = new JsonObject();
        payload.addProperty("type", "broadcast");
        payload.addProperty("event", "escribiendo");
        payload.add("payload", datos);

        enviar(topic, "broadcast", payload, siguienteRef());
    }

    // ---------- CONEXIÓN ----------
    // "generacion" identifica la conexión vigente: lo que llegue de una anterior se ignora
    private void abrir() {
        final int mia = ++generacion;
        hilo.execute(() -> {
            try {
                String token = api.obtenerTokenValido();
                if (token != null) {
                    main.post(() -> {
                        if (mia == generacion && activo) abrirSocket(mia, token);
                    });
                }
            } catch (IOException e) {
                main.post(() -> {
                    if (mia == generacion && activo) programarReconexion();
                });
            }
        });
    }

    private void abrirSocket(int mia, String token) {
        String url = SupabaseConfig.URL.replaceFirst("^http", "ws")
                + "/realtime/v1/websocket?apikey=" + SupabaseConfig.KEY + "&vsn=1.0.0";

        socket = CLIENT.newWebSocket(new Request.Builder().url(url).build(), new WebSocketListener() {
            @Override
            public void onOpen(WebSocket ws, Response respuesta) {
                main.post(() -> {
                    if (mia == generacion) unirse(token);
                });
            }

            @Override
            public void onMessage(WebSocket ws, String texto) {
                main.post(() -> {
                    if (mia == generacion) procesar(texto);
                });
            }

            @Override
            public void onClosed(WebSocket ws, int codigo, String motivo) {
                main.post(() -> {
                    if (mia == generacion) conexionPerdida();
                });
            }

            @Override
            public void onFailure(WebSocket ws, Throwable t, Response respuesta) {
                main.post(() -> {
                    if (mia == generacion) conexionPerdida();
                });
            }
        });
    }

    private void unirse(String token) {
        JsonObject broadcast = new JsonObject();
        broadcast.addProperty("ack", false);
        broadcast.addProperty("self", false);

        JsonObject presence = new JsonObject();
        presence.addProperty("key", "");

        JsonObject cambio = new JsonObject();
        cambio.addProperty("event", "INSERT");
        cambio.addProperty("schema", "public");
        cambio.addProperty("table", "mensajes");
        cambio.addProperty("filter", "conversacion_id=eq." + conversacionId);
        // Fase 8: cambios de recibido / leído de los miembros de este chat
        JsonObject lectura = new JsonObject();
        lectura.addProperty("event", "UPDATE");
        lectura.addProperty("schema", "public");
        lectura.addProperty("table", "conversaciones_miembros");
        lectura.addProperty("filter", "conversacion_id=eq." + conversacionId);

        // Fase 11: mensajes que se marcan como eliminados
        JsonObject eliminados = new JsonObject();
        eliminados.addProperty("event", "UPDATE");
        eliminados.addProperty("schema", "public");
        eliminados.addProperty("table", "mensajes");
        eliminados.addProperty("filter", "conversacion_id=eq." + conversacionId);

        JsonArray cambios = new JsonArray();
        cambios.add(cambio);
        cambios.add(lectura);
        cambios.add(eliminados);

        JsonObject config = new JsonObject();
        config.add("broadcast", broadcast);
        config.add("presence", presence);
        config.add("postgres_changes", cambios);
        // Fase 12: canal privado. Supabase solo deja entrar a los miembros del chat
        // (reglas en realtime.messages, ver fase12_seguridad.sql)
        config.addProperty("private", true);

        JsonObject payload = new JsonObject();
        payload.add("config", config);
        payload.addProperty("access_token", token);

        joinRef = siguienteRef();
        enviar(topic, "phx_join", payload, joinRef);
        iniciarLatido();
    }

    private void conexionPerdida() {
        if (!activo) return;
        cerrarSocket();
        listener.onDesconectado();
        programarReconexion();
    }

    private void cerrarSocket() {
        generacion++;
        main.removeCallbacks(latido);
        main.removeCallbacks(renovarToken);
        esperandoLatido = false;
        unido = false;
        if (socket != null) {
            socket.cancel();
            socket = null;
        }
    }

    // Esperas de 1, 2, 4, 8, 16 y luego 30 segundos
    private void programarReconexion() {
        long espera = Math.min(ESPERA_MAXIMA_S, 1L << Math.min(intentos, 5));
        intentos++;
        main.removeCallbacks(reconectar);
        main.postDelayed(reconectar, espera * 1000);
    }

    private final Runnable reconectar = () -> {
        if (activo) abrir();
    };

    // ---------- MENSAJES DEL SERVIDOR ----------
    private void procesar(String texto) {
        JsonObject mensaje;
        try {
            mensaje = JsonParser.parseString(texto).getAsJsonObject();
        } catch (RuntimeException e) {
            return;
        }

        String evento = leerTexto(mensaje, "event");
        String tema = leerTexto(mensaje, "topic");
        String ref = leerTexto(mensaje, "ref");
        JsonObject payload = leerObjeto(mensaje, "payload");

        switch (evento) {
            case "phx_reply":
                if ("phoenix".equals(tema)) {
                    esperandoLatido = false;
                } else if (ref.equals(joinRef)) {
                    if ("ok".equals(leerTexto(payload, "status"))) {
                        suscrito();
                    } else {
                        conexionPerdida();
                    }
                }
                break;

            case "postgres_changes":
                JsonObject datos = leerObjeto(payload, "data");
                JsonObject registro = leerObjeto(datos, "record");
                String tabla = leerTexto(datos, "table");
                String tipo = leerTexto(datos, "type");
                if (registro.size() == 0) break;

                if ("mensajes".equals(tabla) && "INSERT".equals(tipo)) {
                    Mensaje nuevo = gson.fromJson(registro, Mensaje.class);
                    listener.onMensajeNuevo(nuevo);
                } else if ("mensajes".equals(tabla) && "UPDATE".equals(tipo)) {
                    JsonElement eliminado = registro.get("eliminado");
                    JsonElement id = registro.get("id");
                    if (eliminado != null && !eliminado.isJsonNull() && eliminado.getAsBoolean()
                            && id != null && !id.isJsonNull()) {
                        listener.onMensajeEliminado(id.getAsLong());
                    }
                } else if ("conversaciones_miembros".equals(tabla) && "UPDATE".equals(tipo)) {
                    // Mis propios cambios (yo leyendo) no afectan mis palomitas
                    if (!leerTexto(registro, "perfil_id").equals(miId)) {
                        listener.onLecturaCambiada();
                    }
                }
                break;

            case "broadcast":
                if ("escribiendo".equals(leerTexto(payload, "event"))) {
                    JsonObject aviso = leerObjeto(payload, "payload");
                    String quien = leerTexto(aviso, "perfil_id");
                    JsonElement valor = aviso.get("escribiendo");
                    boolean escribiendo = valor != null && !valor.isJsonNull() && valor.getAsBoolean();
                    if (!quien.isEmpty() && !quien.equals(miId)) {
                        listener.onEscribiendo(quien, escribiendo);
                    }
                }
                break;

            case "phx_error":
            case "phx_close":
                if (topic.equals(tema)) conexionPerdida();
                break;

            default:
                break;
        }
    }

    private void suscrito() {
        intentos = 0;
        unido = true;
        listener.onSuscrito();
        programarRenovacionToken();
    }

    // ---------- LATIDO ----------
    private void iniciarLatido() {
        esperandoLatido = false;
        main.removeCallbacks(latido);
        main.postDelayed(latido, LATIDO_MS);
    }

    private final Runnable latido = new Runnable() {
        @Override
        public void run() {
            if (esperandoLatido) {
                conexionPerdida();
                return;
            }
            esperandoLatido = true;
            enviar("phoenix", "heartbeat", new JsonObject(), siguienteRef());
            main.postDelayed(this, LATIDO_MS);
        }
    };

    // ---------- RENOVACIÓN DEL TOKEN ----------
    private void programarRenovacionToken() {
        long ahora = System.currentTimeMillis() / 1000;
        long espera = Math.max(5, sesion.getExpiraEn() - MARGEN_TOKEN_S - ahora);
        main.removeCallbacks(renovarToken);
        main.postDelayed(renovarToken, espera * 1000);
    }

    private final Runnable renovarToken = new Runnable() {
        @Override
        public void run() {
            final int mia = generacion;
            hilo.execute(() -> {
                try {
                    String token = api.obtenerTokenValido();
                    if (token != null) {
                        main.post(() -> {
                            if (mia != generacion) return;
                            JsonObject payload = new JsonObject();
                            payload.addProperty("access_token", token);
                            enviar(topic, "access_token", payload, siguienteRef());
                            programarRenovacionToken();
                        });
                    }
                } catch (IOException e) {
                    main.post(() -> {
                        if (mia == generacion) main.postDelayed(this, 10000);
                    });
                }
            });
        }
    };

    // ---------- AYUDAS ----------
    private void enviar(String tema, String evento, JsonObject payload, String ref) {
        if (socket == null) return;
        JsonObject mensaje = new JsonObject();
        mensaje.addProperty("topic", tema);
        mensaje.addProperty("event", evento);
        mensaje.add("payload", payload);
        mensaje.addProperty("ref", ref);
        socket.send(mensaje.toString());
    }

    private String siguienteRef() {
        return String.valueOf(++contadorRef);
    }

    private static String leerTexto(JsonObject objeto, String campo) {
        JsonElement e = objeto.get(campo);
        return e == null || e.isJsonNull() ? "" : e.getAsString();
    }

    private static JsonObject leerObjeto(JsonObject objeto, String campo) {
        JsonElement e = objeto.get(campo);
        return e != null && e.isJsonObject() ? e.getAsJsonObject() : new JsonObject();
    }
}