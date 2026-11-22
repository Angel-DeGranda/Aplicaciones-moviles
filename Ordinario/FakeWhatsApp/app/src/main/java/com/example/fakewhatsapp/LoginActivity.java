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

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etCorreo, etPassword;
    private Button btnEntrar;
    private ProgressBar progreso;
    private final AuthService authService = new AuthService();
    private SessionManager sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sesion = new SessionManager(this);

        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnEntrar = findViewById(R.id.btnEntrar);
        progreso = findViewById(R.id.progreso);
        TextView tvIrRegistro = findViewById(R.id.tvIrRegistro);

        btnEntrar.setOnClickListener(v -> intentarLogin());
        tvIrRegistro.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        findViewById(R.id.tvOlvide).setOnClickListener(v ->
                startActivity(new Intent(this, RecoverActivity.class)));
    }

    private void intentarLogin() {
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Correo no válido");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Escribe tu contraseña");
            return;
        }

        cargando(true);
        authService.iniciarSesion(correo, password, new AuthService.AuthCallback() {
            @Override
            public void onExito(JsonObject datos) {
                cargando(false);
                sesion.guardarSesion(datos);
                Toast.makeText(LoginActivity.this,
                        "Sesión iniciada: " + sesion.getCorreo(), Toast.LENGTH_LONG).show();
                // Por ahora vamos a MainActivity como pantalla provisional
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(String mensaje) {
                cargando(false);
                Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargando(boolean activo) {
        progreso.setVisibility(activo ? View.VISIBLE : View.GONE);
        btnEntrar.setEnabled(!activo);
    }
}