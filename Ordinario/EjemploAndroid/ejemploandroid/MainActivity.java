package com.example.ejemploandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    TextView texto1;
    TextView texto2;
    Button boton;

    TextView inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        texto1 = findViewById(R.id.hello);
        texto2 = findViewById(R.id.message);
        boton = findViewById(R.id.button);
        inputText = findViewById(R.id.Input_txt);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp1 = texto1.getText().toString();
                String temp2 = texto2.getText().toString();
                String texto = inputText.getText().toString();

                texto1.setText(texto);
            }
        });
    }
}