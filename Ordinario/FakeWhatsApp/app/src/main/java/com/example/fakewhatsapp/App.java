package com.example.fakewhatsapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// Fase 8: mientras la app está a la vista manda un "latido" cada 20 s
// (en línea + recibido). Al pasar a segundo plano guarda la última conexión.
public class App extends Application {

    private static final long LATIDO_MS = 20000;
    private static final long ESPERA_SALIR_MS = 1500;   // evita "salir" al girar la pantalla

    private final Handler main = new Handler(Looper.getMainLooper());
    private SessionManager sesion;
    private EstadoService estado;

    private int visibles = 0;
    private boolean enPrimerPlano = false;

    @Override
    public void onCreate() {
        super.onCreate();
        sesion = new SessionManager(this);
        estado = new EstadoService(this);

        // Fase 9: los canales deben existir antes de mostrar cualquier notificación
        Notificaciones.crearCanales(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                visibles++;
                main.removeCallbacks(salir);
                if (!enPrimerPlano) {
                    enPrimerPlano = true;
                    main.removeCallbacks(latido);
                    latido.run();
                }
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                visibles = Math.max(0, visibles - 1);
                if (visibles == 0) {
                    main.postDelayed(salir, ESPERA_SALIR_MS);
                }
            }

            @Override public void onActivityCreated(@NonNull Activity a, @Nullable Bundle b) { }
            @Override public void onActivityResumed(@NonNull Activity a) { }
            @Override public void onActivityPaused(@NonNull Activity a) { }
            @Override public void onActivitySaveInstanceState(@NonNull Activity a, @NonNull Bundle b) { }
            @Override public void onActivityDestroyed(@NonNull Activity a) { }
        });
    }

    private final Runnable latido = new Runnable() {
        @Override
        public void run() {
            // Sin sesión no se llama (ApiClient mandaría a Login)
            if (sesion.haySesion()) {
                estado.latido();
            }
            main.postDelayed(this, LATIDO_MS);
        }
    };

    private final Runnable salir = () -> {
        enPrimerPlano = false;
        main.removeCallbacks(latido);
        if (sesion.haySesion()) {
            estado.salir();
        }
    };
}
