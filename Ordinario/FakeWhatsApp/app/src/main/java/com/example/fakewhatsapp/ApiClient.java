package com.example.fakewhatsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    public interface ApiCallback {
        void onExito(JsonElement datos);   // puede ser un objeto, un arreglo o JsonNull
        void onError(String mensaje);
    }

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static ApiClient instancia;

    public static synchronized ApiClient getInstance(Context context) {
        if (instancia == null) {
            instancia = new ApiClient(context.getApplicationContext());
        }
        return instancia;
    }

    private final Context context;
    private final SessionManager sesion;
    private final OkHttpClient client = new OkHttpClient();
    private final ExecutorService hilos = Executors.newFixedThreadPool(4);
    private final Handler main = new Handler(Looper.getMainLooper());
    private final Object lockRenovar = new Object();

    private ApiClient(Context context) {
        this.context = context;
        this.sesion = new SessionManager(context);
    }

    // ---------- MÉTODOS PÚBLICOS ----------
    // La ruta empieza con "/", por ejemplo: "/rest/v1/perfiles?select=*"

    public void get(String ruta, ApiCallback callback) {
        ejecutar("GET", ruta, null, null, callback);
    }

    public void post(String ruta, JsonObject cuerpo, ApiCallback callback) {
        ejecutar("POST", ruta, RequestBody.create(cuerpo.toString(), JSON),
                conRepresentacion(), callback);
    }

    public void patch(String ruta, JsonObject cuerpo, ApiCallback callback) {
        ejecutar("PATCH", ruta, RequestBody.create(cuerpo.toString(), JSON),
                conRepresentacion(), callback);
    }

    public void delete(String ruta, ApiCallback callback) {
        ejecutar("DELETE", ruta, null, null, callback);
    }

    // Inserta, o actualiza si ya existe (la ruta debe llevar ?on_conflict=columna_unica)
    public void upsert(String ruta, JsonObject cuerpo, ApiCallback callback) {
        Map<String, String> extra = Collections.singletonMap("Prefer",
                "resolution=merge-duplicates,return=minimal");
        ejecutar("POST", ruta, RequestBody.create(cuerpo.toString(), JSON), extra, callback);
    }

    // Sube un archivo (por ejemplo una imagen) a Supabase Storage
    public void subirArchivo(String ruta, byte[] contenido, String tipoMime, ApiCallback callback) {
        subirArchivo(ruta, contenido, tipoMime, true, callback);
    }

    // reemplazar = false: el archivo es nuevo y no se sobrescribe nada (fotos del chat)
    public void subirArchivo(String ruta, byte[] contenido, String tipoMime, boolean reemplazar,
                             ApiCallback callback) {
        Map<String, String> extra = reemplazar
                ? Collections.singletonMap("x-upsert", "true")
                : null;
        ejecutar("POST", ruta, RequestBody.create(contenido, MediaType.get(tipoMime)),
                extra, callback);
    }

    // Llama a una función de la base de datos (RPC), por ejemplo "mis_conversaciones"
    public void rpc(String funcion, JsonObject argumentos, ApiCallback callback) {
        ejecutar("POST", "/rest/v1/rpc/" + funcion,
                RequestBody.create(argumentos.toString(), JSON), null, callback);
    }

    // Pide a la base de datos que devuelva la fila creada o modificada
    private Map<String, String> conRepresentacion() {
        return Collections.singletonMap("Prefer", "return=representation");
    }

    // ---------- NÚCLEO ----------
    private void ejecutar(String metodo, String ruta, RequestBody cuerpo,
                          Map<String, String> extra, ApiCallback callback) {
        hilos.execute(() -> {
            try {
                if (!sesion.haySesion()) {
                    sesionExpirada();
                    return;
                }

                // 1) Si el token ya venció, lo renovamos antes de pedir nada
                if (sesion.tokenExpirado() && !renovarToken(sesion.getAccessToken())) {
                    sesionExpirada();
                    return;
                }

                // 2) Primera llamada
                String token = sesion.getAccessToken();
                Response respuesta = llamar(metodo, ruta, cuerpo, extra, token);

                // 3) Si el servidor rechaza el token (401), renovamos una vez y reintentamos
                if (respuesta.code() == 401) {
                    respuesta.close();
                    if (!renovarToken(token)) {
                        sesionExpirada();
                        return;
                    }
                    respuesta = llamar(metodo, ruta, cuerpo, extra, sesion.getAccessToken());
                }

                procesar(respuesta, callback);

            } catch (IOException e) {
                main.post(() -> callback.onError("No hay conexión con el servidor"));
            } catch (Exception e) {
                main.post(() -> callback.onError("Respuesta inesperada del servidor"));
            }
        });
    }

    private Response llamar(String metodo, String ruta, RequestBody cuerpo,
                            Map<String, String> extra, String token) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(SupabaseConfig.URL + ruta)
                .addHeader("apikey", SupabaseConfig.KEY)
                .addHeader("Authorization", "Bearer " + token)
                .method(metodo, cuerpo);

        if (extra != null) {
            for (Map.Entry<String, String> h : extra.entrySet()) {
                builder.addHeader(h.getKey(), h.getValue());
            }
        }
        return client.newCall(builder.build()).execute();
    }

    private void procesar(Response respuesta, ApiCallback callback) throws IOException {
        try (Response r = respuesta) {
            String texto = r.body() != null ? r.body().string() : "";
            if (r.isSuccessful()) {
                JsonElement datos = texto.trim().isEmpty()
                        ? JsonNull.INSTANCE
                        : JsonParser.parseString(texto);
                main.post(() -> callback.onExito(datos));
            } else {
                String mensaje = traducirError(texto, r.code());
                main.post(() -> callback.onError(mensaje));
            }
        }
    }

    // ---------- RENOVACIÓN DEL TOKEN ----------
    // tokenQueFallo: el token que usamos en la petición que nos hizo renovar.
    // Devuelve true si la sesión quedó renovada, false si el refresh token ya no sirve.
    // Lanza IOException si no hay conexión (en ese caso NO se cierra la sesión).
    private boolean renovarToken(String tokenQueFallo) throws IOException {
        synchronized (lockRenovar) {
            // Si otro hilo ya renovó mientras esperábamos, no repetimos (el refresh token es de un solo uso)
            if (!tokenQueFallo.equals(sesion.getAccessToken())) {
                return true;
            }

            String refresh = sesion.getRefreshToken();
            if (refresh == null) {
                return false;
            }

            JsonObject cuerpo = new JsonObject();
            cuerpo.addProperty("refresh_token", refresh);

            Request request = new Request.Builder()
                    .url(SupabaseConfig.URL + "/auth/v1/token?grant_type=refresh_token")
                    .addHeader("apikey", SupabaseConfig.KEY)
                    .post(RequestBody.create(cuerpo.toString(), JSON))
                    .build();

            try (Response r = client.newCall(request).execute()) {
                if (r.code() >= 500) {
                    throw new IOException("Servidor no disponible");
                }
                if (!r.isSuccessful() || r.body() == null) {
                    return false;
                }
                try {
                    JsonObject datos = JsonParser.parseString(r.body().string()).getAsJsonObject();
                    sesion.guardarSesion(datos);
                    return true;
                } catch (RuntimeException e) {
                    return false;
                }
            }
        }
    }

    // Devuelve un access token vigente, renovándolo si hace falta. Llamar desde un hilo secundario.
// Devuelve null si la sesión ya no se puede recuperar (en ese caso manda a Login).
// Lanza IOException si no hay conexión (la sesión NO se cierra).
    public String obtenerTokenValido() throws IOException {
        if (!sesion.haySesion()) {
            return null;
        }
        if (sesion.tokenExpirado() && !renovarToken(sesion.getAccessToken())) {
            sesionExpirada();
            return null;
        }
        return sesion.getAccessToken();
    }

    // La sesión ya no se puede recuperar: se borra y se manda a Login
    private void sesionExpirada() {
        sesion.cerrarSesion();
        main.post(() -> {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });
    }

    // ---------- ERRORES ----------
    private String traducirError(String texto, int codigo) {
        String mensaje = texto;
        try {
            JsonObject json = JsonParser.parseString(texto).getAsJsonObject();
            if (json.has("code") && "23505".equals(json.get("code").getAsString())) {
                return "Ese dato ya está en uso";
            }
            if (json.has("message")) {
                mensaje = json.get("message").getAsString();
            } else if (json.has("msg")) {
                mensaje = json.get("msg").getAsString();
            }
        } catch (Exception ignorado) { }
        return "Error " + codigo + ": " + mensaje;
    }
}