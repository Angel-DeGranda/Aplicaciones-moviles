package com.example.fakewhatsapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

// Fase 9: canales de notificación y cómo se muestra un mensaje nuevo
public final class Notificaciones {

    public static final String CANAL_MENSAJES = "mensajes";   // mismo id que en el AndroidManifest
    public static final String CANAL_GENERAL = "general";

    private Notificaciones() { }

    // Android 8+ exige canales. Crearlos varias veces no pasa nada.
    public static void crearCanales(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationChannel mensajes = new NotificationChannel(CANAL_MENSAJES,
                "Mensajes", NotificationManager.IMPORTANCE_HIGH);
        mensajes.setDescription("Mensajes nuevos de tus chats");
        mensajes.enableVibration(true);
        mensajes.enableLights(true);

        NotificationChannel general = new NotificationChannel(CANAL_GENERAL,
                "General", NotificationManager.IMPORTANCE_DEFAULT);
        general.setDescription("Otros avisos de la app");

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannels(Arrays.asList(mensajes, general));
        }
    }

    public static boolean tienePermiso(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                   == PackageManager.PERMISSION_GRANTED;
    }

    // Un mensaje nuevo. Al tocarla abre ese chat (con Main detrás para poder volver).
    // Una notificación por conversación: los mensajes nuevos reemplazan al anterior.
    @SuppressLint("MissingPermission")
    public static void mostrarMensaje(Context context, String conversacionId, String titulo,
                                      String cuerpo, String avatarUrl, String otroPerfilId) {
        if (!tienePermiso(context)) return;

        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent alTocar;
        if (conversacionId != null) {
            Intent chat = ChatActivity.crearIntent(context, conversacionId, titulo,
                    avatarUrl, otroPerfilId);
            alTocar = TaskStackBuilder.create(context)
                    .addNextIntent(new Intent(context, MainActivity.class))
                    .addNextIntent(chat)
                    .getPendingIntent(idDe(conversacionId), flags);
        } else {
            Intent abrir = new Intent(context, SplashActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            alTocar = PendingIntent.getActivity(context, 0, abrir, flags);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_MENSAJES)
                .setSmallIcon(R.drawable.ic_notificacion)
                .setContentTitle(titulo)
                .setContentText(cuerpo)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(cuerpo))
                .setPriority(NotificationCompat.PRIORITY_HIGH)      // Android 7 (sin canales)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(alTocar);

        NotificationManagerCompat.from(context).notify(idDe(conversacionId), builder.build());
    }

    // Al abrir un chat se quita su notificación
    public static void cancelar(Context context, String conversacionId) {
        NotificationManagerCompat.from(context).cancel(idDe(conversacionId));
    }

    private static int idDe(String conversacionId) {
        return conversacionId == null ? 0 : conversacionId.hashCode();
    }
}
