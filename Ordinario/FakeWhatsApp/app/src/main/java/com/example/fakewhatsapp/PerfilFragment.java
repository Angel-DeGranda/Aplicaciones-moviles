package com.example.fakewhatsapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Pestaña "Perfil" de la pantalla principal (antes PerfilActivity) + cerrar sesión
public class PerfilFragment extends Fragment {

    private ImageView imgAvatar;
    private TextView tvUsuario;
    private TextInputEditText etNombreMostrar, etInfo;
    private Button btnCambiarFoto, btnGuardar, btnCerrarSesion;
    private ProgressBar progreso;

    private PerfilService perfiles;
    private final ExecutorService hilo = Executors.newSingleThreadExecutor();

    // Selector de fotos del sistema (no pide permisos)
    private final ActivityResultLauncher<PickVisualMediaRequest> selectorFoto =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    subirFoto(uri);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle savedInstanceState) {
        imgAvatar = vista.findViewById(R.id.imgAvatar);
        tvUsuario = vista.findViewById(R.id.tvUsuario);
        etNombreMostrar = vista.findViewById(R.id.etNombreMostrar);
        etInfo = vista.findViewById(R.id.etInfo);
        btnCambiarFoto = vista.findViewById(R.id.btnCambiarFoto);
        btnGuardar = vista.findViewById(R.id.btnGuardar);
        btnCerrarSesion = vista.findViewById(R.id.btnCerrarSesion);
        progreso = vista.findViewById(R.id.progreso);
        TextView tvCorreo = vista.findViewById(R.id.tvCorreo);

        perfiles = new PerfilService(requireContext());
        tvCorreo.setText(new SessionManager(requireContext()).getCorreo());

        btnCambiarFoto.setOnClickListener(v -> selectorFoto.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));
        btnGuardar.setOnClickListener(v -> guardar());
        btnCerrarSesion.setOnClickListener(v -> {
            btnCerrarSesion.setEnabled(false);
            ((MainActivity) requireActivity()).cerrarSesion();
        });

        cargarPerfil();
    }

    // ---------- CARGAR ----------
    private void cargarPerfil() {
        cargando(true);
        perfiles.obtenerMiPerfil(new PerfilService.PerfilCallback() {
            @Override
            public void onExito(Perfil perfil) {
                if (!isAdded()) return;
                cargando(false);
                mostrarPerfil(perfil);
            }

            @Override
            public void onError(String mensaje) {
                if (!isAdded()) return;
                cargando(false);
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarPerfil(Perfil perfil) {
        tvUsuario.setText("@" + perfil.getNombreUsuario());
        etNombreMostrar.setText(perfil.getNombreMostrar());
        etInfo.setText(perfil.getInfo() == null ? "" : perfil.getInfo());
        cargarAvatar(perfil.getAvatarUrl());
    }

    private void cargarAvatar(String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .fallback(R.drawable.ic_avatar_placeholder)
                .circleCrop()
                .into(imgAvatar);

        // Tocar la foto la muestra en grande
        imgAvatar.setOnClickListener(url == null ? null
                : v -> VerImagenActivity.abrir(requireContext(), url));
    }

    // ---------- GUARDAR NOMBRE E INFO ----------
    private void guardar() {
        String nombre = etNombreMostrar.getText() == null ? "" : etNombreMostrar.getText().toString().trim();
        String info = etInfo.getText() == null ? "" : etInfo.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombreMostrar.setError("Escribe tu nombre");
            return;
        }

        cargando(true);
        perfiles.actualizarPerfil(nombre, info, new PerfilService.PerfilCallback() {
            @Override
            public void onExito(Perfil perfil) {
                if (!isAdded()) return;
                cargando(false);
                Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String mensaje) {
                if (!isAdded()) return;
                cargando(false);
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------- SUBIR FOTO ----------
    private void subirFoto(Uri uri) {
        cargando(true);
        android.content.Context app = requireContext().getApplicationContext();
        // Preparar la imagen (reducir, rotar, comprimir) en un hilo aparte
        hilo.execute(() -> {
            try {
                byte[] jpeg = ImagenUtil.prepararAvatar(app, uri);
                perfiles.subirAvatar(jpeg, new PerfilService.PerfilCallback() {
                    @Override
                    public void onExito(Perfil perfil) {
                        if (!isAdded()) return;
                        cargando(false);
                        cargarAvatar(perfil.getAvatarUrl());
                        Toast.makeText(requireContext(), "Foto actualizada", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String mensaje) {
                        if (!isAdded()) return;
                        cargando(false);
                        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                imgAvatar.post(() -> {
                    if (!isAdded()) return;
                    cargando(false);
                    Toast.makeText(requireContext(), "No se pudo leer la imagen", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void cargando(boolean activo) {
        progreso.setVisibility(activo ? View.VISIBLE : View.GONE);
        btnGuardar.setEnabled(!activo);
        btnCambiarFoto.setEnabled(!activo);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hilo.shutdown();
    }
}
