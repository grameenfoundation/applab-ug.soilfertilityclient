package org.grameenfoundation.soilfertility.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
public class Database  {
    public Database() {

    }

    @Expose
    @SerializedName("VersionDateTime")
    public Date VersionDateTime;

    @Expose
    @SerializedName("Regions")
    private Collection<Region> Regions = new ArrayList<Region>();

    @Expose
    @SerializedName("Crops")
    private Collection<Crop>  Crops = new ArrayList<Crop>();

    @Expose
    @SerializedName("RegionCrops")
    private Collection<RegionCrop> RegionCrops = new ArrayList<RegionCrop>();

    public Date getVersionDateTime(Version version) {
        return version.getVersion();
    }

    public void setVersionDateTime(Version version) {
        this.VersionDateTime = version.getVersion();
    }

    public void setRegions(List<Region> Regions) {
        for (Region region : Regions) {
              this.Regions.add(region);
        }
    }

    public Collection<Region> getRegions() {
        return Regions;
    }

    public void setCrops(List<Crop> Crops) {
        for (Crop crop : Crops) {
            this.Crops.add(crop);
        }
    }

    public Collection<Crop> getCrops() {
        return Crops;
    }

    public void setRegionCrops(List<RegionCrop> RegionCrops) {
        for (RegionCrop regionCrop : RegionCrops ) {
            this.RegionCrops.add(regionCrop);
        }
    }

    public Collection<RegionCrop> getRegionCrops() {
        return RegionCrops;
    }


}

