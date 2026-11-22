package com.example.fakewhatsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

// Fase 11: crear un grupo (nombre + participantes)
public class NuevoGrupoActivity extends AppCompatActivity {

    private TextInputEditText etNombre;
    private TextView tvSeleccionados, tvVacio;
    private ProgressBar progreso;
    private Button btnCrear;

    private SeleccionContactosAdapter adapter;
    private ConversacionService conversaciones;
    private boolean creando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_grupo);
        PantallaUtil.respetarBarras(this);

        etNombre = findViewById(R.id.etNombreGrupo);
        tvSeleccionados = findViewById(R.id.tvSeleccionados);
        tvVacio = findViewById(R.id.tvVacio);
        progreso = findViewById(R.id.progreso);
        btnCrear = findViewById(R.id.btnCrear);
        RecyclerView rv = findViewById(R.id.rvContactos);

        conversaciones = new ConversacionService(this);

        adapter = new SeleccionContactosAdapter(cantidad ->
                tvSeleccionados.setText("Participantes: " + cantidad
                        + (cantidad == 1 ? " seleccionado" : " seleccionados")));
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        btnCrear.setOnClickListener(v -> crear());

        cargarContactos();
    }

    private void cargarContactos() {
        progreso.setVisibility(View.VISIBLE);
        new PerfilService(this).listarUsuarios(new PerfilService.ListaCallback() {
            @Override
            public void onExito(List<Perfil> perfiles) {
                progreso.setVisibility(View.GONE);
                adapter.setPerfiles(perfiles);
                tvVacio.setVisibility(perfiles == null || perfiles.isEmpty()
                        ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String mensaje) {
                progreso.setVisibility(View.GONE);
                Toast.makeText(NuevoGrupoActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void crear() {
        if (creando) return;

        String nombre = etNombre.getText() == null ? "" : etNombre.getText().toString().trim();
        List<String> miembros = adapter.getSeleccionados();

        if (nombre.isEmpty()) {
            etNombre.setError("Escribe un nombre para el grupo");
            return;
        }
        if (miembros.isEmpty()) {
            Toast.makeText(this, "Elige al menos un participante", Toast.LENGTH_SHORT).show();
            return;
        }

        creando = true;
        btnCrear.setEnabled(false);
        progreso.setVisibility(View.VISIBLE);

        conversaciones.crearGrupo(nombre, miembros, new ConversacionService.IdCallback() {
            @Override
            public void onExito(String conversacionId) {
                progreso.setVisibility(View.GONE);
                // Sin "otro perfil": ChatActivity lo trata como grupo
                startActivity(ChatActivity.crearIntent(NuevoGrupoActivity.this,
                        conversacionId, nombre, null, null));
                finish();
            }

            @Override
            public void onError(String mensaje) {
                creando = false;
                btnCrear.setEnabled(true);
                progreso.setVisibility(View.GONE);
                Toast.makeText(NuevoGrupoActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }
}
