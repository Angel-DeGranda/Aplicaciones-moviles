package com.example.sensoresnavdrawer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class GiroscopioActivity extends BaseActivity implements SensorEventListener {

    // Convierte nanosegundos a segundos
    private static final float NS_A_S = 1.0f / 1_000_000_000.0f;

    private SensorManager sensorManager;
    private Sensor giroscopio;

    private TextView tvEstado, tvX, tvY, tvZ, tvAngulo, tvFlecha;

    // Ángulo acumulado en Z (radianes) y marca de tiempo de la lectura anterior
    private float anguloZ = 0f;
    private long ultimoTimestamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giroscopio);
        configurarDrawer(R.id.nav_giroscopio);

        tvEstado = findViewById(R.id.tv_estado);
        tvX = findViewById(R.id.tv_x);
        tvY = findViewById(R.id.tv_y);
        tvZ = findViewById(R.id.tv_z);
        tvAngulo = findViewById(R.id.tv_angulo);
        tvFlecha = findViewById(R.id.tv_flecha);
        Button btnReiniciar = findViewById(R.id.btn_reiniciar);

        btnReiniciar.setOnClickListener(v -> reiniciarAngulo());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        giroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (giroscopio == null) {
            tvEstado.setText("Este dispositivo no tiene giroscopio");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (giroscopio != null) {
            // Al volver a la pantalla, descartamos el timestamp viejo para no integrar
            // un dt enorme correspondiente al tiempo que estuvimos en pausa
            ultimoTimestamp = 0;
            sensorManager.registerListener(this, giroscopio, SensorManager.SENSOR_DELAY_GAME);
            tvEstado.setText("Gira el teléfono sobre una mesa");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float vx = event.values[0];
        float vy = event.values[1];
        float vz = event.values[2];

        tvX.setText(String.format(Locale.US, "X: %.2f rad/s", vx));
        tvY.setText(String.format(Locale.US, "Y: %.2f rad/s", vy));
        tvZ.setText(String.format(Locale.US, "Z: %.2f rad/s", vz));

        // Integración: ángulo += velocidad * dt
        if (ultimoTimestamp != 0) {
            float dt = (event.timestamp - ultimoTimestamp) * NS_A_S;
            anguloZ += vz * dt;
        }
        ultimoTimestamp = event.timestamp;

        float grados = (float) Math.toDegrees(anguloZ);
        tvAngulo.setText(String.format(Locale.US, "Ángulo Z: %.1f°", grados));

        // Giro positivo en Z = antihorario visto desde arriba. La flecha debe verse
        // girar al revés respecto a la pantalla para quedar fija en el "mundo",
        // y setRotation positivo es horario, por eso se usa el ángulo tal cual.
        tvFlecha.setRotation(grados);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se usa en esta demo
    }

    private void reiniciarAngulo() {
        anguloZ = 0f;
        tvAngulo.setText("Ángulo Z: 0.0°");
        tvFlecha.setRotation(0f);
    }
}