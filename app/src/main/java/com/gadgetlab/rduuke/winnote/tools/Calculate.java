package com.gadgetlab.rduuke.winnote.tools;

import java.util.ArrayList;

public class Calculate {

    private double note;
    private ArrayList<Double> notes;
    private ArrayList<Double> averages;
    private double average_total;

    public Calculate(ArrayList<Double> notes, ArrayList<Double> averages) {
        this.notes = notes;
        this.averages = averages;
    }

    public double currentNote()
    {
        this.note = 0;
        for(int a = 0; a < this.notes.size(); a++) {
            this.note = this.notes.get(a)*(this.averages.get(a)/100) + this.note;
        }
        return this.note;
    }

    public double averageTotal()
    {
        this.average_total = 0;
        for (int a = 0; a < this.averages.size(); a++) {

            this.average_total = this.averages.get(a) + this.average_total;
        }
        return this.average_total;
    }

    public double necessaryNote()
    {
        double limit = 3 - this.note;

        double promedio = (100 - averageTotal())/100;
        if (promedio != 0) {
            return limit/promedio;
        }
        return 0;
    }
}
