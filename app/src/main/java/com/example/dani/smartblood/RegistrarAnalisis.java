package com.example.dani.smartblood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Date;

public class RegistrarAnalisis extends AppCompatActivity {
    private static final int REGISTER_ANALISIS=2;

    private TextView nivelGlucosaView;
    private RadioButton antesdecomer_rdbutton;
    private RadioButton despuesdecomer_rdbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_analisis);
        nivelGlucosaView=findViewById(R.id.nivelGlucosaView);
        antesdecomer_rdbutton=findViewById(R.id.antesdecomer_rdbutton);
        despuesdecomer_rdbutton=findViewById(R.id.despuesdecomer_rdbutton);
    }

    public void onRegistrar(View view) {/*
        Analisis analisis = new Analisis(0,0,0,"","");
        Date currentTime = Calendar.getInstance().getTime();

        analisis.setNivelGlucosa(Integer.valueOf(nivelGlucosaView.getText().toString()));
        analisis.setHora(currentTime.getHours());
        analisis.setMin(currentTime.getMinutes());
        if (antesdecomer_rdbutton.isChecked()) analisis.setNota1(antesdecomer_rdbutton.getText().toString());
        else if (despuesdecomer_rdbutton.isChecked()) analisis.setNota2(despuesdecomer_rdbutton.getText().toString());

        Intent intent = new Intent(RegistrarAnalisis.this, CalendarActivity.class);
        intent.putExtra("Analisis", analisis);
        intent.putExtra("Anyo", currentTime.getYear());
        intent.putExtra("Mes", currentTime.getMonth());
        intent.putExtra("Dia", currentTime.getDay());
        startActivityForResult(intent, REGISTER_ANALISIS);*/
    }
}
