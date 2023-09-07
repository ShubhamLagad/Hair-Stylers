package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

public class ParlourServiceModel {
    String parlour_email;
    String service_name;
    String price;

    public ParlourServiceModel(String parlour_email,String service_name,String price){
        this.parlour_email = parlour_email;
        this.service_name = service_name;
        this.price = price;
    }

    ParlourServiceModel(){}

    public String getParlour_email() {
        return parlour_email;
    }

    public void setParlour_email(String parlour_email) {
        this.parlour_email = parlour_email;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
