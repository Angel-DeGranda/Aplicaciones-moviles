package com.example.fakewhatsapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Mensaje implements Serializable {

    private long id;

    @SerializedName("conversacion_id")
    private String conversacionId;

    @SerializedName("remitente_id")
    private String remitenteId;

    private String contenido;
    private String tipo;

    @SerializedName("creado_en")
    private String creadoEn;   // fecha en formato ISO

    @SerializedName("media_url")
    private String mediaUrl;   // URL pública de la foto (solo tipo "imagen")

    public long getId()                 { return id; }
    public String getMediaUrl()         { return mediaUrl; }
    public String getConversacionId()   { return conversacionId; }
    public String getRemitenteId()      { return remitenteId; }
    public String getContenido()        { return contenido == null ? "" : contenido; }
    public String getTipo()             { return tipo; }
    public String getCreadoEn()         { return creadoEn; }

    // ¿Lo mandé yo? (se compara el remitente con mi id)
    public boolean esMio(String miId) {
        return miId != null && miId.equals(remitenteId);
    }

    public boolean esImagen() {
        return "imagen".equals(tipo);
    }
}