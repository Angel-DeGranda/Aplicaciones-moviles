package com.example.sensoresnavdrawer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class AcelerometroActivity extends BaseActivity implements SensorEventListener {

    // Una sacudida = magnitud mayor a este valor (m/s²). En reposo la magnitud es ~9.81
    private static final float UMBRAL_SACUDIDA = 15f;
    // Tiempo mínimo entre sacudidas para no contar la misma varias veces
    private static final long INTERVALO_MIN_MS = 500;

    private SensorManager sensorManager;
    private Sensor acelerometro;

    private TextView tvEstado, tvX, tvY, tvZ, tvMagnitud, tvSacudidas;

    private int contadorSacudidas = 0;
    private long ultimaSacudida = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometro);
        configurarDrawer(R.id.nav_acelerometro);

        tvEstado = findViewById(R.id.tv_estado);
        tvX = findViewById(R.id.tv_x);
        tvY = findViewById(R.id.tv_y);
        tvZ = findViewById(R.id.tv_z);
        tvMagnitud = findViewById(R.id.tv_magnitud);
        tvSacudidas = findViewById(R.id.tv_sacudidas);
        Button btnReiniciar = findViewById(R.id.btn_reiniciar);

        btnReiniciar.setOnClickListener(v -> {
            contadorSacudidas = 0;
            tvSacudidas.setText("Sacudidas: 0");
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Validar que el dispositivo tenga el sensor
        if (acelerometro == null) {
            tvEstado.setText("Este dispositivo no tiene acelerómetro");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME);
            tvEstado.setText("Mueve o sacude el teléfono");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Importante: liberar el sensor para no gastar batería
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float magnitud = (float) Math.sqrt(x * x + y * y + z * z);

        tvX.setText(String.format(Locale.US, "X: %.2f m/s²", x));
        tvY.setText(String.format(Locale.US, "Y: %.2f m/s²", y));
        tvZ.setText(String.format(Locale.US, "Z: %.2f m/s²", z));
        tvMagnitud.setText(String.format(Locale.US, "Magnitud: %.2f m/s²", magnitud));

        long ahora = SystemClock.elapsedRealtime();
        if (magnitud > UMBRAL_SACUDIDA && (ahora - ultimaSacudida) > INTERVALO_MIN_MS) {
            ultimaSacudida = ahora;
            contadorSacudidas++;
            tvSacudidas.setText("Sacudidas: " + contadorSacudidas);
            tvEstado.setText("¡Sacudida detectada!");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No necesitamos reaccionar a cambios de precisión en esta demo
    }
}