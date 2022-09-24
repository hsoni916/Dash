package com.example.dash;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrintData {

    List<SundryItem> sundryItemList = new ArrayList<>();
    String CustomerName, Phone;
    List<String> MOP;
    String date, BillNo;
    double Discount;

    public String getBillNo() {
        return BillNo;
    }

    public void setBillNo(String billNo) {
        BillNo = billNo;
    }

    public double getDiscount() {
        return Discount;
    }

    public void setDiscount(double discount) {
        Discount = discount;
    }

    public List<SundryItem> getSundryItemList() {
        return sundryItemList;
    }

    public void setSundryItemList(List<SundryItem> sundryItemList) {
        this.sundryItemList = sundryItemList;
    }

    public List<String> getMOP() {
        return MOP;
    }

    public void setMOP(List<String> MOP) {
        this.MOP = MOP;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
