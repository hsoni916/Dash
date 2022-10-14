package com.example.dash;

public class Label {
    String Barcode, GW, NW, LW, EC, HUID, HMStandard, Date;
    Double Purity;
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

    public double getPurity() {
        return Purity;
    }

    public void setPurity(double purity) {
        Purity = purity;
    }

    public String getName() {
        return Name;
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

    public String getHMStandard() {
        return HMStandard;
    }

    public void setHMStandard(String HMStandard) {
        this.HMStandard = HMStandard;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setName(String name){
        Name = name;
    }

    public String getInventory(){
        return getBarcode() + ":" + getHMStandard() + ":" + getName() + ":" + getGW()
                + ":" + getLW() + ":" + getNW() + ":" + getEC() + ":" + getHUID()
                + ":" + getDate();
    }
}
