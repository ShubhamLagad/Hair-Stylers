package com.subhdroid.hairstylers.Customer;

public class CustomerModel {
    String custName;
    String custPhone;
    String custEmail;
    String custPassword;
    String custGender;
    String custPincode;
    String fcmToken;

    CustomerModel(String custName, String custPhone, String custEmail, String custPassword,
                  String custGender, String custPincode, String fcmToken) {
        this.custName = custName;
        this.custPhone = custPhone;
        this.custEmail = custEmail;
        this.custPassword = custPassword;
        this.custGender = custGender;
        this.custPincode = custPincode;
        this.fcmToken = fcmToken;
    }

    CustomerModel() {
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustPhone() {
        return custPhone;
    }

    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }

    public String getCustEmail() {
        return custEmail;
    }

    public void setCustEmail(String custEmail) {
        this.custEmail = custEmail;
    }

    public String getCustPassword() {
        return custPassword;
    }

    public void setCustPassword(String custPassword) {
        this.custPassword = custPassword;
    }

    public String getCustGender() {
        return custGender;
    }

    public void setCustGender(String custGender) {
        this.custGender = custGender;
    }

    public String getCustPincode() {
        return custPincode;
    }

    public void setCustPincode(String custPincode) {
        this.custPincode = custPincode;
    }


    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

}
