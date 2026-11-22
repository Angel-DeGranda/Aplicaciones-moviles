package com.example.listas;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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

        // 1. Datos
        List<Contacto> contactos = new ArrayList<>();
        contactos.add(new Contacto("Ana López", "229 111 1111"));
        contactos.add(new Contacto("Carlos Ruiz", "229 222 2222"));
        contactos.add(new Contacto("María Pérez", "229 333 3333"));
        contactos.add(new Contacto("Luis Hernández", "229 444 4444"));
        contactos.add(new Contacto("Sofía Torres", "229 555 5555"));
        contactos.add(new Contacto("Jorge Díaz", "229 666 6666"));
        contactos.add(new Contacto("Laura Gómez", "229 777 7777"));
        contactos.add(new Contacto("Pedro Castro", "229 888 8888"));
        contactos.add(new Contacto("Elena Morales", "229 999 9999"));
        contactos.add(new Contacto("Diego Vargas", "229 101 0101"));
        contactos.add(new Contacto("Paula Ríos", "229 202 0202"));
        contactos.add(new Contacto("Miguel Ortiz", "229 303 0303"));
        contactos.add(new Contacto("Andrea Silva", "229 404 0404"));
        contactos.add(new Contacto("Raúl Mendoza", "229 505 0505"));

        // 2. RecyclerView
        RecyclerView recycler = findViewById(R.id.recyclerContactos);

        // 3. Posicionamiento (LayoutManager): lista vertical
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // 4. Adapter
        recycler.setAdapter(new ContactoAdapter(contactos));
    }
}