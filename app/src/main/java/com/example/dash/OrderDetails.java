package com.example.dash;

import java.util.List;

public class OrderDetails {
    String CustomerName, PhoneNumber, OrderDate, DeliveryDate, FileName;
    List<String> Remarks;
    List<String> Items;
    List<String> SamplePhotos;
    List<Double> Weights;
    private String OrderNumber;
    boolean RateFix = false;
    double rate = 0.00;

    public boolean isRateFix() {
        return RateFix;
    }

    public void setRateFix(boolean rateFix) {
        RateFix = rateFix;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        DeliveryDate = deliveryDate;
    }

    public List<String> getRemarks() {
        return Remarks;
    }

    public void setRemarks(List<String> remarks) {
        Remarks = remarks;
    }

    public List<String> getItems() {
        return Items;
    }

    public void setItems(List<String> items) {
        Items = items;
    }

    public List<String> getSamplePhotos() {
        return SamplePhotos;
    }

    public void setSamplePhotos(List<String> samplePhotos) {
        SamplePhotos = samplePhotos;
    }

    public List<Double> getWeights() {
        return Weights;
    }

    public void setWeights(List<Double> weights) {
        Weights = weights;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public void setOrderNumber(String timeStamp) {
        OrderNumber = timeStamp;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }
}
