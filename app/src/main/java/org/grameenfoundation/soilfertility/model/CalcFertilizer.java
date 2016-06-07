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
@DatabaseTable(tableName = "calculation_fertilizer")
public class CalcFertilizer extends BaseDaoEnabled implements Serializable {

    @DatabaseField(generatedId = true)
    @Expose
    private int id;

    @DatabaseField(foreign = true, columnName = "calculation_id")
    //@SerializedName("Calculation")
    private transient Calc calculation;

    @DatabaseField(foreign = true)
    @Expose
    @SerializedName("Fertilizer")
    private Fertilizer fertilizer;

    @DatabaseField
    @Expose
    @SerializedName("Price")
    private Integer price;

    @DatabaseField
    @Expose
    @SerializedName("TotalRequired")
    private Double totalRequired;

    public CalcFertilizer(){

    }

    public CalcFertilizer(Calc parentCalculation){
        setCalculation(parentCalculation);
    }

    public Double getTotalRequired() {
        return totalRequired;
    }

    public void setTotalRequired(Double totalRequired) {
        this.totalRequired = totalRequired;
    }

    public Fertilizer getFertilizer() {
        return fertilizer;
    }

    public void setFertilizer(Fertilizer fertilizer) {
        this.fertilizer = fertilizer;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Calc getCalculation() {
        return calculation;
    }

    public void setCalculation(Calc calculation) {
        this.calculation = calculation;
    }
}
