package com.example.dani.smartblood;

public class Analisis {
    public int getHoramin() {
        return horamin;
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

    public void setHoramin(int horamin) {

        this.horamin = horamin;
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

    public Analisis(int horamin, int nivelGlucosa, String nota1, String nota2) {

        this.horamin = horamin;
        this.nivelGlucosa = nivelGlucosa;
        this.nota1 = nota1;
        this.nota2 = nota2;
    }

    int horamin, nivelGlucosa;
    String nota1, nota2;
}
