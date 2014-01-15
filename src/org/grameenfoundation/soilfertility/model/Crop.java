package org.grameenfoundation.soilfertility.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Copyright (c) 2014 AppLab, Grameen Foundation
 * Created by: David
 */
@DatabaseTable(tableName = "crop")
public class Crop implements Serializable {

    @DatabaseField(id = true)
    @Expose
    @SerializedName("Name")
    private String name;

    public Crop() {

    }

    public Crop(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }

}
