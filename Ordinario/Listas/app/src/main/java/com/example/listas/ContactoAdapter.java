package com.example.listas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder> {

    private final List<Contacto> lista;

    public ContactoAdapter(List<Contacto> lista) {
        this.lista = lista;
    }

    // ---------- ViewHolder: guarda las vistas de UNA fila ----------
    public static class ContactoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvTelefono;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
        }
    }

    // ---------- 1. Crear una fila nueva ----------
    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(vista);
    }

    // ---------- 2. Llenar una fila con el dato de la posición ----------
    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        Contacto contacto = lista.get(position);

        holder.tvNombre.setText(contacto.getNombre());
        holder.tvTelefono.setText(contacto.getTelefono());

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(v.getContext(),
                        "Seleccionaste a " + contacto.getNombre(),
                        Toast.LENGTH_SHORT).show());
    }

    // ---------- 3. Cuántos datos hay ----------
    @Override
    public int getItemCount() {
        return lista.size();
    }
}