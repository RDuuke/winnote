package com.gadgetlab.rduuke.winnote.entity;


import android.icu.text.DecimalFormat;

import java.io.Serializable;

public class MateriasModel implements Serializable {
    protected String name, key;

    protected Double note_necessary, note_final;

    public void setNote_necessary(Double note_necessary) {
        this.note_necessary = note_necessary;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setNote_final(Double note_final) {
        this.note_final = note_final;
    }

    public String getKey() {
        return key;
    }

    /*public Subject(String name, Double note_necessary, Double note_final) {
        this.name = name;

        this.note_necessary = note_necessary;
        this.note_final = note_final;
    }*/

    public MateriasModel() {
    }

    public MateriasModel(String name) {
        this.name = name;
    }

    public String getNoteNecessary() {
        return formatDecimal(note_necessary);
    }

    public String getNoteFinal() {
        return formatDecimal(note_final);
    }

    public String getName() {
        return name;
    }
    protected String formatDecimal(double numero)
    {
        return String.format("%.2f", numero);
    }
}
