package com.example.sensoresnavdrawer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class MagnetometroActivity extends BaseActivity implements SensorEventListener {

    // Factor del filtro paso bajo: más alto = más suave pero con más retraso
    private static final float ALPHA = 0.9f;

    private static final String[] PUNTOS = {
            "N", "NE", "E", "SE", "S", "SO", "O", "NO"
    };

    private SensorManager sensorManager;
    private Sensor acelerometro;
    private Sensor magnetometro;

    private TextView tvEstado, tvX, tvY, tvZ, tvIntensidad, tvFlecha, tvAzimut;

    // Últimas lecturas filtradas de cada sensor
    private final float[] gravedad = new float[3];
    private final float[] campo = new float[3];
    private boolean tieneGravedad = false;
    private boolean tieneCampo = false;

    // Buffers reutilizados para no crear objetos en cada evento
    private final float[] matrizRotacion = new float[9];
    private final float[] orientacion = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetometro);
        configurarDrawer(R.id.nav_magnetometro);

        tvEstado = findViewById(R.id.tv_estado);
        tvX = findViewById(R.id.tv_x);
        tvY = findViewById(R.id.tv_y);
        tvZ = findViewById(R.id.tv_z);
        tvIntensidad = findViewById(R.id.tv_intensidad);
        tvFlecha = findViewById(R.id.tv_flecha);
        tvAzimut = findViewById(R.id.tv_azimut);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // La brújula necesita los dos sensores
        if (magnetometro == null) {
            tvEstado.setText("Este dispositivo no tiene magnetómetro");
        } else if (acelerometro == null) {
            tvEstado.setText("Falta el acelerómetro: no se puede calcular la brújula");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (magnetometro != null && acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_GAME);
            tvEstado.setText("Sostén el teléfono plano y gíralo");
            tieneGravedad = false;
            tieneCampo = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Un solo unregister quita los listeners de ambos sensores
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            filtrar(gravedad, event.values, tieneGravedad);
            tieneGravedad = true;

        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            filtrar(campo, event.values, tieneCampo);
            tieneCampo = true;

            // Valores del campo (usamos los crudos para mostrar, sin filtrar)
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float intensidad = (float) Math.sqrt(x * x + y * y + z * z);

            tvX.setText(String.format(Locale.US, "X: %.1f µT", x));
            tvY.setText(String.format(Locale.US, "Y: %.1f µT", y));
            tvZ.setText(String.format(Locale.US, "Z: %.1f µT", z));
            tvIntensidad.setText(String.format(Locale.US, "Intensidad: %.1f µT", intensidad));
        }

        if (tieneGravedad && tieneCampo) {
            actualizarBrujula();
        }
    }

    // Filtro paso bajo: destino = ALPHA * destino + (1 - ALPHA) * nuevo
    private void filtrar(float[] destino, float[] nuevo, boolean yaInicializado) {
        for (int i = 0; i < 3; i++) {
            if (yaInicializado) {
                destino[i] = ALPHA * destino[i] + (1 - ALPHA) * nuevo[i];
            } else {
                destino[i] = nuevo[i]; // primera lectura: sin mezclar con ceros
            }
        }
    }

    private void actualizarBrujula() {
        boolean ok = SensorManager.getRotationMatrix(matrizRotacion, null, gravedad, campo);
        if (!ok) {
            return; // puede fallar si el teléfono está en caída libre
        }
        SensorManager.getOrientation(matrizRotacion, orientacion);

        // orientacion[0] = azimut en radianes (-π a π). Lo pasamos a 0°–360°
        float azimut = (float) Math.toDegrees(orientacion[0]);
        azimut = (azimut + 360f) % 360f;

        // La flecha rota al revés que el teléfono para seguir señalando el norte
        tvFlecha.setRotation(-azimut);

        int indice = Math.round(azimut / 45f) % 8;
        tvAzimut.setText(String.format(Locale.US, "%.0f° %s", azimut, PUNTOS[indice]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() != Sensor.TYPE_MAGNETIC_FIELD) {
            return;
        }
        if (accuracy <= SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
            tvEstado.setText("Calibración baja: mueve el teléfono dibujando un 8 en el aire");
        } else {
            tvEstado.setText("Sostén el teléfono plano y gíralo");
        }
    }
}