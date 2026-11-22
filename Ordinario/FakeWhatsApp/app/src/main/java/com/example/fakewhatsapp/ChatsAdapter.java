package com.example.fakewhatsapp;

import android.graphics.Typeface;
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

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {

    public interface OnChatClick {
        void onClick(Conversacion conversacion);
    }

    private final List<Conversacion> conversaciones = new ArrayList<>();
    private final OnChatClick listener;

    public ChatsAdapter(OnChatClick listener) {
        this.listener = listener;
    }

    public void setConversaciones(List<Conversacion> nuevas) {
        conversaciones.clear();
        if (nuevas != null) {
            conversaciones.addAll(nuevas);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Conversacion chat = conversaciones.get(position);

        holder.tvTitulo.setText(chat.getTitulo());
        holder.tvVistaPrevia.setText(chat.getVistaPrevia());
        holder.tvHora.setText(FechaUtil.formatearHoraChat(chat.getUltimoMensajeEn()));

        // "Sin mensajes todavía" va en cursiva
        holder.tvVistaPrevia.setTypeface(null,
                chat.tieneMensajes() ? Typeface.NORMAL : Typeface.ITALIC);

        Glide.with(holder.imgAvatar)
                .load(chat.getAvatarUrl())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .fallback(R.drawable.ic_avatar_placeholder)
                .circleCrop()
                .into(holder.imgAvatar);

        holder.itemView.setOnClickListener(v -> listener.onClick(chat));
    }

    @Override
    public int getItemCount() {
        return conversaciones.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgAvatar;
        final TextView tvTitulo, tvVistaPrevia, tvHora;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvVistaPrevia = itemView.findViewById(R.id.tvVistaPrevia);
            tvHora = itemView.findViewById(R.id.tvHora);
        }
    }
}