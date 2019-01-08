package com.gadgetlab.rduuke.winnote.entity;

import java.util.ArrayList;

public class Calculate {

    private int rated_average;
    private double note = 0;
    private ArrayList<Double> notes;
    private ArrayList<Double> averages;
    private double average_total;

    public Calculate(ArrayList<Double> notes, ArrayList<Double> averages) {
        this.notes = notes;
        this.averages = averages;
    }

    public double currentNote()
    {
        for(int a = 0; a < this.notes.size(); a++) {
            this.note = this.notes.get(a)*(this.averages.get(a)/100) + this.note;
        }
        return this.note;
    }

    private double averageTotal()
    {
        for (int a = 0; a < this.averages.size(); a++) {

            this.average_total = this.averages.get(a) + this.average_total;
        }
        return this.average_total;
    }

    public double necessaryNote()
    {
        double limit = 3 - this.currentNote();

        double promedio = (100 - this.averageTotal())/100;

        return limit/promedio;
    }
}
