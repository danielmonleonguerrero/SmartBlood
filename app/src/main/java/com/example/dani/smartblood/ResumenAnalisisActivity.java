package com.example.dani.smartblood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ResumenAnalisisActivity extends AppCompatActivity {

    private TextView diaView;
    private TextView mesView;
    private TextView anyoView;
    String[] MesesDelAnyo = new String[]{
     "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Setiembre", "Octubre", "Noviembre", "Diciembre"
    };
    DiaAnalisis diaAnalisis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_analisis);

        diaView = findViewById(R.id.diaView);
        mesView = findViewById(R.id.mesView);
        anyoView = findViewById(R.id.anyoView);

        Intent intent = getIntent();
        diaAnalisis =(DiaAnalisis)intent.getSerializableExtra("DiaAnalisis");

        diaView.setText(Integer.toString(diaAnalisis.getDia()));
        mesView.setText(MesesDelAnyo[diaAnalisis.getMes()]);
        anyoView.setText(Integer.toString(diaAnalisis.getAnyo()));

    }
}
