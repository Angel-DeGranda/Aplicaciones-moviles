package com.example.fakewhatsapp;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

// Fase 8: recibido / leído, última conexión
public class EstadoService {

    public interface LecturaCallback {
        // Hasta qué id les llegó / leyeron los demás miembros del chat
        void onExito(long entregadoId, long leidoId);
    }

    public interface ContactoCallback {
        void onExito(boolean enLinea, String ultimaConexion);
    }

    private final ApiClient api;

    public EstadoService(Context context) {
        this.api = ApiClient.getInstance(context);
    }

    // Marca "en línea" y que me llegaron los mensajes de todos mis chats
    public void latido() {
        api.rpc("latido", new JsonObject(), ignorarRespuesta());
    }

    // Llegó una notificación push: ✓✓ recibido sin ponerme "en línea" (Fase 9)
    public void marcarTodoEntregado() {
        api.rpc("marcar_todo_entregado", new JsonObject(), ignorarRespuesta());
    }

    // La app pasó a segundo plano
    public void salir() {
        api.rpc("salir", new JsonObject(), ignorarRespuesta());
    }

    public void marcarLeido(String conversacionId, long hastaId) {
        JsonObject argumentos = new JsonObject();
        argumentos.addProperty("conv_id", conversacionId);
        argumentos.addProperty("hasta_id", hastaId);
        api.rpc("marcar_leido", argumentos, ignorarRespuesta());
    }

    public void estadoLectura(String conversacionId, LecturaCallback callback) {
        JsonObject argumentos = new JsonObject();
        argumentos.addProperty("conv_id", conversacionId);
        api.rpc("estado_lectura", argumentos, new ApiClient.ApiCallback() {
            @Override
            public void onExito(JsonElement datos) {
                if (!datos.isJsonObject()) return;
                JsonObject o = datos.getAsJsonObject();
                callback.onExito(leerLong(o, "entregado_id"), leerLong(o, "leido_id"));
            }

            @Override
            public void onError(String mensaje) { }
        });
    }

    public void estadoContacto(String otroId, ContactoCallback callback) {
        JsonObject argumentos = new JsonObject();
        argumentos.addProperty("otro_id", otroId);
        api.rpc("estado_contacto", argumentos, new ApiClient.ApiCallback() {
            @Override
            public void onExito(JsonElement datos) {
                if (!datos.isJsonObject()) return;
                JsonObject o = datos.getAsJsonObject();
                boolean enLinea = o.has("en_linea") && !o.get("en_linea").isJsonNull()
                        && o.get("en_linea").getAsBoolean();
                String ultima = o.has("ultima_conexion") && !o.get("ultima_conexion").isJsonNull()
                        ? o.get("ultima_conexion").getAsString()
                        : null;
                callback.onExito(enLinea, ultima);
            }

            @Override
            public void onError(String mensaje) { }
        });
    }

    // ---------- AYUDAS ----------
    private static long leerLong(JsonObject o, String campo) {
        JsonElement e = o.get(campo);
        return e == null || e.isJsonNull() ? 0 : e.getAsLong();
    }

    // Estas llamadas son en segundo plano: si fallan, se reintentan solas en el siguiente latido
    private static ApiClient.ApiCallback ignorarRespuesta() {
        return new ApiClient.ApiCallback() {
            @Override
            public void onExito(JsonElement datos) { }

            @Override
            public void onError(String mensaje) { }
        };
    }
}
