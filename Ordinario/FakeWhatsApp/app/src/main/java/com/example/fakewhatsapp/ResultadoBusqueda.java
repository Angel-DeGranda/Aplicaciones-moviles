package com.example.fakewhatsapp;

import com.google.gson.annotations.SerializedName;

// Fase 11: una fila de la función buscar_mensajes()
public class ResultadoBusqueda {

    @SerializedName("mensaje_id")
    private long mensajeId;

    @SerializedName("conversacion_id")
    private String conversacionId;

    private String contenido;

    @SerializedName("creado_en")
    private String creadoEn;

    @SerializedName("remitente_id")
    private String remitenteId;

    @SerializedName("remitente_nombre")
    private String remitenteNombre;

    @SerializedName("conv_tipo")
    private String convTipo;

    private String titulo;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("otro_perfil_id")
    private String otroPerfilId;

    public long getMensajeId()          { return mensajeId; }
    public String getConversacionId()   { return conversacionId; }
    public String getContenido()        { return contenido == null ? "" : contenido; }
    public String getCreadoEn()         { return creadoEn; }
    public String getRemitenteId()      { return remitenteId; }
    public String getRemitenteNombre()  { return remitenteNombre; }
    public String getTitulo()           { return titulo; }
    public String getAvatarUrl()        { return avatarUrl; }
    public String getOtroPerfilId()     { return otroPerfilId; }

    public boolean esGrupo() {
        return "grupo".equals(convTipo);
    }
}
