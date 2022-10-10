package com.example.dash;

public class Label {
    String Barcode, GW, NW, LW, EC, HUID, Touch, Date;
    private String Name;

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getGW() {
        return GW;
    }

    public void setGW(String GW) {
        this.GW = GW;
    }

    public String getNW() {
        return NW;
    }

    public void setNW(String NW) {
        this.NW = NW;
    }

    public String getLW() {
        return LW;
    }

    public void setLW(String LW) {
        this.LW = LW;
    }

    public String getEC() {
        return EC;
    }

    public void setEC(String EC) {
        this.EC = EC;
    }

    public String getHUID() {
        return HUID;
    }

    public void setHUID(String HUID) {
        this.HUID = HUID;
    }

    public String getTouch() {
        return Touch;
    }

    public void setTouch(String touch) {
        Touch = touch;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }


    public void clearself() {
        this.setBarcode(null);
        this.setTouch(null);
        this.setEC(null);
        this.setDate(null);
        this.setNW(null);
        this.setGW(null);
        this.setLW(null);
        this.setHUID(null);
    }

    public String getname() {
        return Name;
    }

    public void setName(String name){
        Name = name;
    }
}
