package com.example.dani.smartblood;

import java.io.Serializable;
import java.util.ArrayList;

public class DiaAnalisis implements Serializable {
    public DiaAnalisis(int dia, int mes, int anyo, int mediaDiariaGlucosa, ArrayList<Analisis> arrayAnalisi ) {
        this.dia = dia;
        this.mes = mes;
        this.anyo = anyo;
        this.mediaDiariaGlucosa = mediaDiariaGlucosa;
        ArrayAnalisi = arrayAnalisi;
    }

    public int getDia() {
        return dia;
    }

    public int getMes() {
        return mes;
    }

    public int getAnyo() {
        return anyo;
    }

    public int getMediaDiariaGlucosa() {
        return mediaDiariaGlucosa;
    }

    public void setArrayAnalisi(ArrayList<Analisis> arrayAnalisi) {
        ArrayAnalisi = arrayAnalisi;
    }

    public ArrayList<Analisis> getArrayAnalisi() {
        return ArrayAnalisi;
    }

    int dia, mes, anyo, mediaDiariaGlucosa;
    ArrayList<Analisis> ArrayAnalisi = new ArrayList<>();
}
