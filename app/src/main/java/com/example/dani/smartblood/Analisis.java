package com.example.dani.smartblood;

import java.io.Serializable;

public class Analisis implements Serializable {
    public int getHora() {
        return hora;
    }

    public int getNivelGlucosa() {
        return nivelGlucosa;
    }

    public String getNota1() {
        return nota1;
    }

    public String getNota2() {
        return nota2;
    }

    public void setHora(int hora) {

        this.hora = hora;
    }

    public void setNivelGlucosa(int nivelGlucosa) {
        this.nivelGlucosa = nivelGlucosa;
    }

    public void setNota1(String nota1) {
        this.nota1 = nota1;
    }

    public void setNota2(String nota2) {
        this.nota2 = nota2;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMin() {

        return min;
    }

    public Analisis(int hora, int min, int nivelGlucosa, String nota1, String nota2) {

        this.hora = hora;
        this.min =min;

        this.nivelGlucosa = nivelGlucosa;
        this.nota1 = nota1;
        this.nota2 = nota2;
    }

    int hora, min, nivelGlucosa;
    String nota1, nota2;
}
