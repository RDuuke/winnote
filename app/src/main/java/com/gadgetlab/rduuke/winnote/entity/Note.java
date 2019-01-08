package com.gadgetlab.rduuke.winnote.entity;

public class Note {
    protected Double nota;
    protected Double promedio;
    protected String key;
    private String Materia;

    public void setMateria(String materia) {
        Materia = materia;
    }

    public Note() {
    }

    public String getMateria() {
        return Materia;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Note(Double nota, Double promedio) {
        this.nota = nota;

        this.promedio = promedio;
    }

    public double getNota() {
        return nota;
    }

    public double getPromedio() {
        return promedio;
    }
}
