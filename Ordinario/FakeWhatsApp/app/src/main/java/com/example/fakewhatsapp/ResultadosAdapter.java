package com.example.fakewhatsapp;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
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
import java.util.Locale;

// Fase 11: resultados de la búsqueda de mensajes
public class ResultadosAdapter extends RecyclerView.Adapter<ResultadosAdapter.ResultadoViewHolder> {

    public interface OnResultadoClick {
        void onClick(ResultadoBusqueda resultado);
    }

    private final List<ResultadoBusqueda> resultados = new ArrayList<>();
    private final String miId;
    private final OnResultadoClick listener;
    private String buscado = "";

    public ResultadosAdapter(String miId, OnResultadoClick listener) {
        this.miId = miId;
        this.listener = listener;
    }

    public void setResultados(List<ResultadoBusqueda> nuevos, String texto) {
        resultados.clear();
        if (nuevos != null) {
            resultados.addAll(nuevos);
        }
        buscado = texto == null ? "" : texto.trim();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResultadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resultado, parent, false);
        return new ResultadoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultadoViewHolder holder, int position) {
        ResultadoBusqueda r = resultados.get(position);

        holder.tvTitulo.setText(r.getTitulo());
        holder.tvFecha.setText(FechaUtil.formatearHoraChat(r.getCreadoEn()));

        // "Tú: ..." o "Ana: ..." en grupos; en chats directos solo el texto si es del otro
        String prefijo = "";
        if (miId != null && miId.equals(r.getRemitenteId())) {
            prefijo = "Tú: ";
        } else if (r.esGrupo() && r.getRemitenteNombre() != null) {
            prefijo = r.getRemitenteNombre() + ": ";
        }
        holder.tvTexto.setText(resaltar(prefijo, r.getContenido()));

        Glide.with(holder.imgAvatar)
                .load(r.getAvatarUrl())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .fallback(R.drawable.ic_avatar_placeholder)
                .circleCrop()
                .into(holder.imgAvatar);

        holder.itemView.setOnClickListener(v -> listener.onClick(r));
    }

    // Pone en negritas la parte del mensaje que coincide con lo buscado
    private CharSequence resaltar(String prefijo, String contenido) {
        SpannableString texto = new SpannableString(prefijo + contenido);
        if (buscado.isEmpty()) return texto;

        String minusculas = contenido.toLowerCase(Locale.getDefault());
        String aguja = buscado.toLowerCase(Locale.getDefault());
        int desde = minusculas.indexOf(aguja);
        if (desde >= 0) {
            int inicio = prefijo.length() + desde;
            texto.setSpan(new StyleSpan(Typeface.BOLD), inicio, inicio + aguja.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return texto;
    }

    @Override
    public int getItemCount() {
        return resultados.size();
    }

    static class ResultadoViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgAvatar;
        final TextView tvTitulo, tvFecha, tvTexto;

        ResultadoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvTexto = itemView.findViewById(R.id.tvTexto);
        }
    }
}
