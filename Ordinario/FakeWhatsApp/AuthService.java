package com.example.fakewhatsapp;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthService {

    public interface AuthCallback {
        void onExito(JsonObject datos);
        void onError(String mensaje);
    }

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final Handler main = new Handler(Looper.getMainLooper());

    // ---------- REGISTRO ----------
    public void registrar(String correo, String password, String nombreUsuario,
                          String nombreMostrar, AuthCallback callback) {

        JsonObject metadatos = new JsonObject();
        metadatos.addProperty("nombre_usuario", nombreUsuario);
        metadatos.addProperty("nombre_mostrar", nombreMostrar);

        JsonObject cuerpo = new JsonObject();
        cuerpo.addProperty("email", correo);
        cuerpo.addProperty("password", password);
        cuerpo.add("data", metadatos);

        post("/auth/v1/signup", cuerpo, null, callback);
    }

    // ---------- INICIO DE SESIÓN ----------
    public void iniciarSesion(String correo, String password, AuthCallback callback) {
        JsonObject cuerpo = new JsonObject();
        cuerpo.addProperty("email", correo);
        cuerpo.addProperty("password", password);

        post("/auth/v1/token?grant_type=password", cuerpo, null, callback);
    }

    // ---------- RENOVAR SESIÓN ----------
    public void renovarSesion(String refreshToken, AuthCallback callback) {
        JsonObject cuerpo = new JsonObject();
        cuerpo.addProperty("refresh_token", refreshToken);

        post("/auth/v1/token?grant_type=refresh_token", cuerpo, null, callback);
    }

    // ---------- CIERRE DE SESIÓN ----------
    public void cerrarSesion(String accessToken, AuthCallback callback) {
        post("/auth/v1/logout", new JsonObject(), accessToken, callback);
    }

    // ---------- RECUPERAR CONTRASEÑA ----------
    // Paso 1: envía el código al correo
    public void solicitarRecuperacion(String correo, AuthCallback callback) {
        JsonObject cuerpo = new JsonObject();
        cuerpo.addProperty("email", correo);
        post("/auth/v1/recover", cuerpo, null, callback);
    }

    // Paso 2: verifica el código; la respuesta trae un access_token temporal
    public void verificarCodigoRecuperacion(String correo, String codigo, AuthCallback callback) {
        JsonObject cuerpo = new JsonObject();
        cuerpo.addProperty("type", "recovery");
        cuerpo.addProperty("email", correo);
        cuerpo.addProperty("token", codigo);
        post("/auth/v1/verify", cuerpo, null, callback);
    }

    // Paso 3: cambia la contraseña con el token temporal
    public void cambiarPassword(String accessToken, String nuevaPassword, AuthCallback callback) {
        JsonObject cuerpo = new JsonObject();
        cuerpo.addProperty("password", nuevaPassword);
        put("/auth/v1/user", cuerpo, accessToken, callback);
    }

    /// ---------- ENVÍO COMÚN ----------
    private void post(String ruta, JsonObject cuerpo, String token, AuthCallback callback) {
        enviar("POST", ruta, cuerpo, token, callback);
    }

    private void put(String ruta, JsonObject cuerpo, String token, AuthCallback callback) {
        enviar("PUT", ruta, cuerpo, token, callback);
    }

    // token = null para las rutas públicas; con valor, se manda como Bearer
    private void enviar(String metodo, String ruta, JsonObject cuerpo, String token,
                        AuthCallback callback) {
        Request.Builder builder = new Request.Builder()
                .url(SupabaseConfig.URL + ruta)
                .addHeader("apikey", SupabaseConfig.KEY)
                .method(metodo, RequestBody.create(cuerpo.toString(), JSON));

        if (token != null) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        client.newCall(builder.build()).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                main.post(() -> callback.onError("No hay conexión con el servidor"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String texto = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    try {
                        // El logout responde vacío (204): lo tratamos como objeto vacío
                        JsonObject datos = texto.trim().isEmpty()
                                ? new JsonObject()
                                : JsonParser.parseString(texto).getAsJsonObject();
                        main.post(() -> callback.onExito(datos));
                    } catch (Exception e) {
                        main.post(() -> callback.onError("Respuesta inesperada del servidor"));
                    }
                } else {
                    String mensaje = traducirError(texto);
                    main.post(() -> callback.onError(mensaje));
                }
            }
        });
    }

    // Convierte los errores de Supabase en mensajes entendibles
    private String traducirError(String texto) {
        String original = texto;
        try {
            JsonObject json = JsonParser.parseString(texto).getAsJsonObject();
            if (json.has("msg")) original = json.get("msg").getAsString();
            else if (json.has("message")) original = json.get("message").getAsString();
            else if (json.has("error_description")) original = json.get("error_description").getAsString();
        } catch (Exception ignorado) { }

        String m = original.toLowerCase();
        if (m.contains("invalid login credentials")) {
            return "Correo o contraseña incorrectos";
        }
        if (m.contains("expired or is invalid") || m.contains("otp_expired")) {
            return "Código inválido o vencido";
        }
        if (m.contains("different from the old password")) {
            return "La nueva contraseña debe ser diferente a la anterior";
        }
        if (m.contains("rate limit") || m.contains("security purposes")) {
            return "Demasiados intentos, espera unos minutos";
        }
        if (m.contains("email not confirmed")) {
            return "Debes confirmar tu correo antes de entrar";
        }
        if (m.contains("already registered") || m.contains("already exists")) {
            return "Ese correo ya está registrado";
        }
        if (m.contains("database error saving new user")) {
            return "Ese nombre de usuario ya existe";
        }
        if (m.contains("password")) {
            return "La contraseña debe tener al menos 6 caracteres";
        }
        if (m.contains("valid email") || m.contains("invalid email")) {
            return "El correo no es válido";
        }
        return "Error: " + original;
    }
}