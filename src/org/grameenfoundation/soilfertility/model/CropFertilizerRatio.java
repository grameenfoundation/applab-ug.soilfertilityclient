package org.grameenfoundation.soilfertility.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Copyright (c) 2013 AppLab, Grameen Foundation
 * Created by: David
 */
public class CropFertilizerRatio implements Serializable {

    @SerializedName("Crop")
    private String crop;
    @SerializedName("Fert")
    private String fert;
    @SerializedName("Amt")
    private Double amt;

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getFert() {
        return fert;
    }

    public void setFert(String fert) {
        this.fert = fert;
    }

    public Double getAmt() {
        return amt;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }
}
