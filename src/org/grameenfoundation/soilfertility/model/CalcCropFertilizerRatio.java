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
@DatabaseTable(tableName = "calccropfertilizerratio")
public class CalcCropFertilizerRatio extends BaseDaoEnabled implements Serializable {

    @DatabaseField(generatedId = true)
    @Expose
    private int id;

    @DatabaseField(foreign = true, columnName = "calculation_id")
    //@SerializedName("Calculation")
    private Calc calculation;

    @DatabaseField(foreign = true)
    @Expose
    @SerializedName("Crop")
    private Crop calcCrop;

    @DatabaseField(foreign = true)
    @Expose
    @SerializedName("Fert")
    private Fertilizer fert;

    @DatabaseField
    @Expose
    @SerializedName("Amt")
    private Double amt;

    private static final Double ACRE = 2.47105;
    private static boolean is_converted = false;

    public Crop getCalcCrop() {
        return calcCrop;
    }

    public void setCalcCrop(Crop calcCrop) {
        this.calcCrop = calcCrop;
    }

    public Fertilizer getFert() {
        return fert;
    }

    public void setFert(Fertilizer fert) {
        this.fert = fert;
    }

    public Double getAmt() {
        return amt;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }

    /**
     * a call to this method changes the amount from Kg/Ha to Kg/Acre
     * This is necessary because processing is done in the former and
     * yet the user will need to be shown the later
     */
    public void changeAmtToKgsPerAcre() {
        if (!is_converted) {
            this.setAmt(getAmt() / ACRE);
            is_converted = true;
        }
    }

    public Calc getCalculation() {
        return calculation;
    }

    public void setCalculation(Calc calculation) {
        this.calculation = calculation;
    }
}
