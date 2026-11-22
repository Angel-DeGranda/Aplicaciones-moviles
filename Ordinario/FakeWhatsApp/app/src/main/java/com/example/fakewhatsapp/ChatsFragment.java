package com.example.fakewhatsapp;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;

// Pestaña "Chats" de la pantalla principal (antes ChatsActivity)
public class ChatsFragment extends Fragment {

    private TextView tvVacio;
    private ProgressBar progreso;
    private ChatsAdapter adapter;
    private ConversacionService servicio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle savedInstanceState) {
        tvVacio = vista.findViewById(R.id.tvVacio);
        progreso = vista.findViewById(R.id.progreso);
        RecyclerView rvChats = vista.findViewById(R.id.rvChats);

        servicio = new ConversacionService(requireContext());

        adapter = new ChatsAdapter(this::alAbrirChat);
        rvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvChats.setAdapter(adapter);

        // "Nuevo chat" lleva a la pestaña Contactos
        vista.findViewById(R.id.btnNuevoChat).setOnClickListener(v ->
                ((MainActivity) requireActivity()).irAContactos());

        vista.findViewById(R.id.btnBuscar).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), BuscarActivity.class)));
        vista.findViewById(R.id.btnNuevoGrupo).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), NuevoGrupoActivity.class)));
    }

    // Se recarga al volver de un chat...
    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) cargarChats();
    }

    // ...y al volver a esta pestaña
    @Override
    public void onHiddenChanged(boolean oculto) {
        super.onHiddenChanged(oculto);
        if (!oculto) cargarChats();
    }

    private void cargarChats() {
        // El spinner solo si todavía no hay nada en pantalla (evita parpadeos al volver)
        if (adapter.getItemCount() == 0) {
            progreso.setVisibility(View.VISIBLE);
        }

        servicio.listarMisConversaciones(new ConversacionService.ListaCallback() {
            @Override
            public void onExito(List<Conversacion> conversaciones) {
                if (!isAdded()) return;
                progreso.setVisibility(View.GONE);
                adapter.setConversaciones(conversaciones);
                tvVacio.setVisibility(conversaciones.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String mensaje) {
                if (!isAdded()) return;
                progreso.setVisibility(View.GONE);
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void alAbrirChat(Conversacion chat) {
        startActivity(ChatActivity.crearIntent(requireContext(),
                chat.getId(), chat.getTitulo(), chat.getAvatarUrl(),
                chat.esDirecta() ? chat.getOtroPerfilId() : null));
    }
}
