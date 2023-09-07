package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

public class ParlourCustomersModel {
    String customerName;
    String customerEmail;
    String mobile;
    String parlourName;
    String parlourEmail;
    String service;
    String price;
    String date;
    String time;
    String customerToken;

    ParlourCustomersModel(String customerName, String mobile, String service, String date) {

        this.customerName = customerName;
        this.mobile = mobile;
        this.service = service;
        this.date = date;
    }
    ParlourCustomersModel(){}

    public ParlourCustomersModel(String customerName,String customerEmail,  String service) {

        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.service = service;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getParlourName() {
        return parlourName;
    }

    public void setParlourName(String parlourName) {
        this.parlourName = parlourName;
    }

    public String getParlourEmail() {
        return parlourEmail;
    }

    public void setParlourEmail(String parlourEmail) {
        this.parlourEmail = parlourEmail;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCustomerToken() {
        return customerToken;
    }

    public void setCustomerToken(String customerToken) {
        this.customerToken = customerToken;
    }
}
