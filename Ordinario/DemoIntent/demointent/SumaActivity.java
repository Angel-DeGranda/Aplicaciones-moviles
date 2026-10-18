package com.example.demointent;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SumaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suma);

        // El segundo parámetro es el valor por defecto si no llega el extra
        int a = getIntent().getIntExtra(Claves.NUM1, 0);
        int b = getIntent().getIntExtra(Claves.NUM2, 0);

        TextView tvOperacion = findViewById(R.id.tvOperacion);
        tvOperacion.setText(a + " + " + b + " = " + (a + b));

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }
}