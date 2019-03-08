package com.example.dani.smartblood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ResumenAnalisisActivity extends AppCompatActivity {

    int dia=0, mes=0, anyo=0;
    private TextView diaView;
    private TextView mesView;
    private TextView anyoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_analisis);

        diaView = findViewById(R.id.diaView);
        mesView = findViewById(R.id.mesView);
        anyoView = findViewById(R.id.anyoView);
        Intent intent = getIntent();
        if(intent!= null){
            dia=intent.getIntExtra("dia", -1);
            mes=intent.getIntExtra("mes", -1);
            anyo=intent.getIntExtra("anyo", -1);
        }
        diaView.setText(Integer.toString(dia));
        mesView.setText(Integer.toString(mes+1));
        anyoView.setText(Integer.toString(anyo));


    }
}
