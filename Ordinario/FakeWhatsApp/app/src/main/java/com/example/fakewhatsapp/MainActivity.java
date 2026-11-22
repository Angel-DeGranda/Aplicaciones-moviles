package com.example.fakewhatsapp;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;

// Pantalla principal estilo WhatsApp: Chats / Contactos / Perfil con barra abajo
public class MainActivity extends AppCompatActivity {

    private static final String TAG_CHATS = "chats";
    private static final String TAG_CONTACTOS = "contactos";
    private static final String TAG_PERFIL = "perfil";

    private SessionManager sesion;
    private DispositivoService dispositivos;
    private final AuthService authService = new AuthService();
    private BottomNavigationView navegacion;

    // Fase 9: permiso de notificaciones (Android 13+)
    private final ActivityResultLauncher<String> pedirPermisoNotificaciones =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
                if (!concedido) {
                    Toast.makeText(this, "No recibirás notificaciones de mensajes nuevos",
                            Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sesion = new SessionManager(this);

        // Protección: sin sesión no se puede estar aquí
        if (!sesion.haySesion()) {
            irALogin();
            return;
        }

        // Fase 9: notificaciones push
        dispositivos = new DispositivoService(this);
        dispositivos.registrar();   // obtiene el token FCM y lo guarda en Supabase
        if (savedInstanceState == null
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && !Notificaciones.tienePermiso(this)) {
            pedirPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS);
        }

        // Arriba: espacio de la barra de estado. Abajo lo maneja la barra de navegación.
        View raiz = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, 0);
            return insets;
        });

        navegacion = findViewById(R.id.navegacion);
        navegacion.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_chats) {
                mostrar(TAG_CHATS);
            } else if (id == R.id.nav_contactos) {
                mostrar(TAG_CONTACTOS);
            } else if (id == R.id.nav_perfil) {
                mostrar(TAG_PERFIL);
            }
            return true;
        });

        if (savedInstanceState == null) {
            mostrar(TAG_CHATS);   // al entrar se ve Chats, como en WhatsApp
        }

        // Atrás (botón o gesto) desde Contactos o Perfil vuelve a Chats; desde Chats sale
        volverAChats = new OnBackPressedCallback(navegacion.getSelectedItemId() != R.id.nav_chats) {
            @Override
            public void handleOnBackPressed() {
                navegacion.setSelectedItemId(R.id.nav_chats);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, volverAChats);
        navegacion.setOnItemReselectedListener(item -> { });   // tocar la pestaña actual no hace nada
    }

    private OnBackPressedCallback volverAChats;

    // ---------- PESTAÑAS ----------
    // Cada pestaña se crea una vez y luego solo se muestra / oculta (no pierde lo que tenía)
    private void mostrar(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();

        for (String otro : new String[]{TAG_CHATS, TAG_CONTACTOS, TAG_PERFIL}) {
            Fragment f = fm.findFragmentByTag(otro);
            if (f != null && !otro.equals(tag)) tx.hide(f);
        }

        Fragment elegido = fm.findFragmentByTag(tag);
        if (elegido == null) {
            tx.add(R.id.contenedor, crear(tag), tag);
        } else {
            tx.show(elegido);
        }
        tx.commit();

        if (volverAChats != null) {
            volverAChats.setEnabled(!TAG_CHATS.equals(tag));
        }
    }

    private Fragment crear(String tag) {
        switch (tag) {
            case TAG_CONTACTOS: return new ContactosFragment();
            case TAG_PERFIL:    return new PerfilFragment();
            default:            return new ChatsFragment();
        }
    }

    // "Nuevo chat" en la pestaña Chats
    public void irAContactos() {
        navegacion.setSelectedItemId(R.id.nav_contactos);
    }


    // ---------- CERRAR SESIÓN (lo llama la pestaña Perfil) ----------
    public void cerrarSesion() {
        // Primero se borra el token: este teléfono ya no debe recibir notificaciones de esta cuenta
        dispositivos.eliminar(this::cerrarSesionEnServidor);
    }

    private void cerrarSesionEnServidor() {
        authService.cerrarSesion(sesion.getAccessToken(), new AuthService.AuthCallback() {
            @Override
            public void onExito(JsonObject datos) {
                terminarSesionLocal();
            }

            @Override
            public void onError(String mensaje) {
                // Aunque el servidor falle, cerramos la sesión en el teléfono
                terminarSesionLocal();
            }
        });
    }

    private void terminarSesionLocal() {
        sesion.cerrarSesion();
        irALogin();
    }

    private void irALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
