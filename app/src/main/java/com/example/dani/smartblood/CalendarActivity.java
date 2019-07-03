package com.example.dani.smartblood;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.util.Set;
import java.util.UUID;

import ConexionBT.BluetoothConnectionService;
import pl.rafman.scrollcalendar.ScrollCalendar;
import pl.rafman.scrollcalendar.contract.DateWatcher;
import pl.rafman.scrollcalendar.contract.MonthScrollListener;
import pl.rafman.scrollcalendar.contract.OnDateClickListener;
import pl.rafman.scrollcalendar.contract.State;
import pl.rafman.scrollcalendar.data.CalendarDay;

public class CalendarActivity extends AppCompatActivity {
    private static final String TAG = "SmartBlood";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String MY_BLUETOOTH_SMARTBLOOD_ADDRESS = "00:14:03:05:F3:AA";

    private static final int PERMISSONS_REQUEST_BLUETOOTH = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int VIEW_ANALISIS = 3;

    private static final String NombreArchivoSalvaguarda = "AnalisisSalvaguarda";

    private BluetoothDevice device;
    private BluetoothAdapter adapter;
    private BluetoothConnectionService connection;

    ScrollCalendar scrollcalendar;
    List<Analisis> ListAnalisis = new ArrayList<>();

    // ------------------- METODOS ONCREATE Y ONSTOP ------------------- //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkHaveBluetoothOrExit();

        setContentView(R.layout.activity_calendar);
        scrollcalendar = findViewById(R.id.scrollcalendar);

        CargarDatosAnalisis();

