package com.example.dash;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrintData {

    List<SundryItem> sundryItemList = new ArrayList<>();
    String CustomerName, Phone;
    List<String> MOP;
    Map<String,Double> PaymentDetails;
    String date, BillNo;
    double Discount;

    public Map<String, Double> getPaymentDetails() {
        return PaymentDetails;
    }

    public void setPaymentDetails(Map<String, Double> paymentDetails) {
        PaymentDetails = paymentDetails;
    }

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
