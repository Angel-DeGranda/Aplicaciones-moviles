package com.example.fakewhatsapp;

import android.app.Activity;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Fase 11: en Android 15+ la app dibuja detrás de las barras del sistema.
// Esto agrega el espacio necesario para que nada quede tapado (ni por el teclado).
public final class PantallaUtil {

    private PantallaUtil() { }

    public static void respetarBarras(Activity activity) {
        View contenido = activity.findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(contenido, (vista, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
            vista.setPadding(barras.left, barras.top, barras.right,
                    Math.max(barras.bottom, teclado.bottom));
            return insets;
        });
    }
}
