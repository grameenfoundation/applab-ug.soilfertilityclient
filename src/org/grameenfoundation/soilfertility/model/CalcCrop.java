package org.grameenfoundation.soilfertility.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Copyright (c) 2013 AppLab, Grameen Foundation
 * Created by: David
 */
@DatabaseTable(tableName = "calculation_crop")
public class CalcCrop extends BaseDaoEnabled implements Serializable {

    @DatabaseField(generatedId = true)
    @Expose
    @SerializedName("Id")
    private int id;

    @DatabaseField(foreign = true, columnName = "calculation_id")
    //@SerializedName("Calculation")
    private Calc calculation;

    @DatabaseField(foreign = true)
    @Expose
    @SerializedName("Crop")
    private Crop crop;

    @DatabaseField
    @Expose
    @SerializedName("Area")
    private Double area;

    @DatabaseField
    @Expose
    @SerializedName("Profit")
    private Double profit;

    @DatabaseField
    @Expose
    @SerializedName("YieldIncrease")
    private Double yieldIncrease;

    @DatabaseField
    @Expose
    @SerializedName("NetReturns")
    private Double netReturns;

    public CalcCrop(){

    }

    public CalcCrop(Calc parentCalculation){
        setCalculation(parentCalculation);
    }

    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
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

    public Calc getCalculation() {
        return calculation;
    }

    public void setCalculation(Calc calculation) {
        this.calculation = calculation;
    }
}
