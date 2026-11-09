package com.example.fakewhatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

public class MainActivity extends AppCompatActivity {

    private SessionManager sesion;
    private final AuthService authService = new AuthService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sesion = new SessionManager(this);

        // Protección: sin sesión no se puede estar aquí
        if (!sesion.haySesion()) {
            irALogin();
            return;
        }

        TextView tvBienvenida = findViewById(R.id.tvBienvenida);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        tvBienvenida.setText("Sesión iniciada como\n" + sesion.getCorreo());
        btnCerrarSesion.setOnClickListener(v -> {
            btnCerrarSesion.setEnabled(false);
            cerrarSesion();
        });
    }

    private void cerrarSesion() {
        authService.cerrarSesion(sesion.getAccessToken(), new AuthService.AuthCallback() {
            @Override
            public void onExito(JsonObject datos) {
                terminarSesionLocal();
            }

            @Override
            public void onError(String mensaje) {
                // Aunque el servidor falle, cerramos la sesión en el teléfono
                terminarSesionLocal();
            }
        });
    }

    private void terminarSesionLocal() {
        sesion.cerrarSesion();
        irALogin();
    }

    private void irALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}