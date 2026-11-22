package com.example.fakewhatsapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Conversacion implements Serializable {

    @SerializedName("conversacion_id")
    private String id;

    private String tipo;
    private String nombre;

    @SerializedName("imagen_url")
    private String imagenUrl;

    @SerializedName("otro_perfil_id")
    private String otroPerfilId;

    @SerializedName("otro_nombre_mostrar")
    private String otroNombreMostrar;

    @SerializedName("otro_nombre_usuario")
    private String otroNombreUsuario;

    @SerializedName("otro_avatar_url")
    private String otroAvatarUrl;

    @SerializedName("ultimo_mensaje")
    private String ultimoMensaje;

    @SerializedName("ultimo_mensaje_tipo")
    private String ultimoMensajeTipo;

    @SerializedName("ultimo_mensaje_remitente")
    private String ultimoMensajeRemitente;

    @SerializedName("ultimo_mensaje_en")
    private String ultimoMensajeEn;   // fecha en formato ISO; null si no hay mensajes

    // ---------- Datos tal cual vienen ----------
    public String getId()                      { return id; }
    public String getTipo()                    { return tipo; }
    public String getOtroPerfilId()            { return otroPerfilId; }
    public String getOtroNombreUsuario()       { return otroNombreUsuario; }
    public String getUltimoMensajeRemitente()  { return ultimoMensajeRemitente; }
    public String getUltimoMensajeEn()         { return ultimoMensajeEn; }

    // ---------- Ayudas para mostrar la fila ----------
    public boolean esDirecta() {
        return "directa".equals(tipo);
    }

    // En un chat directo el título es el nombre de la otra persona
    public String getTitulo() {
        if (esDirecta() && otroNombreMostrar != null) {
            return otroNombreMostrar;
        }
        if (nombre != null && !nombre.isEmpty()) {
            return nombre;
        }
        return "Chat";
    }

    public String getAvatarUrl() {
        return esDirecta() ? otroAvatarUrl : imagenUrl;
    }

    public boolean tieneMensajes() {
        return ultimoMensajeEn != null;
    }

    public String getVistaPrevia() {
        if (!tieneMensajes()) {
            return "Sin mensajes todavía";
        }
        if ("imagen".equals(ultimoMensajeTipo)) {
            return "\uD83D\uDCF7 Foto";
        }
        return ultimoMensaje == null ? "" : ultimoMensaje;
    }
}