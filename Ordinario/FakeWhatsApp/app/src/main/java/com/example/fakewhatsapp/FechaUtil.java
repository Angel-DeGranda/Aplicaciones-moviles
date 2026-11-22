package com.example.fakewhatsapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FechaUtil {

    // Convierte "2026-11-09T20:15:30.123456+00:00" en una fecha. Devuelve null si no se puede.
    public static Date parsear(String iso) {
        if (iso == null) return null;
        try {
            String limpio = iso.replaceAll("\\.\\d+", "")   // quita las fracciones de segundo
                    .replace("Z", "+00:00");
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
            return formato.parse(limpio);
        } catch (Exception e) {
            return null;
        }
    }

    // Hoy -> "14:32", ayer -> "Ayer", antes -> "09/11/26". Sin fecha -> ""
    public static String formatearHoraChat(String iso) {
        Date fecha = parsear(iso);
        if (fecha == null) return "";

        Calendar mensaje = Calendar.getInstance();
        mensaje.setTime(fecha);

        Calendar hoy = Calendar.getInstance();
        if (mismoDia(mensaje, hoy)) {
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(fecha);
        }

        Calendar ayer = Calendar.getInstance();
        ayer.add(Calendar.DAY_OF_YEAR, -1);
        if (mismoDia(mensaje, ayer)) {
            return "Ayer";
        }

        return new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(fecha);
    }

    // Hoy -> "14:32", otro día -> "09/11/26 14:32". Sin fecha -> ""
    public static String formatearHoraMensaje(String iso) {
        Date fecha = parsear(iso);
        if (fecha == null) return "";

        Calendar mensaje = Calendar.getInstance();
        mensaje.setTime(fecha);

        if (mismoDia(mensaje, Calendar.getInstance())) {
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(fecha);
        }
        return new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(fecha);
    }

    // "últ. vez hoy a las 14:32", "últ. vez ayer a las 09:10", "últ. vez el 09/11/26". Sin fecha -> ""
    public static String formatearUltimaConexion(String iso) {
        Date fecha = parsear(iso);
        if (fecha == null) return "";

        Calendar conexion = Calendar.getInstance();
        conexion.setTime(fecha);
        String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(fecha);

        if (mismoDia(conexion, Calendar.getInstance())) {
            return "últ. vez hoy a las " + hora;
        }

        Calendar ayer = Calendar.getInstance();
        ayer.add(Calendar.DAY_OF_YEAR, -1);
        if (mismoDia(conexion, ayer)) {
            return "últ. vez ayer a las " + hora;
        }

        return "últ. vez el "
                + new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(fecha);
    }

    private static boolean mismoDia(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }
}