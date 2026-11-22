package com.example.fakewhatsapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

// Pestaña "Contactos" de la pantalla principal (antes ContactosActivity)
public class ContactosFragment extends Fragment {

    private static final long RETARDO_BUSQUEDA_MS = 300;

    private TextView tvVacio;
    private ProgressBar progreso;

    private ContactosAdapter adapter;
    private PerfilService perfiles;
    private ConversacionService conversaciones;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable busquedaPendiente;
    private int numeroBusqueda = 0;       // sirve para ignorar respuestas viejas
    private boolean creandoChat = false;  // evita toques repetidos

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_contactos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle savedInstanceState) {
        TextInputEditText etBuscar = vista.findViewById(R.id.etBuscar);
        tvVacio = vista.findViewById(R.id.tvVacio);
        progreso = vista.findViewById(R.id.progreso);
        RecyclerView rvContactos = vista.findViewById(R.id.rvContactos);

        conversaciones = new ConversacionService(requireContext());
        perfiles = new PerfilService(requireContext());

        adapter = new ContactosAdapter(this::alSeleccionar);
        rvContactos.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvContactos.setAdapter(adapter);

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                programarBusqueda(s.toString());
            }
        });

        buscar(""); // carga inicial: todos los usuarios
    }

    // Espera 300 ms sin que el usuario escriba antes de buscar
    private void programarBusqueda(String texto) {
        if (busquedaPendiente != null) {
            handler.removeCallbacks(busquedaPendiente);
        }
        busquedaPendiente = () -> buscar(texto);
        handler.postDelayed(busquedaPendiente, RETARDO_BUSQUEDA_MS);
    }

    private void buscar(String texto) {
        final int miBusqueda = ++numeroBusqueda;
        progreso.setVisibility(View.VISIBLE);

        perfiles.buscarUsuarios(texto, new PerfilService.ListaCallback() {
            @Override
            public void onExito(List<Perfil> lista) {
                if (!isAdded() || miBusqueda != numeroBusqueda) return;
                progreso.setVisibility(View.GONE);
                adapter.setPerfiles(lista);
                actualizarVacio(texto, lista == null || lista.isEmpty());
            }

            @Override
            public void onError(String mensaje) {
                if (!isAdded() || miBusqueda != numeroBusqueda) return;
                progreso.setVisibility(View.GONE);
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarVacio(String texto, boolean vacia) {
        if (!vacia) {
            tvVacio.setVisibility(View.GONE);
            return;
        }
        tvVacio.setText(texto.trim().isEmpty()
                ? "Aún no hay otros usuarios registrados"
                : "No se encontraron usuarios");
        tvVacio.setVisibility(View.VISIBLE);
    }

    // Crea o recupera el chat directo y lo abre
    private void alSeleccionar(Perfil destinatario) {
        if (creandoChat) return;
        creandoChat = true;
        progreso.setVisibility(View.VISIBLE);

        conversaciones.obtenerOCrearDirecta(destinatario.getId(),
                new ConversacionService.IdCallback() {
                    @Override
                    public void onExito(String conversacionId) {
                        creandoChat = false;
                        if (!isAdded()) return;
                        progreso.setVisibility(View.GONE);
                        startActivity(ChatActivity.crearIntent(requireContext(),
                                conversacionId,
                                destinatario.getNombreMostrar(),
                                destinatario.getAvatarUrl(),
                                destinatario.getId()));
                    }

                    @Override
                    public void onError(String mensaje) {
                        creandoChat = false;
                        if (!isAdded()) return;
                        progreso.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
