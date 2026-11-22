package com.example.fakewhatsapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

// Fase 11: buscar un texto en los mensajes de todos mis chats
public class BuscarActivity extends AppCompatActivity {

    private static final long RETARDO_BUSQUEDA_MS = 350;
    private static final int MINIMO_LETRAS = 2;

    private TextView tvVacio;
    private ProgressBar progreso;
    private ResultadosAdapter adapter;
    private MensajeService mensajes;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable busquedaPendiente;
    private int numeroBusqueda = 0;   // para ignorar respuestas viejas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        PantallaUtil.respetarBarras(this);

        TextInputEditText etBuscar = findViewById(R.id.etBuscar);
        tvVacio = findViewById(R.id.tvVacio);
        progreso = findViewById(R.id.progreso);
        RecyclerView rv = findViewById(R.id.rvResultados);

        mensajes = new MensajeService(this);

        adapter = new ResultadosAdapter(new SessionManager(this).getUserId(), this::abrirChat);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                programarBusqueda(s.toString().trim());
            }
        });

        etBuscar.requestFocus();
    }

    // Espera a que el usuario deje de escribir antes de buscar
    private void programarBusqueda(String texto) {
        if (busquedaPendiente != null) {
            handler.removeCallbacks(busquedaPendiente);
        }
        if (texto.length() < MINIMO_LETRAS) {
            numeroBusqueda++;   // descarta cualquier búsqueda en curso
            progreso.setVisibility(View.GONE);
            adapter.setResultados(null, "");
            mostrarVacio("Busca una palabra en tus chats");
            return;
        }
        busquedaPendiente = () -> buscar(texto);
        handler.postDelayed(busquedaPendiente, RETARDO_BUSQUEDA_MS);
    }

    private void buscar(String texto) {
        final int miBusqueda = ++numeroBusqueda;
        progreso.setVisibility(View.VISIBLE);

        mensajes.buscar(texto, new MensajeService.BusquedaCallback() {
            @Override
            public void onExito(List<ResultadoBusqueda> resultados) {
                if (miBusqueda != numeroBusqueda) return;
                progreso.setVisibility(View.GONE);
                adapter.setResultados(resultados, texto);
                if (resultados.isEmpty()) {
                    mostrarVacio("No hay mensajes con \"" + texto + "\"");
                } else {
                    tvVacio.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String mensaje) {
                if (miBusqueda != numeroBusqueda) return;
                progreso.setVisibility(View.GONE);
                mostrarVacio(mensaje);
            }
        });
    }

    private void mostrarVacio(String texto) {
        tvVacio.setText(texto);
        tvVacio.setVisibility(View.VISIBLE);
    }

    // Abre el chat donde está el mensaje
    private void abrirChat(ResultadoBusqueda r) {
        startActivity(ChatActivity.crearIntent(this, r.getConversacionId(), r.getTitulo(),
                r.getAvatarUrl(), r.esGrupo() ? null : r.getOtroPerfilId()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
