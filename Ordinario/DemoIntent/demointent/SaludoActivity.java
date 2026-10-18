package com.example.demointent;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SaludoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saludo);

        String nombre = getIntent().getStringExtra(Claves.NOMBRE);
        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = "desconocido";
        }

        TextView tvSaludo = findViewById(R.id.tvSaludo);
        tvSaludo.setText("¡Hola, " + nombre + "!");

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }
}