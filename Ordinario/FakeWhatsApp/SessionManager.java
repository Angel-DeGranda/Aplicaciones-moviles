package com.example.fakewhatsapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;

public class SessionManager {

    private static final String PREFS = "sesion";
    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    // Guarda la respuesta del login de Supabase
    public void guardarSesion(JsonObject datos) {
        JsonObject usuario = datos.getAsJsonObject("user");

        long expiraEn;
        if (datos.has("expires_at")) {
            expiraEn = datos.get("expires_at").getAsLong();
        } else {
            expiraEn = System.currentTimeMillis() / 1000
                    + datos.get("expires_in").getAsLong();
        }

        prefs.edit()
                .putString("access_token", datos.get("access_token").getAsString())
                .putString("refresh_token", datos.get("refresh_token").getAsString())
                .putLong("expira_en", expiraEn)
                .putString("user_id", usuario.get("id").getAsString())
                .putString("correo", usuario.get("email").getAsString())
                .apply();
    }

    public boolean haySesion() {
        return prefs.getString("access_token", null) != null;
    }

    // Verdadero si el access token ya venció (o vence en menos de 30 segundos)
    public boolean tokenExpirado() {
        long ahora = System.currentTimeMillis() / 1000;
        return ahora >= prefs.getLong("expira_en", 0) - 30;
    }

    public String getAccessToken()  { return prefs.getString("access_token", null); }
    public String getRefreshToken() { return prefs.getString("refresh_token", null); }
    public String getUserId()       { return prefs.getString("user_id", null); }
    public String getCorreo()       { return prefs.getString("correo", null); }

    public void cerrarSesion() {
        prefs.edit().clear().apply();
    }
}