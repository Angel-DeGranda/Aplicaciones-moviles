package com.example.fakewhatsapp;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import android.content.Intent;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNombreMostrar, etNombreUsuario, etCorreo, etPassword;
    private Button btnRegistrar;
    private ProgressBar progreso;
    private final AuthService authService = new AuthService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombreMostrar = findViewById(R.id.etNombreMostrar);
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        progreso = findViewById(R.id.progreso);

        btnRegistrar.setOnClickListener(v -> intentarRegistro());

        findViewById(R.id.tvIrLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void intentarRegistro() {
        String nombreMostrar = etNombreMostrar.getText().toString().trim();
        String nombreUsuario = etNombreUsuario.getText().toString().trim().toLowerCase();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (nombreMostrar.isEmpty()) {
            etNombreMostrar.setError("Escribe tu nombre");
            return;
        }
        if (!nombreUsuario.matches("^[a-z0-9_]{3,20}$")) {
            etNombreUsuario.setError("3 a 20 caracteres: letras, números o _");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Correo no válido");
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Mínimo 6 caracteres");
            return;
        }

        cargando(true);
        authService.registrar(correo, password, nombreUsuario, nombreMostrar,
                new AuthService.AuthCallback() {
                    @Override
                    public void onExito(JsonObject datos) {
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(String mensaje) {
                        cargando(false);
                        Toast.makeText(RegisterActivity.this, mensaje, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void cargando(boolean activo) {
        progreso.setVisibility(activo ? View.VISIBLE : View.GONE);
        btnRegistrar.setEnabled(!activo);
    }
}