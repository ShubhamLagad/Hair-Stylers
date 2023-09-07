package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

public class TransactionModel {
    String customerName;
    String customerEmail;
    String mobile;
    String parlourEmail;
    String parlourName;
    String workerEmail;
    String service;
    String price;
    String date;
    String time;
    String customerToken;

    public TransactionModel(String customerName,String customerEmail, String mobile,
                            String parlourEmail,
                            String parlourName, String workerEmail, String service,
                            String price, String date, String time, String customerToken) {

        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.mobile = mobile;
        this.parlourEmail = parlourEmail;
        this.parlourName = parlourName;
        this.workerEmail = workerEmail;
        this.service = service;
        this.price = price;
        this.date = date;
        this.time = time;
        this.customerToken = customerToken;

    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getParlourName() {
        return parlourName;
    }

    public void setParlourName(String parlourName) {
        this.parlourName = parlourName;
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

    public String getParlourEmail() {
        return parlourEmail;
    }

    public void setParlourEmail(String parlourEmail) {
        this.parlourEmail = parlourEmail;
    }

    public String getWorkerEmail() {
        return workerEmail;
    }

    public void setWorkerEmail(String workerEmail) {
        this.workerEmail = workerEmail;
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
