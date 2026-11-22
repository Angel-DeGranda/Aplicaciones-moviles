package com.example.fakewhatsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


// Fase 9: guarda el token FCM de este teléfono en la tabla "dispositivos"
public class DispositivoService {

    private static final String TAG = "DispositivoService";
    private static final String PREFS = "fcm";            // aparte de "sesion": sobrevive al cerrar sesión
    private static final String CLAVE_TOKEN = "token";

    private final ApiClient api;
    private final SessionManager sesion;
    private final SharedPreferences prefs;

    public DispositivoService(Context context) {
        this.api = ApiClient.getInstance(context);
        this.sesion = new SessionManager(context);
        this.prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    // Pide el token a Firebase y lo registra (llamar con la sesión ya iniciada)
    public void registrar() {
        if (!sesion.haySesion()) return;

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tarea -> {
            if (!tarea.isSuccessful() || tarea.getResult() == null) {
                Log.w(TAG, "No se pudo obtener el token FCM", tarea.getException());
                return;
            }
            guardarToken(tarea.getResult());
        });
    }

    // Guarda (o actualiza) el token en Supabase.
    // Si el token ya estaba con otro usuario en este teléfono, pasa al usuario actual.
    public void guardarToken(String token) {
        prefs.edit().putString(CLAVE_TOKEN, token).apply();
        if (!sesion.haySesion()) return;   // se registrará al iniciar sesión

        // Fase 12: la tabla está protegida con RLS; se registra con una función
        // que usa el usuario de la sesión (no se puede registrar a nombre de otro)
        JsonObject argumentos = new JsonObject();
        argumentos.addProperty("token", token);
        argumentos.addProperty("modelo_equipo", Build.MANUFACTURER + " " + Build.MODEL);

        api.rpc("registrar_dispositivo", argumentos,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onExito(JsonElement datos) {
                        Log.d(TAG, "Token FCM registrado en Supabase");
                    }

                    @Override
                    public void onError(String mensaje) {
                        Log.w(TAG, "No se pudo registrar el token: " + mensaje);
                    }
                });
    }

    // Al cerrar sesión: este teléfono ya no debe recibir notificaciones de esa cuenta.
    // alTerminar se llama siempre (aunque falle), en el hilo principal.
    public void eliminar(Runnable alTerminar) {
        String token = prefs.getString(CLAVE_TOKEN, null);
        if (token == null || !sesion.haySesion()) {
            alTerminar.run();
            return;
        }

        JsonObject argumentos = new JsonObject();
        argumentos.addProperty("token", token);

        api.rpc("eliminar_dispositivo", argumentos,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onExito(JsonElement datos) {
                        alTerminar.run();
                    }

                    @Override
                    public void onError(String mensaje) {
                        Log.w(TAG, "No se pudo borrar el token: " + mensaje);
                        alTerminar.run();
                    }
                });
    }
}
