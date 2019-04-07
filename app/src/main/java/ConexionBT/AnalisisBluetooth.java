package ConexionBT;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.example.dani.smartblood.RegistrarAnalisis;

import java.io.Serializable;

public class AnalisisBluetooth extends AppCompatActivity {

    public AnalisisBluetooth(int glucosa) {
        this.glucosa = glucosa;
    }

    int glucosa;

    public void registrarAnalisis() {
        Intent intent = new Intent(getApplication().getBaseContext(),RegistrarAnalisis.class);
        intent.putExtra("GlucosaBluetooth", glucosa);
        startActivity(intent);
    }
}
