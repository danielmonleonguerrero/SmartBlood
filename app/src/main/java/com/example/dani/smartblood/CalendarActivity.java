package com.example.dani.smartblood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import ConexionBT.BluetoothConnection;
import pl.rafman.scrollcalendar.ScrollCalendar;
import pl.rafman.scrollcalendar.contract.DateWatcher;
import pl.rafman.scrollcalendar.contract.MonthScrollListener;
import pl.rafman.scrollcalendar.contract.OnDateClickListener;
import pl.rafman.scrollcalendar.contract.State;
import pl.rafman.scrollcalendar.data.CalendarDay;

public class CalendarActivity extends AppCompatActivity {

    private static final int VIEW_ANALISIS=1;
    private static final String NombreArchivoSalvaguarda="AnalisisSalvaguarda";
    private static final String NombreUsuario="Pepita/Gonzalez/Ruiz"; //El nombre del usuario es el nombre de la coleccion en firebase. Se obtiene en RegistrarUsuarioActivity.

    BluetoothConnection bluetoothConnection = new BluetoothConnection(this);

    ScrollCalendar scrollcalendar;
    List<Analisis> ListAnalisis=new ArrayList<>();

    //-------------------METODOS ONCREATE Y ONSTOP-------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        scrollcalendar = findViewById(R.id.scrollcalendar);

        CargarDatosAnalisis();

        Intent intent = getIntent();
        if(intent.getBooleanExtra("NewAnalisis", false)){
            Analisis newAnalisisIntent = (Analisis)intent.getSerializableExtra("Analisis");
            ListAnalisis.add(newAnalisisIntent);
        }

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

        //Se detecta CLIC en un dia del calendario
        scrollcalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onCalendarDayClicked(int year, int month, int day) {
                scrollcalendar.refresh();
                //-------ON CLIC REAL-------//
                //Se rellena una lista con los analisis del dia seleccionada
                DiaAnalisis diaAnalisis =new DiaAnalisis(new ArrayList<Analisis>());
                List<Analisis> ListAnalisisIntent=new ArrayList<>();
                for(int i=0; i<ListAnalisis.size(); i++){
                    if(year==ListAnalisis.get(i).getTiempo().getYear() && month==ListAnalisis.get(i).getTiempo().getMonth() && day==ListAnalisis.get(i).getTiempo().getDate()){
                        ListAnalisisIntent.add(ListAnalisis.get(i));
                    }
                }
                //Se encapsua el List en un objeto del tipo DiaAnalisis para poder pasarlo a otra actividad como extra en el intent.
                diaAnalisis.setArrayAnalisis(ListAnalisisIntent);

                //Se crea una intent y se llama a la actividad ResumenAnalisisActivity
                Intent intent = new Intent(CalendarActivity.this, ResumenAnalisisActivity.class);
                intent.putExtra("diaAnalisis", diaAnalisis);
                intent.putExtra("Anyo", year);
                intent.putExtra("Mes", month);
                intent.putExtra("Dia", day);
                startActivityForResult(intent, VIEW_ANALISIS);
            }

        });

        //Modifica las apariencias de los dias en el calendario.
        scrollcalendar.setDateWatcher(new DateWatcher() {
            @State
            @Override
            public int getStateForDate(int year, int month, int day) {
                if(isMarcado(year,month,day)) return CalendarDay.TODAY;
                else return CalendarDay.DEFAULT;
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        GuardarDatosAnalisis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GuardarDatosAnalisis();
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ConexionBT:
                bluetoothConnection.startConnection();
                return true;
            case R.id.DesconexionBT:
                Toast.makeText(this, "Desconexión Bluetooth", Toast.LENGTH_SHORT).show();
                bluetoothConnection.disableBT();
                return true;
            case R.id.CodigoQR:
                Toast.makeText(this, "Codigo QR", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //----------------------------------------------------------------//

    //Mira si X dia necesita estar marcado con color rojo.
    private boolean isMarcado(int year, int month, int day) {
        Log.e("SMARTBLOOD", Integer.toString(ListAnalisis.size()));
        if(ListAnalisis.size()>0){
            for(int i=0; i<ListAnalisis.size(); i++){
                if(year==ListAnalisis.get(i).getTiempo().getYear() && month==ListAnalisis.get(i).getTiempo().getMonth() && day==ListAnalisis.get(i).getTiempo().getDate()){
                    return(true);
                }
            }
        }
        return(false);
    }

    //--------METODOS PARA LA SALVAGUARDA DE DATOS DE ANALISIS--------//
    private void CargarDatosAnalisis() {
        try {
            FileInputStream FIS =openFileInput(NombreArchivoSalvaguarda);
            Scanner scanner = new Scanner(FIS);
            while (scanner.hasNextLine()){
                String Analisisscanner = scanner.nextLine();
                String[] parts = Analisisscanner.split("/");
                Analisis lAnalisis = new Analisis(new Date(Integer.valueOf(parts[3]),
                        Integer.valueOf(parts[4]), Integer.valueOf(parts[5]), Integer.valueOf(parts[6]),
                        Integer.valueOf(parts[7])), Integer.valueOf(parts[0]), parts[1], parts[2] );
                ListAnalisis.add(lAnalisis);
            }
            scrollcalendar.refresh();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void GuardarDatosAnalisis() {
        try {
            FileOutputStream FOS = openFileOutput(NombreArchivoSalvaguarda, MODE_PRIVATE);
            for (int i=0; i<ListAnalisis.size(); i++){
                String sAnalisis = crearStringSalvaguarda(i); //salvaguardaAnalisis
                try {
                    FOS.write(sAnalisis.getBytes());
                } catch (IOException e) {
                    Log.e("SMARTBLOOD", "No se ha podido escribir en el fichero. Excpecion: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("SMARTBLOOD", "No se ha podido abrir el archivo: " + NombreArchivoSalvaguarda + "Excepcion: " + e.getMessage());
        }
    }

    private String crearStringSalvaguarda(int i) {
        Analisis Analisis = ListAnalisis.get(i);
        Date sdAnalisis = Analisis.getTiempo();
        String ssAnalisis = Integer.valueOf(Analisis.getNivelGlucosa()) + "/" +     //Part 0
                Analisis.getNota1() + "/"                                           //Part 1
                +Analisis.getNota2() + "/"+                                         //Part 2
                String.valueOf(sdAnalisis.getYear()) + "/" +                        //Part 3
                String.valueOf(sdAnalisis.getMonth()) + "/"+                        //Part 4
                String.valueOf(sdAnalisis.getDate()) + "/"+                         //Part 5
                String.valueOf(sdAnalisis.getHours()) + "/"+                        //Part 6
                String.valueOf(sdAnalisis.getMinutes()) + "\n";                     //Part 7
        return (ssAnalisis);
    }
    //----------------------------------------------------------------//



}
