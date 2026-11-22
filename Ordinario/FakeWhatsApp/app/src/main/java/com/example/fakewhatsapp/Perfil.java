package com.example.fakewhatsapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Perfil implements Serializable {

    private String id;

    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    @SerializedName("nombre_mostrar")
    private String nombreMostrar;

    @SerializedName("avatar_url")
    private String avatarUrl;

    private String info;

    public String getId()            { return id; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getNombreMostrar() { return nombreMostrar; }
    public String getAvatarUrl()     { return avatarUrl; }
    public String getInfo()          { return info; }
}