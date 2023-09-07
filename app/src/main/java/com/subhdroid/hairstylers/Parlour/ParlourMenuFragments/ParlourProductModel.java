package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

public class ParlourProductModel {
    String prodName;
    String prodPrice;
    String description;
    String review;
    String parlourName;
    String parlourEmail;


    public ParlourProductModel(String prodName,
                               String prodPrice,
                               String description,
                               String review,
                               String parlourName,
                               String parlourEmail) {

        this.prodName = prodName;
        this.prodPrice = prodPrice;
        this.description = description;
        this.review = review;
        this.parlourName = parlourName;
        this.parlourEmail = parlourEmail;
    }

    ParlourProductModel() {
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(String prodPrice) {
        this.prodPrice = prodPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
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
}
