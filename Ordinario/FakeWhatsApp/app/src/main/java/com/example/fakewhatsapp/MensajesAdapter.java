package com.example.fakewhatsapp;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.MensajeViewHolder> {

    // Fase 11: toques sobre un mensaje (los maneja ChatActivity)
    public interface Acciones {
        void onTocarFoto(Mensaje mensaje);
        void onMantenerPresionado(Mensaje mensaje);
    }

    private static final int TIPO_ENVIADO = 1;
    private static final int TIPO_RECIBIDO = 2;

    private static final Object CAMBIO_ESTADO = new Object();   // payload: solo redibujar palomitas

    private final List<Mensaje> mensajes = new ArrayList<>();
    private final String miId;
    private final Acciones acciones;

    // Hasta qué id les llegaron / leyeron mis mensajes los demás (Fase 8)
    private long entregadoId = 0;
    private long leidoId = 0;

    // Fase 11: en grupos se muestra quién mandó cada mensaje
    private boolean esGrupo = false;
    private final Map<String, String> nombres = new HashMap<>();   // perfil_id -> nombre

    public MensajesAdapter(String miId, Acciones acciones) {
        this.miId = miId;
        this.acciones = acciones;
    }

    // ---------- DATOS ----------

    // Lista inicial (orden cronológico: viejo -> nuevo)
    public void setMensajes(List<Mensaje> nuevos) {
        mensajes.clear();
        if (nuevos != null) {
            mensajes.addAll(nuevos);
        }
        notifyDataSetChanged();
    }

    // Agrega al final (mensajes nuevos o enviados). Ignora los que ya están.
    // Devuelve cuántos agregó.
    public int agregarAlFinal(List<Mensaje> nuevos) {
        if (nuevos == null || nuevos.isEmpty()) return 0;

        int antes = mensajes.size();
        long ultimo = getUltimoId();
        for (Mensaje m : nuevos) {
            if (m.getId() > ultimo) {
                mensajes.add(m);
                ultimo = m.getId();
            }
        }
        int agregados = mensajes.size() - antes;
        if (agregados > 0) {
            notifyItemRangeInserted(antes, agregados);
        }
        return agregados;
    }

    // Agrega al principio (página de mensajes anteriores). Devuelve cuántos agregó.
    public int agregarAlInicio(List<Mensaje> anteriores) {
        if (anteriores == null || anteriores.isEmpty()) return 0;

        mensajes.addAll(0, anteriores);
        notifyItemRangeInserted(0, anteriores.size());
        return anteriores.size();
    }

    // Fase 11: quita un mensaje eliminado. Devuelve true si estaba en la lista.
    public boolean quitar(long mensajeId) {
        for (int i = 0; i < mensajes.size(); i++) {
            if (mensajes.get(i).getId() == mensajeId) {
                mensajes.remove(i);
                notifyItemRemoved(i);
                return true;
            }
        }
        return false;
    }

    // Fase 11: activa los nombres en las burbujas recibidas (solo grupos)
    public void setGrupo(boolean esGrupo, Map<String, String> nombresPorId) {
        this.esGrupo = esGrupo;
        nombres.clear();
        if (nombresPorId != null) {
            nombres.putAll(nombresPorId);
        }
        notifyDataSetChanged();
    }

    // Id del mensaje más nuevo que tengo (0 si no hay ninguno)
    public long getUltimoId() {
        return mensajes.isEmpty() ? 0 : mensajes.get(mensajes.size() - 1).getId();
    }

    // Id del mensaje más viejo que tengo (0 si no hay ninguno)
    public long getPrimerId() {
        return mensajes.isEmpty() ? 0 : mensajes.get(0).getId();
    }

    // Actualiza recibido / leído (nunca retrocede)
    public void setEstadoLectura(long entregado, long leido) {
        long nuevoEntregado = Math.max(entregadoId, Math.max(entregado, leido));
        long nuevoLeido = Math.max(leidoId, leido);
        if (nuevoEntregado == entregadoId && nuevoLeido == leidoId) return;

        entregadoId = nuevoEntregado;
        leidoId = nuevoLeido;
        if (!mensajes.isEmpty()) {
            notifyItemRangeChanged(0, mensajes.size(), CAMBIO_ESTADO);
        }
    }

    // ---------- DIBUJO ----------

    // Aquí se diferencia enviado de recibido: mi id contra el remitente
    @Override
    public int getItemViewType(int position) {
        return mensajes.get(position).esMio(miId) ? TIPO_ENVIADO : TIPO_RECIBIDO;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int diseno = viewType == TIPO_ENVIADO
                ? R.layout.item_mensaje_enviado
                : R.layout.item_mensaje_recibido;
        View vista = LayoutInflater.from(parent.getContext()).inflate(diseno, parent, false);
        return new MensajeViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);

        // Nombre de quien lo mandó (solo en grupos y en mensajes de otros)
        if (holder.tvRemitente != null) {
            String nombre = esGrupo ? nombres.get(mensaje.getRemitenteId()) : null;
            holder.tvRemitente.setVisibility(nombre != null ? View.VISIBLE : View.GONE);
            holder.tvRemitente.setText(nombre);
        }

        // Foto (Fase 11)
        if (mensaje.esImagen() && mensaje.getMediaUrl() != null) {
            holder.imgFoto.setVisibility(View.VISIBLE);
            Glide.with(holder.imgFoto)
                    .load(mensaje.getMediaUrl())
                    .transform(new CenterCrop(), new RoundedCorners(24))
                    .placeholder(R.color.burbuja_recibida)
                    .error(R.color.burbuja_recibida)
                    .into(holder.imgFoto);
            holder.imgFoto.setOnClickListener(v -> acciones.onTocarFoto(mensaje));
        } else {
            Glide.with(holder.imgFoto).clear(holder.imgFoto);
            holder.imgFoto.setVisibility(View.GONE);
            holder.imgFoto.setOnClickListener(null);
        }

        // Texto (las fotos no llevan texto)
        String texto = mensaje.getContenido();
        holder.tvContenido.setVisibility(texto.isEmpty() ? View.GONE : View.VISIBLE);
        holder.tvContenido.setText(texto);

        holder.tvHora.setText(FechaUtil.formatearHoraMensaje(mensaje.getCreadoEn()));
        dibujarEstado(holder, mensaje);

        // Mantener presionado: opciones (eliminar)
        View.OnLongClickListener largo = v -> {
            acciones.onMantenerPresionado(mensaje);
            return true;
        };
        holder.itemView.setOnLongClickListener(largo);
        holder.imgFoto.setOnLongClickListener(largo);
        holder.tvContenido.setOnLongClickListener(largo);
    }

    // Si solo cambiaron las palomitas no se vuelve a dibujar todo el mensaje
    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.contains(CAMBIO_ESTADO)) {
            dibujarEstado(holder, mensajes.get(position));
        } else {
            onBindViewHolder(holder, position);
        }
    }

    // ✓ enviado · ✓✓ recibido · ✓✓ azul leído (solo en mis mensajes)
    private void dibujarEstado(MensajeViewHolder holder, Mensaje mensaje) {
        if (holder.tvEstado == null) return;

        long id = mensaje.getId();
        if (id <= leidoId) {
            holder.tvEstado.setText("✓✓");
            holder.tvEstado.setTextColor(ContextCompat.getColor(
                    holder.itemView.getContext(), R.color.tick_leido));
            holder.tvEstado.setAlpha(1f);
            holder.tvEstado.setContentDescription("Leído");
        } else {
            holder.tvEstado.setText(id <= entregadoId ? "✓✓" : "✓");
            holder.tvEstado.setTextColor(holder.colorNormal);
            holder.tvEstado.setAlpha(0.75f);
            holder.tvEstado.setContentDescription(id <= entregadoId ? "Recibido" : "Enviado");
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        final TextView tvContenido, tvHora;
        final TextView tvEstado;            // null en mensajes recibidos
        final TextView tvRemitente;         // null en mensajes enviados
        final ImageView imgFoto;
        final ColorStateList colorNormal;

        MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContenido = itemView.findViewById(R.id.tvContenido);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvRemitente = itemView.findViewById(R.id.tvRemitente);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            colorNormal = tvEstado != null ? tvEstado.getTextColors() : null;
        }
    }
}
