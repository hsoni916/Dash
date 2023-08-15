package com.example.dash;

public class SundryItem {
    String name, typeOf, LabourType;
    double GW, LW, NW, Wastage, EC, labour, rate;
    Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getLabourType() {
        return LabourType;
    }

    public void setLabourType(String labourType) {
        LabourType = labourType;
    }

    public double getLabour() {
        return labour;
    }

    public void setLabour(double labour) {
        this.labour = labour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeOf() {
        return typeOf;
    }

    public void setTypeOf(String typeOf) {
        this.typeOf = typeOf;
    }

    public double getGW() {
        return GW;
    }

    public void setGW(double GW) {
        this.GW = GW;
    }

    public double getLW() {
        return LW;
    }

    public void setLW(double LW) {
        this.LW = LW;
    }

    public double getNW() {
        return NW;
    }

    public void setNW(double NW) {
        this.NW = NW;
    }

    public double getWastage() {
        return Wastage;
    }

    public void setWastage(double wastage) {
        Wastage = wastage;
    }

    public double getEC() {
        return EC;
    }

    public void setEC(double EC) {
        this.EC = EC;
    }
}
