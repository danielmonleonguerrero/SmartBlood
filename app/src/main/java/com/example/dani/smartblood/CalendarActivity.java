package com.example.dani.smartblood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import pl.rafman.scrollcalendar.ScrollCalendar;
import pl.rafman.scrollcalendar.contract.DateWatcher;
import pl.rafman.scrollcalendar.contract.MonthScrollListener;
import pl.rafman.scrollcalendar.contract.OnDateClickListener;
import pl.rafman.scrollcalendar.contract.State;
import pl.rafman.scrollcalendar.data.CalendarDay;

public class CalendarActivity extends AppCompatActivity {

    private static final int VIEW_ANALISIS=1;
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
                boolean nuevoDiaAnalisis=true;
               //Buscamos si existe un Analisis con el dia seleccionado
                for(int i=0; i<ArrayDiaAnalisis.size(); i++){
                    if(year==ArrayDiaAnalisis.get(i).getAnyo() && month==ArrayDiaAnalisis.get(i).getMes() && day==ArrayDiaAnalisis.get(i).getDia()){
                        //Si hay un Analisis en el dia seleccionado, se introduce un nuevo Analisis
                        nuevoDiaAnalisis=false;
                        ArrayList<Analisis> analisis;
                        analisis=ArrayDiaAnalisis.get(i).getArrayAnalisi(); //Se coge el array de los analisis actual
                        analisis.add(new Analisis(1240, 100, "Nota1", "Nota2")); //Se añade un nuevo objeto analisis y se rellena
                        ArrayDiaAnalisis.get(i).setArrayAnalisi(analisis);  //Devolvemos el array de los analisis con un analisis mas
                        Intent intent = new Intent(CalendarActivity.this, ResumenAnalisisActivity.class);
                        intent.putExtra("dia", day);
                        intent.putExtra("mes", month);
                        intent.putExtra("anyo", year);
                        intent.putExtra("num", ArrayDiaAnalisis.get(i).getArrayAnalisi().size());
                        startActivityForResult(intent, VIEW_ANALISIS);
                    }
                }
                if(nuevoDiaAnalisis){
                    //Si no existe un Analisis en el dia seleccionado, se crea uno. Tambien se crea un array de analisis para introducirlo en el array de diaanalisis
                    ArrayList<Analisis> ArrayAnalisis = new ArrayList<>();
                    ArrayDiaAnalisis.add(new DiaAnalisis(day, month, year, 100, ArrayAnalisis));
                    Intent intent = new Intent(CalendarActivity.this, ResumenAnalisisActivity.class);
                    intent.putExtra("dia", day);
                    intent.putExtra("mes", month);
                    intent.putExtra("anyo", year);
                    intent.putExtra("num", 1);
                    startActivityForResult(intent, VIEW_ANALISIS);
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
