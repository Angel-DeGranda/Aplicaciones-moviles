package com.example.fakewhatsapp;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

public class PerfilService {

    public interface PerfilCallback {
        void onExito(Perfil perfil);
        void onError(String mensaje);
    }

    public interface ListaCallback {
        void onExito(List<Perfil> perfiles);
        void onError(String mensaje);
    }

    private static final String COLUMNAS = "id,nombre_usuario,nombre_mostrar,avatar_url,info";
    private static final Type TIPO_LISTA = new TypeToken<List<Perfil>>() { }.getType();

    private final ApiClient api;
    private final SessionManager sesion;
    private final Gson gson = new Gson();

    public PerfilService(Context context) {
        this.api = ApiClient.getInstance(context);
        this.sesion = new SessionManager(context);
    }

    // ---------- MI PERFIL ----------
    public void obtenerMiPerfil(PerfilCallback callback) {
        api.get("/rest/v1/perfiles?id=eq." + sesion.getUserId() + "&select=" + COLUMNAS,
                comoPerfil(callback));
    }

    public void actualizarPerfil(String nombreMostrar, String info, PerfilCallback callback) {
        JsonObject cuerpo = new JsonObject();
        cuerpo.addProperty("nombre_mostrar", nombreMostrar);
        cuerpo.addProperty("info", info);
        api.patch("/rest/v1/perfiles?id=eq." + sesion.getUserId(), cuerpo, comoPerfil(callback));
    }

    // Sube la foto (JPEG) y guarda su URL en perfiles.avatar_url
    public void subirAvatar(byte[] jpeg, PerfilCallback callback) {
        String uid = sesion.getUserId();
        api.subirArchivo("/storage/v1/object/avatares/" + uid + "/avatar.jpg", jpeg, "image/jpeg",
                new ApiClient.ApiCallback() {
                    @Override
                    public void onExito(JsonElement datos) {
                        // ?v=... evita que se muestre la foto anterior por el caché
                        String url = SupabaseConfig.URL + "/storage/v1/object/public/avatares/"
                                + uid + "/avatar.jpg?v=" + System.currentTimeMillis();
                        JsonObject cuerpo = new JsonObject();
                        cuerpo.addProperty("avatar_url", url);
                        api.patch("/rest/v1/perfiles?id=eq." + uid, cuerpo, comoPerfil(callback));
                    }

                    @Override
                    public void onError(String mensaje) {
                        callback.onError(mensaje);
                    }
                });
    }

    // ---------- OTROS USUARIOS ----------
    public void listarUsuarios(ListaCallback callback) {
        api.get("/rest/v1/perfiles?select=" + COLUMNAS
                        + "&id=neq." + sesion.getUserId()
                        + "&order=nombre_mostrar.asc&limit=100",
                comoLista(callback));
    }

    public void buscarUsuarios(String texto, ListaCallback callback) {
        // Quitamos los caracteres que rompen la sintaxis del filtro
        String limpio = texto.replaceAll("[,()*%\\\\\"]", "").trim();
        if (limpio.isEmpty()) {
            listarUsuarios(callback);
            return;
        }

        String patron = "*" + codificar(limpio) + "*";
        api.get("/rest/v1/perfiles?select=" + COLUMNAS
                        + "&id=neq." + sesion.getUserId()
                        + "&or=(nombre_usuario.ilike." + patron
                        + ",nombre_mostrar.ilike." + patron + ")"
                        + "&order=nombre_mostrar.asc&limit=50",
                comoLista(callback));
    }

    // ---------- AYUDAS INTERNAS ----------
    private String codificar(String texto) {
        try {
            return URLEncoder.encode(texto, "UTF-8").replace("+", "%20");
        } catch (Exception e) {
            return texto;
        }
    }

    // Convierte la respuesta (un arreglo con una fila) en un Perfil
    private ApiClient.ApiCallback comoPerfil(PerfilCallback callback) {
        return new ApiClient.ApiCallback() {
            @Override
            public void onExito(JsonElement datos) {
                if (datos.isJsonArray() && datos.getAsJsonArray().size() > 0) {
                    Perfil perfil = gson.fromJson(datos.getAsJsonArray().get(0), Perfil.class);
                    callback.onExito(perfil);
                } else {
                    callback.onError("No se encontró el perfil");
                }
            }

            @Override
            public void onError(String mensaje) {
                callback.onError(mensaje);
            }
        };
    }

    // Convierte la respuesta (un arreglo de filas) en una lista de Perfil
    private ApiClient.ApiCallback comoLista(ListaCallback callback) {
        return new ApiClient.ApiCallback() {
            @Override
            public void onExito(JsonElement datos) {
                List<Perfil> lista = gson.fromJson(datos, TIPO_LISTA);
                callback.onExito(lista);
            }

            @Override
            public void onError(String mensaje) {
                callback.onError(mensaje);
            }
        };
    }
}