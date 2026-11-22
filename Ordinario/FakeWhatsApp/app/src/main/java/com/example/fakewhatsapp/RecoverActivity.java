package com.example.fakewhatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

public class RecoverActivity extends AppCompatActivity {

    private View etapa1, etapa2;
    private TextInputEditText etCorreo, etCodigo, etNuevaPassword;
    private Button btnEnviarCodigo, btnCambiar;
    private TextView tvAviso;
    private ProgressBar progreso;
    private final AuthService authService = new AuthService();

    private String correo; // el correo al que se envió el código

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);

        etapa1 = findViewById(R.id.etapa1);
        etapa2 = findViewById(R.id.etapa2);
        etCorreo = findViewById(R.id.etCorreo);
        etCodigo = findViewById(R.id.etCodigo);
        etNuevaPassword = findViewById(R.id.etNuevaPassword);
        btnEnviarCodigo = findViewById(R.id.btnEnviarCodigo);
        btnCambiar = findViewById(R.id.btnCambiar);
        tvAviso = findViewById(R.id.tvAviso);
        progreso = findViewById(R.id.progreso);

        btnEnviarCodigo.setOnClickListener(v -> enviarCodigo());
        btnCambiar.setOnClickListener(v -> cambiarPassword());
        findViewById(R.id.tvVolver).setOnClickListener(v -> irALogin());
    }

    // ETAPA 1: pedir el código
    private void enviarCodigo() {
        String correoEscrito = etCorreo.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(correoEscrito).matches()) {
            etCorreo.setError("Correo no válido");
            return;
        }

        cargando(true);
        authService.solicitarRecuperacion(correoEscrito, new AuthService.AuthCallback() {
            @Override
            public void onExito(JsonObject datos) {
                cargando(false);
                correo = correoEscrito;
                tvAviso.setText("Te enviamos un código a " + correo);
                etapa1.setVisibility(View.GONE);
                etapa2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String mensaje) {
                cargando(false);
                Toast.makeText(RecoverActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    // ETAPA 2: verificar el código y cambiar la contraseña
    private void cambiarPassword() {
        String codigo = etCodigo.getText().toString().trim();
        String nueva = etNuevaPassword.getText().toString();

        if (codigo.length() < 6) {
            etCodigo.setError("Escribe el código completo");
            return;
        }
        if (nueva.length() < 6) {
            etNuevaPassword.setError("Mínimo 6 caracteres");
            return;
        }

        cargando(true);
        authService.verificarCodigoRecuperacion(correo, codigo, new AuthService.AuthCallback() {
            @Override
            public void onExito(JsonObject datos) {
                // El código es válido: usamos el token temporal para cambiar la contraseña
                String tokenTemporal = datos.get("access_token").getAsString();
                guardarNuevaPassword(tokenTemporal, nueva);
            }

            @Override
            public void onError(String mensaje) {
                cargando(false);
                Toast.makeText(RecoverActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void guardarNuevaPassword(String tokenTemporal, String nueva) {
        authService.cambiarPassword(tokenTemporal, nueva, new AuthService.AuthCallback() {
            @Override
            public void onExito(JsonObject datos) {
                cargando(false);
                Toast.makeText(RecoverActivity.this,
                        "Contraseña actualizada. Inicia sesión.", Toast.LENGTH_LONG).show();
                irALogin();
            }

            @Override
            public void onError(String mensaje) {
                cargando(false);
                Toast.makeText(RecoverActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargando(boolean activo) {
        progreso.setVisibility(activo ? View.VISIBLE : View.GONE);
        btnEnviarCodigo.setEnabled(!activo);
        btnCambiar.setEnabled(!activo);
    }

    private void irALogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}