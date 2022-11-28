package com.subhdroid.hairstylers.Parlour;

public class ParlourModel {
    String parlourName;
    String subtitle;
    String phone;
    String email;
    String password;
    String parlourTypes;
    String openingTime;
    String closingTime;
    String address;
    String pincode;
    String photo;

    ParlourModel(String parlourName, String subtitle, String phone, String email, String password
            , String parlourTypes, String openingTime, String closingTime,
                 String address, String pincode,String photo) {
        this.parlourName = parlourName;
        this.subtitle = subtitle;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.parlourTypes = parlourTypes;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.address = address;
        this.pincode = pincode;
        this.photo = photo;
    }

    ParlourModel(){}

    public String getParlourName() {
        return parlourName;
    }

    public void setParlourName(String parlourName) {
        this.parlourName = parlourName;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getParlourTypes() {
        return parlourTypes;
    }

    public void setParlourTypes(String parlourTypes) {
        this.parlourTypes = parlourTypes;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
