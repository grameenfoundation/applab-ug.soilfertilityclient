package org.grameenfoundation.soilfertility.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright (c) 2016 AppLab, Grameen Foundation
 * Created by: Josh
 */
@DatabaseTable(tableName = "version")
public class Version implements Serializable {

    @DatabaseField(id = true,columnName = "dateModified")
    @Expose
    //@DatabaseField(columnName = "dateModified")
    private Date DateTime;

    public Version() {

    }

    public Version(Date DateTime) {
        setVersion(DateTime);
    }

    public Date getVersion() {
        return DateTime;
    }

    public void setVersion(Date DateTime) {
        this.DateTime = DateTime;
    }

    @Override
    public String toString() {
        return getVersion().toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
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
        Version other = (Version) obj;
        if (this.getVersion() == null) {
            if (other.getVersion() != null)
                return false;
        } else if (!this.getVersion().equals(other.getVersion()))
            return false;
        return true;
    }
}
