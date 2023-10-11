package com.example.figuras3d;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Intent itn = new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnEsfera = findViewById(R.id.btnEsfera);
        Button btnCilindro = findViewById(R.id.btnCilindro);
        Button btnCono = findViewById(R.id.btnCono);
        Button btnCubo = findViewById(R.id.btnCubo);

        btnEsfera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EsferaActivity.class);
                startActivity(intent);
            }
        });

        btnCilindro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CilindroActivity.class);
                startActivity(intent);
            }
        });

        btnCono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ConoActivity.class);
                startActivity(intent);
            }
        });

        btnCubo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CuboActivity.class);
                startActivity(intent);
            }
        });
    }
}
