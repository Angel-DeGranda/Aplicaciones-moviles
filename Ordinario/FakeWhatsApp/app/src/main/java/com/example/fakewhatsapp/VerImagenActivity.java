package com.example.fakewhatsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

// Fase 11: muestra una foto (del chat o de perfil) en pantalla completa
public class VerImagenActivity extends AppCompatActivity {

    private static final String EXTRA_URL = "url";

    public static void abrir(Context context, String url) {
        if (url == null || url.isEmpty()) return;
        Intent intent = new Intent(context, VerImagenActivity.class);
        intent.putExtra(EXTRA_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_imagen);
        PantallaUtil.respetarBarras(this);

        ImageView img = findViewById(R.id.imgCompleta);
        View progreso = findViewById(R.id.progreso);
        findViewById(R.id.btnCerrar).setOnClickListener(v -> finish());
        img.setOnClickListener(v -> finish());

        Glide.with(this)
                .load(getIntent().getStringExtra(EXTRA_URL))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object modelo,
                                                Target<Drawable> destino, boolean primero) {
                        progreso.setVisibility(View.GONE);
                        Toast.makeText(VerImagenActivity.this,
                                "No se pudo cargar la foto", Toast.LENGTH_LONG).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable recurso, Object modelo,
                                                   Target<Drawable> destino, DataSource origen,
                                                   boolean primero) {
                        progreso.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(img);
    }
}
