package com.example.fakewhatsapp;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

// Fase 9: recibe los mensajes push de Firebase Cloud Messaging.
//
// Formato que debe mandar el servidor (mensaje "data", para que llegue aquí
// también con la app en segundo plano):
//   {
//     "conversacion_id": "uuid",
//     "titulo":          "Nombre de quien escribe (o del grupo)",
//     "cuerpo":          "Texto del mensaje",
//     "avatar_url":      "https://... (opcional)",
//     "otro_perfil_id":  "uuid de quien escribe (solo chats directos, opcional)"
//   }
public class NotificacionesService extends FirebaseMessagingService {

    // Firebase cambió el token (primera instalación, datos borrados, etc.)
    @Override
    public void onNewToken(@NonNull String token) {
        new DispositivoService(this).guardarToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage mensaje) {
        Map<String, String> datos = mensaje.getData();
        RemoteMessage.Notification aviso = mensaje.getNotification();

        String conversacionId = datos.get("conversacion_id");
        String titulo = primero(datos.get("titulo"), aviso != null ? aviso.getTitle() : null, "Nuevo mensaje");
        String cuerpo = primero(datos.get("cuerpo"), aviso != null ? aviso.getBody() : null, "");

        // El mensaje llegó al teléfono: ✓✓ recibido (Fase 8)
        if (new SessionManager(this).haySesion()) {
            new EstadoService(this).marcarTodoEntregado();
        }

        // Si ese chat está abierto en pantalla, no hace falta notificar
        if (conversacionId != null && conversacionId.equals(ChatActivity.conversacionVisible)) {
            return;
        }

        Notificaciones.mostrarMensaje(this, conversacionId, titulo, cuerpo,
                datos.get("avatar_url"), datos.get("otro_perfil_id"));
    }

    private static String primero(String... opciones) {
        for (String o : opciones) {
            if (o != null && !o.isEmpty()) return o;
        }
        return "";
    }
}
