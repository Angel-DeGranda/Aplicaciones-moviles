package com.example.calculadora_de_sumas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private int numero1;
    private int numero2;
    private int resultado;

    private EditText imputNumero1;
    private EditText imputNumero2;
    private TextView labelResultado;
    private Button buttonSumar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        imputNumero1 = findViewById(R.id.numero1);
        imputNumero2 = findViewById(R.id.numero2);
        labelResultado = findViewById(R.id.resultado);
        buttonSumar = findViewById(R.id.buttonSumar);

        buttonSumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String str = imputNumero1.getText().toString();
            try {
                numero1 = Integer.parseInt(str);
            }catch (NumberFormatException e) {
                return;
            }

            String str2 = imputNumero2.getText().toString();
            try {
                numero2 = Integer.parseInt(str2);
            }catch (NumberFormatException e) {
                return;
            }

            resultado = numero1 + numero2;

            String result;
            try {
                result = Integer.toString(resultado);
            }catch (NumberFormatException e) {
                return;
            }

            labelResultado.setText(result);
            }
        });

    }
}