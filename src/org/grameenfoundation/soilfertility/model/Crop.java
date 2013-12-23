package org.grameenfoundation.soilfertility.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Copyright (c) 2013 AppLab, Grameen Foundation
 * Created by: David
 */
public class Crop implements Serializable {

    @SerializedName("Name")
    private String name;
    @SerializedName("Area")
    private int area;
    @SerializedName("Profit")
    private int profit;
    @SerializedName("YieldIncrease")
    private Double yieldIncrease;
    @SerializedName("NetReturns")
    private Double netReturns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public Double getYieldIncrease() {
        return yieldIncrease;
    }

    public void setYieldIncrease(Double yieldIncrease) {
        this.yieldIncrease = yieldIncrease;
    }

    public Double getNetReturns() {
        return netReturns;
    }

    public void setNetReturns(Double netReturns) {
        this.netReturns = netReturns;
    }
}
