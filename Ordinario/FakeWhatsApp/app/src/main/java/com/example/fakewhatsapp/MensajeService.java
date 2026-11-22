package com.example.fakewhatsapp;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MensajeService {

    public static final int TAMANO_PAGINA = 30;
    public static final int LIMITE_CARACTERES = 2000;

    public interface MensajeCallback {
        void onExito(Mensaje mensaje);
        void onError(String texto);
    }

    public interface ListaCallback {
        void onExito(List<Mensaje> mensajes);   // siempre en orden cronológico (viejo -> nuevo)
        void onError(String texto);
    }

    // Fase 11
    public interface SimpleCallback {
        void onExito();
        void onError(String texto);
    }

    public interface BusquedaCallback {
        void onExito(List<ResultadoBusqueda> resultados);
        void onError(String texto);
    }

    private static final String COLUMNAS =
            "id,conversacion_id,remitente_id,contenido,tipo,media_url,creado_en";
    private static final Type TIPO_LISTA = new TypeToken<List<Mensaje>>() { }.getType();
    private static final Type TIPO_RESULTADOS = new TypeToken<List<ResultadoBusqueda>>() { }.getType();

    private final ApiClient api;
    private final Gson gson = new Gson();

    public MensajeService(Context context) {
        this.api = ApiClient.getInstance(context);
    }

    // ---------- ENVIAR ----------
    public void enviarTexto(String conversacionId, String texto, MensajeCallback callback) {
        String limpio = texto == null ? "" : texto.trim();
        if (limpio.isEmpty()) {
            callback.onError("Escribe un mensaje");
            return;
        }
        if (limpio.length() > LIMITE_CARACTERES) {
            callback.onError("El mensaje es demasiado largo (máximo "
                    + LIMITE_CARACTERES + " caracteres)");
            return;
        }

        // No mandamos remitente_id: la base de datos lo pone con el id del JWT
        JsonObject cuerpo = new JsonObject();
        cuerpo.addProperty("conversacion_id", conversacionId);
        cuerpo.addProperty("contenido", limpio);
        cuerpo.addProperty("tipo", "texto");
        insertar(cuerpo, callback);
    }

    // ---------- ENVIAR FOTO (Fase 11) ----------
    // 1) sube el JPEG al bucket "imagenes"  2) crea el mensaje con su URL pública
    public void enviarImagen(String conversacionId, byte[] jpeg, MensajeCallback callback) {
        String ruta = conversacionId + "/" + UUID.randomUUID() + ".jpg";

        api.subirArchivo("/storage/v1/object/imagenes/" + ruta, jpeg, "image/jpeg", false,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onExito(JsonElement datos) {
                        JsonObject cuerpo = new JsonObject();
                        cuerpo.addProperty("conversacion_id", conversacionId);
                        cuerpo.addProperty("tipo", "imagen");
                        cuerpo.addProperty("media_url", SupabaseConfig.URL
                                + "/storage/v1/object/public/imagenes/" + ruta);
                        insertar(cuerpo, callback);
                    }

                    @Override
                    public void onError(String mensaje) {
                        callback.onError("No se pudo subir la foto. " + mensaje);
                    }
                });
    }

    // ---------- ELIMINAR (Fase 11) ----------
    // "Eliminar para todos": solo funciona con mensajes propios (lo valida la base de datos)
    public void eliminar(long mensajeId, SimpleCallback callback) {
        JsonObject argumentos = new JsonObject();
        argumentos.addProperty("msg_id", mensajeId);
        api.rpc("eliminar_mensaje", argumentos, new ApiClient.ApiCallback() {
            @Override
            public void onExito(JsonElement datos) {
                callback.onExito();
            }

            @Override
            public void onError(String mensaje) {
                callback.onError(mensaje);
            }
        });
    }

    // ---------- BUSCAR (Fase 11) ----------
    // Busca el texto en todos mis chats (mínimo 2 letras). Del más nuevo al más viejo.
    public void buscar(String texto, BusquedaCallback callback) {
        JsonObject argumentos = new JsonObject();
        argumentos.addProperty("texto", texto);
        api.rpc("buscar_mensajes", argumentos, new ApiClient.ApiCallback() {
            @Override
            public void onExito(JsonElement datos) {
                List<ResultadoBusqueda> lista = gson.fromJson(datos, TIPO_RESULTADOS);
                callback.onExito(lista != null ? lista : new ArrayList<>());
            }

            @Override
            public void onError(String mensaje) {
                callback.onError(mensaje);
            }
        });
    }

    // Crea la fila en "mensajes" y devuelve el mensaje ya guardado (con su id)
    private void insertar(JsonObject cuerpo, MensajeCallback callback) {
        api.post("/rest/v1/mensajes?select=" + COLUMNAS, cuerpo, new ApiClient.ApiCallback() {
            @Override
            public void onExito(JsonElement datos) {
                if (datos.isJsonArray() && datos.getAsJsonArray().size() > 0) {
                    Mensaje creado = gson.fromJson(datos.getAsJsonArray().get(0), Mensaje.class);
                    callback.onExito(creado);
                } else {
                    callback.onError("No se pudo confirmar el envío");
                }
            }

            @Override
            public void onError(String mensaje) {
                callback.onError(mensaje);
            }
        });
    }

    // ---------- HISTORIAL ----------
    // Los últimos 30 mensajes de la conversación
    public void cargarUltimos(String conversacionId, ListaCallback callback) {
        api.get(base(conversacionId)
                        + "&order=id.desc&limit=" + TAMANO_PAGINA,
                comoLista(callback, true));
    }

    // Los 30 mensajes anteriores al mensaje con ese id (para cargar más al subir)
    public void cargarAnteriores(String conversacionId, long antesDeId, ListaCallback callback) {
        api.get(base(conversacionId)
                        + "&id=lt." + antesDeId
                        + "&order=id.desc&limit=" + TAMANO_PAGINA,
                comoLista(callback, true));
    }

    // Los mensajes más nuevos que el mensaje con ese id (refresco)
    public void cargarNuevos(String conversacionId, long despuesDeId, ListaCallback callback) {
        api.get(base(conversacionId)
                        + "&id=gt." + despuesDeId
                        + "&order=id.asc&limit=100",
                comoLista(callback, false));
    }

    // ---------- AYUDAS INTERNAS ----------
    private String base(String conversacionId) {
        return "/rest/v1/mensajes?select=" + COLUMNAS
                + "&conversacion_id=eq." + conversacionId
                + "&eliminado=eq.false";
    }

    // invertir = true cuando la consulta llega del más nuevo al más viejo
    private ApiClient.ApiCallback comoLista(ListaCallback callback, boolean invertir) {
        return new ApiClient.ApiCallback() {
            @Override
            public void onExito(JsonElement datos) {
                List<Mensaje> lista = gson.fromJson(datos, TIPO_LISTA);
                if (lista == null) {
                    lista = new ArrayList<>();
                }
                if (invertir) {
                    Collections.reverse(lista);
                }
                callback.onExito(lista);
            }

            @Override
            public void onError(String mensaje) {
                callback.onError(mensaje);
            }
        };
    }
}