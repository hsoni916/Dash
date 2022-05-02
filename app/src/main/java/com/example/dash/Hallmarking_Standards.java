package com.example.dash;

public class Hallmarking_Standards {
    String Label;
    double BasePurity;

    public Hallmarking_Standards(String label, double basePurity) {
        Label = label;
        BasePurity = basePurity;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public double getBasePurity() {
        return BasePurity;
    }

    public void setBasePurity(double basePurity) {
        BasePurity = basePurity;
    }
}
