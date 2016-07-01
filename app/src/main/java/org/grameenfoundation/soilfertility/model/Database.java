package org.grameenfoundation.soilfertility.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Copyright (c) 2016 AppLab, Grameen Foundation
 * Created by: Joshua Tendo
 *
 * custom database class to pass data between the server and mobile client
 */
public class Database implements Serializable {
    public Database() {

    }

    @Expose
    @SerializedName("VersionDateTime")
    public Date versionDateTime;

    @Expose
    @SerializedName("Regions")
    private Collection<Region> regions = new ArrayList<Region>();

    @Expose
    @SerializedName("Crops")
    private Collection<Crop>  crops = new ArrayList<Crop>();

    @Expose
    @SerializedName("RegionCrops")
    private Collection<RegionCrop> regionCrops = new ArrayList<RegionCrop>();

    public Date getVersionDateTime() {
        return versionDateTime;
    }

    public void setVersionDateTime(Version version) {
        this.versionDateTime = version.getVersion();
    }

    public void setRegions(List<Region> regions) {
        for (Region region : regions) {
              this.regions.add(region);
        }
    }

    public Collection<Region> getRegions() {
        return regions;
    }

    public void setCrops(List<Crop> Crops) {
        for (Crop crop : Crops) {
            this.crops.add(crop);
        }
    }

    public Collection<Crop> getCrops() {
        return crops;
    }

    public void setRegionCrops(List<RegionCrop> RegionCrops) {
        for (RegionCrop regionCrop : RegionCrops ) {
            this.regionCrops.add(regionCrop);
        }
    }

    public Collection<RegionCrop> getRegionCrops() {
        return regionCrops;
    }


}

