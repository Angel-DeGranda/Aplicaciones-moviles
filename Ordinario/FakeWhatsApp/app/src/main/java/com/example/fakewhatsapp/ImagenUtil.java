package com.example.fakewhatsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImagenUtil {

    private static final int LADO_AVATAR = 512;
    private static final int LADO_FOTO_CHAT = 1280;   // Fase 11: buena calidad sin pesar demasiado
    private static final int CALIDAD_JPEG = 85;

    // Foto de perfil: 512 px como máximo
    // IMPORTANTE: llamar desde un hilo secundario, no desde el principal
    public static byte[] prepararAvatar(Context context, Uri uri) throws IOException {
        return preparar(context, uri, LADO_AVATAR);
    }

    // Foto para mandar en un chat: 1280 px como máximo (Fase 11)
    // IMPORTANTE: llamar desde un hilo secundario, no desde el principal
    public static byte[] prepararFotoChat(Context context, Uri uri) throws IOException {
        return preparar(context, uri, LADO_FOTO_CHAT);
    }

    // Lee la imagen elegida, la reduce, corrige su rotación y la devuelve como JPEG
    private static byte[] preparar(Context context, Uri uri, int ladoMaximo) throws IOException {
        // 1) Leer solo las dimensiones, sin cargar toda la imagen en memoria
        BitmapFactory.Options opciones = new BitmapFactory.Options();
        opciones.inJustDecodeBounds = true;
        try (InputStream in = context.getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(in, null, opciones);
        }

        // 2) Cargarla ya reducida (potencias de 2) para no gastar memoria de más
        int ladoMayor = Math.max(opciones.outWidth, opciones.outHeight);
        int muestreo = 1;
        while (ladoMayor / muestreo > ladoMaximo * 2) {
            muestreo *= 2;
        }
        opciones.inJustDecodeBounds = false;
        opciones.inSampleSize = muestreo;

        Bitmap original;
        try (InputStream in = context.getContentResolver().openInputStream(uri)) {
            original = BitmapFactory.decodeStream(in, null, opciones);
        }
        if (original == null) {
            throw new IOException("No se pudo leer la imagen");
        }

        // 3) Escalar al tamaño máximo y girar si hace falta, en un solo paso
        float escala = Math.min(1f, (float) ladoMaximo / Math.max(original.getWidth(), original.getHeight()));
        Matrix matriz = new Matrix();
        matriz.postScale(escala, escala);
        int rotacion = leerRotacion(context, uri);
        if (rotacion != 0) {
            matriz.postRotate(rotacion);
        }
        Bitmap final_ = Bitmap.createBitmap(original, 0, 0,
                original.getWidth(), original.getHeight(), matriz, true);

        // 4) Comprimir a JPEG
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        final_.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida);

        if (final_ != original) {
            original.recycle();
        }
        return salida.toByteArray();
    }

    private static int leerRotacion(Context context, Uri uri) {
        try (InputStream in = context.getContentResolver().openInputStream(uri)) {
            ExifInterface exif = new ExifInterface(in);
            int orientacion = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientacion) {
                case ExifInterface.ORIENTATION_ROTATE_90:  return 90;
                case ExifInterface.ORIENTATION_ROTATE_180: return 180;
                case ExifInterface.ORIENTATION_ROTATE_270: return 270;
                default: return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }
}
