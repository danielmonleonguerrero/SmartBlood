package com.example.dani.smartblood;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import pl.rafman.scrollcalendar.ScrollCalendar;
import pl.rafman.scrollcalendar.contract.DateWatcher;
import pl.rafman.scrollcalendar.contract.MonthScrollListener;
import pl.rafman.scrollcalendar.contract.OnDateClickListener;
import pl.rafman.scrollcalendar.contract.State;
import pl.rafman.scrollcalendar.data.CalendarDay;

public class CalendarActivity extends AppCompatActivity {

    int dia=0, mes=0, año=0;
    ScrollCalendar scrollcalendar;

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
                Toast.makeText(CalendarActivity.this, day+"/"+month+"/"+year, Toast.LENGTH_SHORT).show();
                dia=day;
                mes=month;
                año=year;
            }
        });

        scrollcalendar.setDateWatcher(new DateWatcher() {
            @State
            @Override
            public int getStateForDate(int year, int month, int day) {
                if(dia==day && mes==month && año==year) return CalendarDay.TODAY;
                else return CalendarDay.DEFAULT;
            }
        });
    }
}
