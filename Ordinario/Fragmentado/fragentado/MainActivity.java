package com.example.fragentado;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

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

        Button btnPrimero = findViewById(R.id.btnPrimero);
        Button btnSegundo = findViewById(R.id.btnSegundo);
        Button btnTercero = findViewById(R.id.btnTercero);

        btnPrimero.setOnClickListener(v -> mostrarFragmento(new PrimerFragment()));
        btnSegundo.setOnClickListener(v -> mostrarFragmento(new SegundoFragment()));
        btnTercero.setOnClickListener(v -> mostrarFragmento(new TercerFragment()));

        if (savedInstanceState == null) {
            mostrarFragmento(new PrimerFragment());
        }
    }

    private void mostrarFragmento(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}