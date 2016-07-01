package org.grameenfoundation.soilfertility.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Copyright (c) 2016 AppLab, Grameen Foundation
 * Created by: Josh
 */

@DatabaseTable(tableName = "region_crop")
public class RegionCrop  implements Serializable {

    @DatabaseField(generatedId = true)
    @Expose
    @SerializedName("Id")
    private int id;

    @DatabaseField
    @Expose
    @SerializedName("RegionId")
    private int regionId;

    @DatabaseField(foreign = true,foreignAutoRefresh =true)
    @Expose
    @SerializedName("Crop")
    private Crop crop;

    public RegionCrop() {

    }

    public RegionCrop(int regionId, Crop Crop) {
       setRegion(regionId);
        setCrop(Crop);
    }


     public int getRegion() {
        return regionId;
     }

    public void setRegion(int regionId) {
         this.regionId = regionId;
    }

    public Crop getCrop() {
        return crop;
     }

     public void setCrop(Crop crop) {
         this.crop = crop;
    }

//    @Override
//    public String toString() {
//        return getName();
//    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCrop() == null) ? 0 : getCrop().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RegionCrop other = (RegionCrop) obj;
        if (this.getCrop() == null) {
            if (other.getCrop() != null)
                return false;
        } else if (!this.getCrop().equals(other.getCrop()))
            return false;
        return true;
    }
}
