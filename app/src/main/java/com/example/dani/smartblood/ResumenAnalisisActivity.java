package com.example.dani.smartblood;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResumenAnalisisActivity extends AppCompatActivity {

    private TextView diaView;
    private TextView mesView;
    private TextView anyoView;

    String[] MesesDelAnyo = new String[]{
     "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Setiembre", "Octubre", "Noviembre", "Diciembre"
    };
    Analisis analisis =new Analisis(new Date(0,0,0),0,"","");
    List<Analisis> ListAnalisis =new ArrayList<>();
    private Adapter adapter;
    private RecyclerView analisis_list_view;
    DiaAnalisis diaAnalisis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_analisis);

        diaView = findViewById(R.id.diaView);
        mesView = findViewById(R.id.mesView);
        anyoView = findViewById(R.id.anyoView);
        analisis_list_view = findViewById(R.id.analisis_list_view);
        analisis_list_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        analisis_list_view.setAdapter(adapter);

        Intent intent = getIntent();
        diaAnalisis =(DiaAnalisis)intent.getSerializableExtra("diaAnalisis");
        ListAnalisis=diaAnalisis.getArrayAnalisis();
        Log.e("SMARTBLOOD", "Tamanyo en resumen: " + String.valueOf(ListAnalisis.size()));
        diaView.setText(Integer.toString(ListAnalisis.get(0).getTiempo().getDate()));
        mesView.setText(MesesDelAnyo[ListAnalisis.get(0).getTiempo().getMonth()]);
        anyoView.setText(Integer.toString(ListAnalisis.get(0).getTiempo().getYear()));


    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nivelGlucosaView, horaView, minView, nota1, nota2;
        private ImageView bloodropView;
        public ViewHolder(View itemView){
            super(itemView);
            nivelGlucosaView=itemView.findViewById(R.id.nivelGlucosaView);
            horaView=itemView.findViewById(R.id.horaView);
            minView=itemView.findViewById(R.id.minView);
            nota1=itemView.findViewById(R.id.nota1View);
            nota2=itemView.findViewById(R.id.nota2View);
            bloodropView=itemView.findViewById(R.id.blooddropView);
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder>{
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View itemView = getLayoutInflater().inflate(R.layout.item_analisis_view, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder,int position){
            Analisis analisisHolder = ListAnalisis.get(position);
            holder.nivelGlucosaView.setText( Integer.toString(analisisHolder.getNivelGlucosa()));
            holder.horaView.setText(Integer.toString(analisisHolder.getTiempo().getHours()));
            holder.minView.setText(Integer.toString(analisisHolder.getTiempo().getMinutes()));
            holder.nota1.setText(analisisHolder.getNota1());
            holder.nota2.setText(analisisHolder.getNota2());
            //Glide.with(this).load("file:///android_asset/burger.png").into(holder.bloodropView);
        }
        public int getItemCount(){
            return ListAnalisis.size();
        }
    }

}
