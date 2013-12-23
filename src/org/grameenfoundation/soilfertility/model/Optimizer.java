package org.grameenfoundation.soilfertility.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2013 AppLab, Grameen Foundation
 * Created by: David
 */
public class Optimizer implements Serializable {

    @SerializedName("Id")
    private String id;
    @SerializedName("FarmerName")
    private String farmerName;
    @SerializedName("Imei")
    private String imei;
    @SerializedName("AmtAvailable")
    private Double amtAvailable;
    @SerializedName("TotNetReturns")
    private Double totNetReturns;
    @SerializedName("Crops")
    private List<Crop> crops = new ArrayList<Crop>();
    @SerializedName("Fertilizers")
    private List<Fertilizer> fertilizers = new ArrayList<Fertilizer>();
    @SerializedName("CropFerts")
    private List<CropFertilizerRatio> cropFerts = new ArrayList<CropFertilizerRatio>();

    public List<Fertilizer> getFertilizers() {
        return fertilizers;
    }

    public void setFertilizers(List<Fertilizer> fertilizers) {
        this.fertilizers = fertilizers;
    }

    public List<Crop> getCrops() {
        return crops;
    }

    public void setCrops(List<Crop> crops) {
        this.crops = crops;
    }

    public Double getTotNetReturns() {
        return totNetReturns;
    }

    public void setTotNetReturns(Double totNetReturns) {
        this.totNetReturns = totNetReturns;
    }

    public Double getAmtAvailable() {
        return amtAvailable;
    }

    public void setAmtAvailable(Double amtAvailable) {
        this.amtAvailable = amtAvailable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CropFertilizerRatio> getCropFerts() {
        return cropFerts;
    }

    public void setCropFerts(List<CropFertilizerRatio> cropFerts) {
        this.cropFerts = cropFerts;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getFarmerName() {

        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }
}
