package com.example.dani.smartblood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.rafman.scrollcalendar.ScrollCalendar;
import pl.rafman.scrollcalendar.contract.DateWatcher;
import pl.rafman.scrollcalendar.contract.MonthScrollListener;
import pl.rafman.scrollcalendar.contract.OnDateClickListener;
import pl.rafman.scrollcalendar.contract.State;
import pl.rafman.scrollcalendar.data.CalendarDay;

public class CalendarActivity extends AppCompatActivity {

    private static final int VIEW_ANALISIS=1;
    ScrollCalendar scrollcalendar;
    List<Analisis> ListAnalisis=new ArrayList<>();
    int lvlGlucosa =100;

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
                //Se crea un nuevo analisis
                Analisis newAnalisis = new Analisis(new Date (year, month, day, 12, 40),lvlGlucosa,"Antes de comer", "Poco ejercicio");
                ListAnalisis.add(newAnalisis); //Se añade un nuevo objeto analisis y se rellena

                //Se rellena una lista con los analisis del dia seleccionada
                DiaAnalisis diaAnalisis =new DiaAnalisis(new ArrayList<Analisis>());
                List<Analisis> ListAnalisisIntent=new ArrayList<>();
                for(int i=0; i<ListAnalisis.size(); i++){
                    if(year==ListAnalisis.get(i).getTiempo().getYear() && month==ListAnalisis.get(i).getTiempo().getMonth() && day==ListAnalisis.get(i).getTiempo().getDate()){
                        ListAnalisisIntent.add(ListAnalisis.get(i));
                    }
                }
                //Se encapsua el List en un objeto del tipo DiaAnalisis para poder pasarlo a otra actividad con el intent.
                diaAnalisis.setArrayAnalisis(ListAnalisisIntent);

                //Se crea una intent
                Intent intent = new Intent(CalendarActivity.this, ResumenAnalisisActivity.class);
                intent.putExtra("diaAnalisis", diaAnalisis);
                startActivityForResult(intent, VIEW_ANALISIS);

                //Si no existe un Analisis en el dia seleccionado, se crea uno. Tambien se crea un array de analisis para introducirlo en el array de diaanalisis

                lvlGlucosa=lvlGlucosa+5;
            }

        });

        //Modifica las apariencias de los dias en el calendario.
        scrollcalendar.setDateWatcher(new DateWatcher() {
            @State
            @Override
            public int getStateForDate(int year, int month, int day) {
                if(isSelected(year,month,day)) return CalendarDay.TODAY;
                else return CalendarDay.DEFAULT;
            }
        });
    }

    //Se mira si X dia necesita estar marcado con color rojo.
    private boolean isSelected(int year, int month, int day) {
        for(int i=0; i<ListAnalisis.size(); i++){
            if(year==ListAnalisis.get(i).getTiempo().getYear() && month==ListAnalisis.get(i).getTiempo().getMonth() && day==ListAnalisis.get(i).getTiempo().getDate()){
                return(true);
            }
        }
        return(false);
    }
}
