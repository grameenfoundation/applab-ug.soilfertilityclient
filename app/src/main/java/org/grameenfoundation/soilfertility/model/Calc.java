package org.grameenfoundation.soilfertility.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Copyright (c) 2013 AppLab, Grameen Foundation
 * Created by: David
 */
@DatabaseTable(tableName = "calculation")
public class Calc extends BaseDaoEnabled implements Serializable {

    public static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");

    @DatabaseField(id = true)
    @Expose
    @SerializedName("Id")
    private String id;

    @DatabaseField(columnName = "dateCreated")
    private Date dateCreated;

    @DatabaseField
    private boolean solved;

    @DatabaseField
    @Expose
    @SerializedName("Region")
    private int region;

    @DatabaseField
    @Expose
    @SerializedName("FarmerName")
    private String farmerName;

    @DatabaseField
    @Expose
    @SerializedName("Units")
    private String units;

    @DatabaseField
    @Expose
    @SerializedName("Imei")
    private String imei;

    @DatabaseField
    @Expose
    @SerializedName("AmtAvailable")
    private Double amtAvailable;

    @DatabaseField
    @Expose
    @SerializedName("TotNetReturns")
    private Double totNetReturns;

    @ForeignCollectionField(eager = true)
    @Expose
    @SerializedName("CalcCrops")
    private Collection<CalcCrop> calcCrops = new ArrayList<CalcCrop>();

    @ForeignCollectionField(eager = true)
    @Expose
    @SerializedName("CalcFertilizers")
    private Collection<CalcFertilizer> calcFertilizers = new ArrayList<CalcFertilizer>();

    @ForeignCollectionField(eager = true)
    @Expose
    @SerializedName("CalcCropFertilizerRatios")
    private Collection<CalcCropFertilizerRatio> cropFerts = new ArrayList<CalcCropFertilizerRatio>();

    public Calc() {
        // all persisted classes must define a no-arg constructor
        // with at least package visibility
        setId(java.util.UUID.randomUUID().toString());
        setDateCreated(Calendar.getInstance().getTime());
    }

    public Collection<CalcFertilizer> getCalcFertilizers() {
        return calcFertilizers;
    }

    public void setCalcFertilizers(List<CalcFertilizer> calcFertilizers) {
        for (CalcFertilizer calcFertilizer : calcFertilizers) {
            if (calcFertilizer.getCalculation() == null) {
                calcFertilizer.setCalculation(this);
            }
            this.calcFertilizers.add(calcFertilizer);
        }
    }

    public Collection<CalcCrop> getCalcCrops() {
        return calcCrops;
    }

    public void setCalcCrops(List<CalcCrop> calcCrops) {
        for (CalcCrop calcCrop : calcCrops) {
            if (calcCrop.getCalculation() == null) {
                calcCrop.setCalculation(this);
            }
            this.calcCrops.add(calcCrop);
        }
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

    public Collection<CalcCropFertilizerRatio> getCropFerts() {
        return cropFerts;
    }

    public void setCropFerts(List<CalcCropFertilizerRatio> cropFerts) {
        for (CalcCropFertilizerRatio ratio : cropFerts) {
            if (ratio.getCalculation() == null) {
                ratio.setCalculation(this);
            }
            this.cropFerts.add(ratio);
        }
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

    @Override
    public String toString() {
        return getFarmerName() + " \t" + dateFormat.format(getDateCreated());
    }

    public int getRegion() {return region;}

    public void setRegion(int region) {
        this.region = region;
    }

    public String getUnits() {return units;}

    public void setUnits(String units) {
        this.units = units;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    /**
     * returns the total land size for all crops
     * @return  total land size
     */
    public Double getTotalLandSize(){
        Double total = 0d;
        for(CalcCrop crop: getCalcCrops()){
            total += crop.getArea();
        }
        return  total;
    }
}
