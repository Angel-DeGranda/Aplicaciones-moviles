package com.example.fakewhatsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ContactosAdapter extends RecyclerView.Adapter<ContactosAdapter.ContactoViewHolder> {

    public interface OnContactoClick {
        void onClick(Perfil perfil);
    }

    private final List<Perfil> perfiles = new ArrayList<>();
    private final OnContactoClick listener;

    public ContactosAdapter(OnContactoClick listener) {
        this.listener = listener;
    }

    public void setPerfiles(List<Perfil> nuevos) {
        perfiles.clear();
        if (nuevos != null) {
            perfiles.addAll(nuevos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        Perfil perfil = perfiles.get(position);

        holder.tvNombre.setText(perfil.getNombreMostrar());
        holder.tvUsuario.setText("@" + perfil.getNombreUsuario());

        Glide.with(holder.imgAvatar)
                .load(perfil.getAvatarUrl())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .fallback(R.drawable.ic_avatar_placeholder)
                .circleCrop()
                .into(holder.imgAvatar);

        holder.itemView.setOnClickListener(v -> listener.onClick(perfil));
    }

    @Override
    public int getItemCount() {
        return perfiles.size();
    }

    static class ContactoViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgAvatar;
        final TextView tvNombre, tvUsuario;

        ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
        }
    }
}