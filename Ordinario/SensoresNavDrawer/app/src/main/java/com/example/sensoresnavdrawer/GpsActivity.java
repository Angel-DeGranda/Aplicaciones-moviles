package com.example.sensoresnavdrawer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class GpsActivity extends BaseActivity implements LocationListener {

    private LocationManager locationManager;

    private TextView tvEstado, tvLatitud, tvLongitud, tvAltitud,
            tvVelocidad, tvRumbo, tvPrecision;
    private Button btnGps;

    // true mientras el usuario quiere recibir ubicación
    private boolean activo = false;

    // Solicita el permiso y recibe la respuesta del usuario
    private final ActivityResultLauncher<String> solicitudPermiso =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
                if (concedido) {
                    iniciarGps();
                } else {
                    tvEstado.setText("Permiso de ubicación denegado. Sin él no se puede usar el GPS.");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        configurarDrawer(R.id.nav_gps);

        tvEstado = findViewById(R.id.tv_estado);
        tvLatitud = findViewById(R.id.tv_latitud);
        tvLongitud = findViewById(R.id.tv_longitud);
        tvAltitud = findViewById(R.id.tv_altitud);
        tvVelocidad = findViewById(R.id.tv_velocidad);
        tvRumbo = findViewById(R.id.tv_rumbo);
        tvPrecision = findViewById(R.id.tv_precision);
        btnGps = findViewById(R.id.btn_gps);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnGps.setOnClickListener(v -> {
            if (activo) {
                detenerGps();
                tvEstado.setText("GPS detenido");
            } else {
                intentarIniciar();
            }
        });
    }

    // Paso 1: comprobar el permiso
    private void intentarIniciar() {
        if (tienePermiso()) {
            iniciarGps();
        } else {
            solicitudPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private boolean tienePermiso() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    // Paso 2: comprobar que el GPS esté encendido, y Paso 3: pedir actualizaciones
    @SuppressLint("MissingPermission") // ya comprobamos el permiso antes de llegar aquí
    private void iniciarGps() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            tvEstado.setText("El GPS está apagado. Abriendo ajustes de ubicación...");
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        // Cada 1000 ms, sin distancia mínima
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 0, this);

        activo = true;
        btnGps.setText("Detener");
        tvEstado.setText("Buscando señal GPS... (puede tardar; mejor al aire libre)");
    }

    private void detenerGps() {
        locationManager.removeUpdates(this);
        activo = false;
        btnGps.setText("Iniciar");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Si estaba activo antes de salir de la pantalla, se reanuda
        if (activo && tienePermiso()) {
            iniciarGps();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Quitar actualizaciones, pero recordar que el usuario las quería
        if (activo) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        tvLatitud.setText(String.format(Locale.US, "Latitud: %.6f", location.getLatitude()));
        tvLongitud.setText(String.format(Locale.US, "Longitud: %.6f", location.getLongitude()));

        if (location.hasAltitude()) {
            tvAltitud.setText(String.format(Locale.US, "Altitud: %.1f m", location.getAltitude()));
        }
        if (location.hasSpeed()) {
            // getSpeed() viene en m/s; × 3.6 = km/h
            tvVelocidad.setText(String.format(Locale.US, "Velocidad: %.1f km/h",
                    location.getSpeed() * 3.6f));
        }
        if (location.hasBearing()) {
            tvRumbo.setText(String.format(Locale.US, "Rumbo: %.0f°", location.getBearing()));
        }
        if (location.hasAccuracy()) {
            tvPrecision.setText(String.format(Locale.US, "Precisión: ±%.1f m", location.getAccuracy()));
        }
        tvEstado.setText("Ubicación recibida");
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        tvEstado.setText("Se apagó el GPS del dispositivo");
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        tvEstado.setText("GPS encendido, buscando señal...");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Obsoleto en versiones recientes; se deja vacío por compatibilidad con API 24
    }
}