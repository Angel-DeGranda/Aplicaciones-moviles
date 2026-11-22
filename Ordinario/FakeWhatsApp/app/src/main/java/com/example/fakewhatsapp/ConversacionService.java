package com.example.fakewhatsapp;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConversacionService {

    public interface IdCallback {
        void onExito(String conversacionId);
        void onError(String mensaje);
    }

    public interface ListaCallback {
        void onExito(List<Conversacion> conversaciones);
        void onError(String mensaje);
    }

    // Fase 11
    public interface MiembrosCallback {
        void onExito(List<Perfil> miembros);
        void onError(String mensaje);
    }

    private static final Type TIPO_LISTA = new TypeToken<List<Conversacion>>() { }.getType();

    private final ApiClient api;
    private final Gson gson = new Gson();

    public ConversacionService(Context context) {
        this.api = ApiClient.getInstance(context);
    }

    // Devuelve el id del chat directo con esa persona (lo crea si todavía no existe)
    public void obtenerOCrearDirecta(String otroPerfilId, IdCallback callback) {
        JsonObject argumentos = new JsonObject();
        argumentos.addProperty("otro_id", otroPerfilId);

        api.rpc("obtener_o_crear_conversacion_directa", argumentos, new ApiClient.ApiCallback() {
            @Override
            public void onExito(JsonElement datos) {
                // La función devuelve un solo valor (el uuid), no una lista
                if (datos.isJsonPrimitive()) {
                    callback.onExito(datos.getAsString());
                } else {
                    callback.onError("Respuesta inesperada del servidor");
                }
            }

            @Override
            public void onError(String mensaje) {
                callback.onError(mensaje);
            }
        });
    }

    // ---------- GRUPOS (Fase 11) ----------
    // Crea el grupo (yo quedo como admin) y devuelve el id de la conversación
    public void crearGrupo(String nombre, List<String> miembros, IdCallback callback) {
        JsonObject argumentos = new JsonObject();
        argumentos.addProperty("nombre_grupo", nombre);
        JsonArray ids = new JsonArray();
        for (String id : miembros) {
            ids.add(id);
        }
        argumentos.add("miembros", ids);

        api.rpc("crear_grupo", argumentos, new ApiClient.ApiCallback() {
            @Override
            public void onExito(JsonElement datos) {
                if (datos.isJsonPrimitive()) {
                    callback.onExito(datos.getAsString());
                } else {
                    callback.onError("Respuesta inesperada del servidor");
                }
            }

            @Override
            public void onError(String mensaje) {
                callback.onError(mensaje);
            }
        });
    }

    // Perfiles de todos los miembros de una conversación (para los nombres en los grupos)
    public void listarMiembros(String conversacionId, MiembrosCallback callback) {
        api.get("/rest/v1/conversaciones_miembros?conversacion_id=eq." + conversacionId
                        + "&select=perfiles(id,nombre_usuario,nombre_mostrar,avatar_url,info)",
                new ApiClient.ApiCallback() {
                    @Override
                    public void onExito(JsonElement datos) {
                        List<Perfil> lista = new ArrayList<>();
                        if (datos.isJsonArray()) {
                            for (JsonElement fila : datos.getAsJsonArray()) {
                                JsonElement perfil = fila.getAsJsonObject().get("perfiles");
                                if (perfil != null && perfil.isJsonObject()) {
                                    lista.add(gson.fromJson(perfil, Perfil.class));
                                }
                            }
                        }
                        callback.onExito(lista);
                    }

                    @Override
                    public void onError(String mensaje) {
                        callback.onError(mensaje);
                    }
                });
    }

    // Mis chats, del más reciente al más antiguo
    public void listarMisConversaciones(ListaCallback callback) {
        api.rpc("mis_conversaciones", new JsonObject(), new ApiClient.ApiCallback() {
            @Override
            public void onExito(JsonElement datos) {
                List<Conversacion> lista = gson.fromJson(datos, TIPO_LISTA);
                callback.onExito(lista != null ? lista : new ArrayList<>());
            }

            @Override
            public void onError(String mensaje) {
                callback.onError(mensaje);
            }
        });
    }
}