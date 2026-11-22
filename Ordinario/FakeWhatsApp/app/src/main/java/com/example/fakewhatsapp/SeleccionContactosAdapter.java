package com.example.fakewhatsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// Fase 11: lista de contactos con casillas (para elegir participantes de un grupo)
public class SeleccionContactosAdapter
        extends RecyclerView.Adapter<SeleccionContactosAdapter.FilaViewHolder> {

    public interface OnCambio {
        void onSeleccionCambio(int cantidad);
    }

    private final List<Perfil> perfiles = new ArrayList<>();
    private final Set<String> seleccionados = new LinkedHashSet<>();
    private final OnCambio listener;

    public SeleccionContactosAdapter(OnCambio listener) {
        this.listener = listener;
    }

    public void setPerfiles(List<Perfil> nuevos) {
        perfiles.clear();
        if (nuevos != null) {
            perfiles.addAll(nuevos);
        }
        notifyDataSetChanged();
    }

    public List<String> getSeleccionados() {
        return new ArrayList<>(seleccionados);
    }

    @NonNull
    @Override
    public FilaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacto_seleccion, parent, false);
        return new FilaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull FilaViewHolder holder, int position) {
        Perfil perfil = perfiles.get(position);

        holder.tvNombre.setText(perfil.getNombreMostrar());
        holder.tvUsuario.setText("@" + perfil.getNombreUsuario());
        holder.chk.setChecked(seleccionados.contains(perfil.getId()));

        Glide.with(holder.imgAvatar)
                .load(perfil.getAvatarUrl())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .fallback(R.drawable.ic_avatar_placeholder)
                .circleCrop()
                .into(holder.imgAvatar);

        holder.itemView.setOnClickListener(v -> {
            if (!seleccionados.remove(perfil.getId())) {
                seleccionados.add(perfil.getId());
            }
            holder.chk.setChecked(seleccionados.contains(perfil.getId()));
            listener.onSeleccionCambio(seleccionados.size());
        });
    }

    @Override
    public int getItemCount() {
        return perfiles.size();
    }

    static class FilaViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgAvatar;
        final TextView tvNombre, tvUsuario;
        final CheckBox chk;

        FilaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            chk = itemView.findViewById(R.id.chkSeleccion);
        }
    }
}
