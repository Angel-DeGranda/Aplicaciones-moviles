package com.example.sensoresnavdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.splashscreen.SplashScreen;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private static final long DURACION_SPLASH_MS = 2000; // cambia aquí el tiempo

    private DrawerLayout drawerLayout;
    private boolean mantenerSplash = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Debe ir ANTES de super.onCreate()
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> mantenerSplash);
        new Handler(Looper.getMainLooper()).postDelayed(
                () -> mantenerSplash = false, DURACION_SPLASH_MS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Botón de hamburguesa que abre/cierra el drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Navegación: cada opción lanza su actividad
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_acelerometro) {
                intent = new Intent(this, AcelerometroActivity.class);
            } else if (id == R.id.nav_giroscopio) {
                intent = new Intent(this, GiroscopioActivity.class);
            } else if (id == R.id.nav_magnetometro) {
                intent = new Intent(this, MagnetometroActivity.class);
            } else if (id == R.id.nav_gps) {
                intent = new Intent(this, GpsActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Botón atrás: si el drawer está abierto, lo cierra en lugar de salir
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                    drawerLayout.closeDrawers();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }
}