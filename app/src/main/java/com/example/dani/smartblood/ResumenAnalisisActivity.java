package com.example.dani.smartblood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ResumenAnalisisActivity extends AppCompatActivity {

    int dia=0, mes=0, anyo=0, num=0;
    private TextView diaView;
    private TextView mesView;
    private TextView anyoView;
    private TextView numAnalisisView;
    String[] MesesDelAnyo = new String[]{
     "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Setiembre", "Octubre", "Noviembre", "Diciembre"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_analisis);

        diaView = findViewById(R.id.diaView);
        mesView = findViewById(R.id.mesView);
        anyoView = findViewById(R.id.anyoView);
        numAnalisisView=findViewById(R.id.numAnalisisView);
        Intent intent = getIntent();
        if(intent!= null){
            dia=intent.getIntExtra("dia", -1);
            mes=intent.getIntExtra("mes", -1);
            anyo=intent.getIntExtra("anyo", -1);
            num=intent.getIntExtra("num", -1);
        }
        diaView.setText(Integer.toString(dia));
        mesView.setText(MesesDelAnyo[mes]);
        anyoView.setText(Integer.toString(anyo));
        numAnalisisView.setText(Integer.toString(num));


    }
}
