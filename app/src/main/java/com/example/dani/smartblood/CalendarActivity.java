package com.example.dani.smartblood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import pl.rafman.scrollcalendar.ScrollCalendar;
import pl.rafman.scrollcalendar.contract.DateWatcher;
import pl.rafman.scrollcalendar.contract.MonthScrollListener;
import pl.rafman.scrollcalendar.contract.OnDateClickListener;
import pl.rafman.scrollcalendar.contract.State;
import pl.rafman.scrollcalendar.data.CalendarDay;

public class CalendarActivity extends AppCompatActivity {

    private static final int VIEW_ANALISIS=1;
    int dia=0, mes=0, año=0;
    ScrollCalendar scrollcalendar;
    ArrayList<DiaAnalisis> ArrayDiaAnalisis = new ArrayList<>(50);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        scrollcalendar = findViewById(R.id.scrollcalendar);

        //Ocultamos meses posteriores
        scrollcalendar.setMonthScrollListener(new MonthScrollListener() {
            @Override
            public boolean shouldAddNextMonth(int lastDisplayedYear, int lastDisplayedMonth) {
                // return false if you don't want to show later months
                return false;
            }
            @Override
            public boolean shouldAddPreviousMonth(int firstDisplayedYear, int firstDisplayedMonth) {
                // return false if you don't want to show previous months
                return true;
            }
        });

        scrollcalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onCalendarDayClicked(int year, int month, int day) {
                //En la aplicacion final, no se crea aqui el analisis. Es una version de prueba
                ArrayDiaAnalisis.add(new DiaAnalisis(day, month, year, 100, new Analisis(1340, 100, "Antes de comer", "Mucho ejercicio") ));
                //En la aplicacion final, no sabemos en que posicion del array esta el analisis del dia clicado, asi que tenemos que buscarlo
                for(int i=0; i<ArrayDiaAnalisis.size(); i++){
                    if(year==ArrayDiaAnalisis.get(i).getAnyo() && month==ArrayDiaAnalisis.get(i).getMes() && day==ArrayDiaAnalisis.get(i).getDia()){
                        Intent intent = new Intent(CalendarActivity.this, ResumenAnalisisActivity.class);
                        intent.putExtra("dia", day);
                        intent.putExtra("mes", month);
                        intent.putExtra("anyo", year);
                        startActivityForResult(intent, VIEW_ANALISIS);
                    }
                }
            }
        });

        scrollcalendar.setDateWatcher(new DateWatcher() {
            @State
            @Override
            public int getStateForDate(int year, int month, int day) {
                if(isSelected(year,month,day)) return CalendarDay.TODAY;
                else return CalendarDay.DEFAULT;
            }
        });
    }

    private boolean isSelected(int year, int month, int day) {
        for(int i=0; i<ArrayDiaAnalisis.size(); i++){
            if(year==ArrayDiaAnalisis.get(i).getAnyo() && month==ArrayDiaAnalisis.get(i).getMes() && day==ArrayDiaAnalisis.get(i).getDia()){
                return(true);
            }
        }
        return(false);
    }
}
