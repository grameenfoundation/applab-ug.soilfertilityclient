package org.grameenfoundation.soilfertility.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Copyright (c) 2013 AppLab, Grameen Foundation
 * Created by: David
 */
public class Fertilizer implements Serializable {

    @SerializedName("Name")
    private String name;
    @SerializedName("Price")
    private int price;
    @SerializedName("TotalRequired")
    private Double totalRequired;

    public Double getTotalRequired() {
        return totalRequired;
    }

    public void setTotalRequired(Double totalRequired) {
        this.totalRequired = totalRequired;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
