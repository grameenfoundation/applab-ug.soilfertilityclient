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

    @DatabaseField(foreign = true,foreignAutoRefresh =true)
    @Expose
    @SerializedName("Region")
    private Region region;

    @DatabaseField(foreign = true,foreignAutoRefresh =true)
    @Expose
    @SerializedName("Crop")
    private Crop crop;

    public RegionCrop() {

    }

    public RegionCrop(Region Region, Crop Crop) {
       setRegion(Region);
        setCrop(Crop);
    }


     public Region getRegion() {
        return region;
     }

    public void setRegion(Region region) {
         this.region = region;
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
