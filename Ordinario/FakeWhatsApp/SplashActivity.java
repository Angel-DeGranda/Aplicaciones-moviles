package com.example.fakewhatsapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

public class SplashActivity extends AppCompatActivity {

    private SessionManager sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sesion = new SessionManager(this);

        if (!sesion.haySesion()) {
            irA(LoginActivity.class);
        } else if (!sesion.tokenExpirado()) {
            irA(MainActivity.class);
        } else {
            renovarYEntrar();
        }
    }

    private void renovarYEntrar() {
        new AuthService().renovarSesion(sesion.getRefreshToken(), new AuthService.AuthCallback() {
            @Override
            public void onExito(JsonObject datos) {
                sesion.guardarSesion(datos); // guarda también el refresh token nuevo
                irA(MainActivity.class);
            }

            @Override
            public void onError(String mensaje) {
                sesion.cerrarSesion();
                irA(LoginActivity.class);
            }
        });
    }

    private void irA(Class<?> destino) {
        startActivity(new Intent(this, destino));
        finish();
    }
}