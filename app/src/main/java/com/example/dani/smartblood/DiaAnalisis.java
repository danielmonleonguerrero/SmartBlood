package com.example.dani.smartblood;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DiaAnalisis implements Serializable {
    public void setArrayAnalisis(List<Analisis> arrayAnalisis) {
        ArrayAnalisis = arrayAnalisis;
    }

    public List<Analisis> getArrayAnalisis() {

        return ArrayAnalisis;
    }

    public DiaAnalisis(List<Analisis> arrayAnalisis) {

        ArrayAnalisis = arrayAnalisis;
    }

    List<Analisis> ArrayAnalisis = new ArrayList<>();
}
