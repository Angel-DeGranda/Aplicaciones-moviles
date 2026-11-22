package com.example.sensoresnavdrawer;

import android.content.Intent;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;

    /**
     * Llamar justo después de setContentView().
     * @param itemMenuActual id del item del menú que corresponde a esta pantalla
     */
    protected void configurarDrawer(int itemMenuActual) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setCheckedItem(itemMenuActual);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawers();

            // Si ya estás en esa pantalla, no hace nada
            if (id == itemMenuActual) {
                return true;
            }

            Class<?> destino = null;
            if (id == R.id.nav_acelerometro) {
                destino = AcelerometroActivity.class;
            } else if (id == R.id.nav_giroscopio) {
                destino = GiroscopioActivity.class;
            } else if (id == R.id.nav_magnetometro) {
                destino = MagnetometroActivity.class;
            } else if (id == R.id.nav_gps) {
                destino = GpsActivity.class;
            }

            if (destino != null) {
                startActivity(new Intent(this, destino));
                // Cierra la actual para que no se acumulen pantallas;
                // al dar "atrás" regresas al Main
                finish();
            }
            return true;
        });

        // Botón atrás: si el drawer está abierto lo cierra, si no, sale normal
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawers();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }
}