        Intent intent = getIntent();
        if (intent.getBooleanExtra("NewAnalisis", false)) {
            Analisis newAnalisisIntent = (Analisis) intent.getSerializableExtra("Analisis");
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

        // Se detecta CLIC en un dia del calendario
        scrollcalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onCalendarDayClicked(int year, int month, int day) {
                scrollcalendar.refresh();
                //-------ON CLIC REAL-------//
                // Se rellena una lista con los analisis del dia seleccionada
                DiaAnalisis diaAnalisis = new DiaAnalisis(new ArrayList<Analisis>());
                List<Analisis> ListAnalisisIntent = new ArrayList<>();
                for (int i = 0; i < ListAnalisis.size(); i++) {
                    if (year == ListAnalisis.get(i).getTiempo().getYear() && month == ListAnalisis.get(i).getTiempo().getMonth() && day == ListAnalisis.get(i).getTiempo().getDate()) {
                        ListAnalisisIntent.add(ListAnalisis.get(i));
                    }
                }
                // Se encapsula el List en un objeto del tipo DiaAnalisis para poder pasarlo a otra actividad como extra en el intent.
                diaAnalisis.setArrayAnalisis(ListAnalisisIntent);

                // Se crea una intent y se llama a la actividad ResumenAnalisisActivity
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
                if (estaMarcado(year, month, day)) return CalendarDay.TODAY;
                else return CalendarDay.DEFAULT;
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        GuardarDatosAnalisis();
    }

    @Override
    protected void onDestroy() {
        adapter.cancelDiscovery();
        unregisterReceiver(discoveredDevicesReceiver);
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
        switch (item.getItemId()) {
            case R.id.ConexionBT:
                Log.i(TAG, "onOptionsItemSelected: Conexion Bluetooth");
                startBluetoothConnection();
                break;

            case R.id.DesconexionBT:
                Log.i(TAG, "onOptionsItemSelected: Desconexión Bluetooth");
                stopBluetoothConnection();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    //----------------------------------------------------------------//

    //Mira si X dia necesita estar marcado con color rojo.
    private boolean estaMarcado(int year, int month, int day) {
        Log.e("SMARTBLOOD", Integer.toString(ListAnalisis.size()));
        for (int i = 0; i < ListAnalisis.size(); i++) {
            Date t = ListAnalisis.get(i).getTiempo();
            if (year == t.getYear() && month == t.getMonth() && day == t.getDate()) {
                return true;
            }
        }
        return false;
    }

    //--------METODOS PARA LA SALVAGUARDA DE DATOS DE ANALISIS--------//
    private void CargarDatosAnalisis() {
        try {
            FileInputStream FIS = openFileInput(NombreArchivoSalvaguarda);
            Scanner scanner = new Scanner(FIS);
            while (scanner.hasNextLine()) {
                String Analisisscanner = scanner.nextLine();
                String[] parts = Analisisscanner.split("/");
                Analisis lAnalisis = new Analisis(new Date(Integer.valueOf(parts[3]),
                        Integer.valueOf(parts[4]), Integer.valueOf(parts[5]), Integer.valueOf(parts[6]),
                        Integer.valueOf(parts[7])), Integer.valueOf(parts[0]), parts[1], parts[2]);
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
            for (int i = 0; i < ListAnalisis.size(); i++) {
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
                Analisis.getNota1() + "/" +                                         //Part 1
                Analisis.getNota2() + "/" +                                         //Part 2
                sdAnalisis.getYear() + "/" +                        //Part 3
                sdAnalisis.getMonth() + "/" +                        //Part 4
                sdAnalisis.getDate() + "/" +                         //Part 5
                sdAnalisis.getHours() + "/" +                        //Part 6
                sdAnalisis.getMinutes() + "\n";                     //Part 7
        return (ssAnalisis);
    }
    //----------------------------------------------------------------//


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case VIEW_ANALISIS:
                DiaAnalisis diaAnalisisBorrados = (DiaAnalisis) data.getSerializableExtra("ListaAnalisisBorrados");
                for (int i = 0; i < diaAnalisisBorrados.getArrayAnalisis().size(); i++) {
                    for (int j = 0; j < ListAnalisis.size(); j++) {
                        if (ListAnalisis.get(j).getTiempo().equals(diaAnalisisBorrados.getArrayAnalisis().get(i).getTiempo())
                                && ListAnalisis.get(j).getNivelGlucosa() == diaAnalisisBorrados.getArrayAnalisis().get(i).getNivelGlucosa()
                                && ListAnalisis.get(j).getNota2().equals(diaAnalisisBorrados.getArrayAnalisis().get(i).getNota2())
                                && ListAnalisis.get(j).getNota1().equals(diaAnalisisBorrados.getArrayAnalisis().get(i).getNota1())) {
                            ListAnalisis.remove(j);
                            Log.d("SMARTBLOOD", "se ha borrado un analisis");
                        }
                    }
                }
                scrollcalendar.refresh();
                break;

            case REQUEST_ENABLE_BT:
                // Al abrir el cuadro de diálogo sobre activar Bluetooth, el usuario ha cancelado.
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "La aplicación necesita tener Bluetooth activo para poder recibir datos", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /* ------------------- Métodos relacionados con Bluetooth ------------------- */

    void checkHaveBluetoothOrExit() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            // Si no existe, es que no tenemos Bluetooth
            Toast.makeText(this, "Tu dispositivo no dispone de Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    void checkBluetoothIsEnabled() {
        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void registerNewBluetoothDevicesReceiver() {
        IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveredDevicesReceiver, intent);
    }

    private void checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH)) {
                    // TODO: mostrar info (in the fuuuuture...)
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.BLUETOOTH},
                            PERMISSONS_REQUEST_BLUETOOTH);
                }
            }
        } else {
            Log.d(TAG, "checkBluetoothPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSONS_REQUEST_BLUETOOTH:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Bluetooth permission granted!");
                } else {
                    Log.e(TAG, "Bluetooth permission denied.");
                    Toast.makeText(this, "Permiso Bluetooth denegado", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void startBluetoothConnection() {
        if (!adapter.isEnabled()) {
            Toast.makeText(this, "La aplicación requiere tener Bluetooth activado para recibir datos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (connection == null) {
            boolean isPaired = connectToPreviouslyPairedDevice();
            if (!isPaired) {
                Log.d(TAG, "No hay dispositivos emparejados: buscando nuevos...");
                if (!adapter.isDiscovering()) {
                    adapter.startDiscovery();
                }
                return;
            }
        }
        createBluetoothConnection();
    }

    private void createBluetoothConnection() {
        adapter.cancelDiscovery();
        connection = new BluetoothConnectionService(this);
        connection.startClient(device, MY_UUID_INSECURE);
    }

    public void stopBluetoothConnection() {
        if (connection != null) {
            Toast.makeText(this, "Desconectando Bluetooth", Toast.LENGTH_SHORT).show();
            connection.close();
            // connection = null; ???
        }
    }

    private boolean connectToPreviouslyPairedDevice() {
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        for (BluetoothDevice dev : pairedDevices) {
            if (dev.getAddress().equals(MY_BLUETOOTH_SMARTBLOOD_ADDRESS)) {
                Log.d(TAG, "SmartBlood emparejado anteriormente encontrado");
                device = dev;
                return true;
            }
        }
        return false;
    }

    private BroadcastReceiver discoveredDevicesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive.");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: " + dev.getName() + ": " + dev.getAddress());
                if (dev.getAddress().equals(MY_BLUETOOTH_SMARTBLOOD_ADDRESS)) {
                    Log.d(TAG, "SmartBlood encontrado");
                    device = dev;
                    createBluetoothConnection();
                }
            }
        }
    };
}
