package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CicloVida";
    private static final String KEY_RESULTADO = "resultado";

    private TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText etTexto = findViewById(R.id.etTexto);
        Button btnMostrar = findViewById(R.id.btnMostrar);
        tvResultado = findViewById(R.id.tvResultado);

        // Restaurar el dato si la Activity se está recreando
        if (savedInstanceState != null) {
            tvResultado.setText(savedInstanceState.getString(KEY_RESULTADO, ""));
        }

        btnMostrar.setOnClickListener(v -> {
            String texto = etTexto.getText().toString().trim();
            if (texto.isEmpty()) {
                Toast.makeText(this, "Escribe algo primero", Toast.LENGTH_SHORT).show();
            } else {
                tvResultado.setText(texto);
            }
        });

        mostrar("onCreate");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_RESULTADO, tvResultado.getText().toString());
        mostrar("onSaveInstanceState");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mostrar("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrar("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mostrar("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mostrar("onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mostrar("onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mostrar("onDestroy");
    }

    private void mostrar(String metodo) {
        Toast.makeText(getApplicationContext(), metodo, Toast.LENGTH_SHORT).show();
        Log.d(TAG, metodo);
    }
}