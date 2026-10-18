package com.example.demointent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Intent recibido = getIntent();
        String nombre = recibido.getStringExtra(Claves.NOMBRE);
        int edad = recibido.getIntExtra(Claves.EDAD, 0);
        boolean esEstudiante = recibido.getBooleanExtra(Claves.ES_ESTUDIANTE, false);

        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = "desconocido";
        }

        TextView tvPerfil = findViewById(R.id.tvPerfil);
        tvPerfil.setText("Nombre: " + nombre
                + "\nEdad: " + edad
                + "\nEstudiante: " + (esEstudiante ? "Sí" : "No"));

        String nombreFinal = nombre;
        findViewById(R.id.btnConfirmar).setOnClickListener(v -> {
            // Devolvemos datos a MainActivity
            Intent resultado = new Intent();
            resultado.putExtra(Claves.RESPUESTA, "Perfil de " + nombreFinal + " confirmado");
            setResult(RESULT_OK, resultado);
            finish();
        });
    }
}