package com.example.dani.smartblood;

public class DiaAnalisis {
    public DiaAnalisis(int dia, int mes, int anyo, int mediaDiariaGlucosa, Analisis analisis) {
        this.dia = dia;
        this.mes = mes;
        this.anyo = anyo;
        this.mediaDiariaGlucosa = mediaDiariaGlucosa;
        this.analisis = analisis;
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

    public Analisis getAnalisis() {
        return analisis;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public void setAnyo(int anyo) {
        this.anyo = anyo;
    }

    public void setMediaDiariaGlucosa(int mediaDiariaGlucosa) {
        this.mediaDiariaGlucosa = mediaDiariaGlucosa;
    }

    public void setAnalisis(Analisis analisis) {
        this.analisis = analisis;
    }

    int dia, mes, anyo, mediaDiariaGlucosa;
    Analisis analisis;

}
