package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.subhdroid.hairstylers.Parlour.ParlourModel;

public class ParlourSlotsModel {
    String slotTime;
    String slotCustomerName;
    String customerEmail;
    String parlourEmail;
    String service;
    String customerPhone;
    String customerToken;

    public ParlourSlotsModel(String slotTime, String slotCustomerName,
                             String customerEmail, String parlourEmail, String service,
                             String customerPhone, String customerToken) {
        this.slotTime = slotTime;
        this.slotCustomerName = slotCustomerName;
        this.customerEmail = customerEmail;
        this.parlourEmail = parlourEmail;
        this.service = service;
        this.customerPhone = customerPhone;
        this.customerToken = customerToken;
    }

    public ParlourSlotsModel() {
    }


    public String getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(String slotTime) {
        this.slotTime = slotTime;
    }

    public String getSlotCustomerName() {
        return slotCustomerName;
    }

    public void setSlotCustomerName(String slotCustomerName) {
        this.slotCustomerName = slotCustomerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
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

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerToken() {
        return customerToken;
    }

    public void setCustomerToken(String customerToken) {
        this.customerToken = customerToken;
    }
}
