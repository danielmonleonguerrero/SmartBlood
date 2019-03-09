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

public class ResumenAnalisisActivity extends AppCompatActivity {

    private TextView diaView;
    private TextView mesView;
    private TextView anyoView;

    String[] MesesDelAnyo = new String[]{
     "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Setiembre", "Octubre", "Noviembre", "Diciembre"
    };
    DiaAnalisis diaAnalisis;
    private Adapter adapter;
    private RecyclerView analisis_list_view;

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
        diaAnalisis =(DiaAnalisis)intent.getSerializableExtra("DiaAnalisis");
        diaView.setText(Integer.toString(diaAnalisis.getDia()));
        mesView.setText(MesesDelAnyo[diaAnalisis.getMes()]);
        anyoView.setText(Integer.toString(diaAnalisis.getAnyo()));


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
            Analisis analisis = diaAnalisis.getArrayAnalisi().get(position);
            holder.nivelGlucosaView.setText( Integer.toString(analisis.getNivelGlucosa()));
            holder.horaView.setText(Integer.toString(analisis.getHora()));
            holder.minView.setText(Integer.toString(analisis.getMin()));
            holder.nota1.setText(analisis.getNota1());
            holder.nota2.setText(analisis.getNota2());
            //Glide.with(this).load("file:///android_asset/burger.png").into(holder.bloodropView);

        }
        public int getItemCount(){
            return diaAnalisis.getArrayAnalisi().size();
        }
    }

}
