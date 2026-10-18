package com.example.demointent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etNombre, etEdad, etNum1, etNum2;
    private CheckBox cbEstudiante;
    private TextView tvResultado;

    // Recibe el resultado que PerfilActivity devuelve con setResult()
    private final ActivityResultLauncher<Intent> perfilLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String respuesta = result.getData().getStringExtra(Claves.RESPUESTA);
                    tvResultado.setText(respuesta);
                } else {
                    tvResultado.setText("Perfil cancelado");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        etNum1 = findViewById(R.id.etNum1);
        etNum2 = findViewById(R.id.etNum2);
        cbEstudiante = findViewById(R.id.cbEstudiante);
        tvResultado = findViewById(R.id.tvResultado);

        Button btnSaludo = findViewById(R.id.btnSaludo);
        Button btnSuma = findViewById(R.id.btnSuma);
        Button btnPerfil = findViewById(R.id.btnPerfil);

        // Botón 1: Intent explícito + extra String
        btnSaludo.setOnClickListener(v -> {
            Intent intent = new Intent(this, SaludoActivity.class);
            intent.putExtra(Claves.NOMBRE, etNombre.getText().toString());
            startActivity(intent);
        });

        // Botón 2: Intent explícito + dos extras int
        btnSuma.setOnClickListener(v -> {
            Intent intent = new Intent(this, SumaActivity.class);
            intent.putExtra(Claves.NUM1, leerEntero(etNum1));
            intent.putExtra(Claves.NUM2, leerEntero(etNum2));
            startActivity(intent);
        });

        // Botón 3: extras de varios tipos + espera una respuesta
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, PerfilActivity.class);
            intent.putExtra(Claves.NOMBRE, etNombre.getText().toString());
            intent.putExtra(Claves.EDAD, leerEntero(etEdad));
            intent.putExtra(Claves.ES_ESTUDIANTE, cbEstudiante.isChecked());
            perfilLauncher.launch(intent);
        });
    }

    // Convierte el texto a int; si está vacío o es inválido devuelve 0
    private int leerEntero(EditText campo) {
        try {
            return Integer.parseInt(campo.getText().toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}