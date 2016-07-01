package org.grameenfoundation.soilfertility.dataaccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.grameenfoundation.soilfertility.model.*;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Copyright (c) 2014 AppLab, Grameen Foundation
 * Created by: David
 *
 * Database helper class used to manage the creation and upgrading of the database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String DATABASE_NAME = "soilFertility.db";
    // any time changes are made to database objects, the database version has to be increased
    private static final int DATABASE_VERSION = 18;

    // the DAO objects we use to access the database tables
    private Dao<Crop, String> cropDao = null;
    private Dao<Fertilizer, Integer> fertilizerDao = null;
    private Dao<CalcCrop, Integer> calcCropDao = null;
    private Dao<CalcFertilizer, Integer> calcFertilizerDao = null;
    private Dao<CalcCropFertilizerRatio, Integer> calcCropFertilizerRatioDao = null;
    private Dao<Calc, Integer> calculationDao = null;
    private Dao<Region, Integer> regionDao = null;
    private Dao<RegionCrop, Integer> regionCropDao = null;
    private Dao<Version, String> versionDao = null;

//    private DatabaseHelper databaseHelper = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created
     * @param sqLiteDatabase
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            //Log.i(DatabaseHelper.class.getName(), "onCreate");
            log.info("onCreate");
            TableUtils.createTable(connectionSource, Crop.class);
            TableUtils.createTable(connectionSource, Fertilizer.class);
            TableUtils.createTable(connectionSource, CalcCrop.class);
            TableUtils.createTable(connectionSource, CalcFertilizer.class);
            TableUtils.createTable(connectionSource, CalcCropFertilizerRatio.class);
            TableUtils.createTable(connectionSource, Calc.class);
            TableUtils.createTable(connectionSource, Region.class);
            TableUtils.createTable(connectionSource, RegionCrop.class);
            TableUtils.createTable(connectionSource, Version.class);

            //insert installation date into the version table
            Dao<Version, String> versionDao = getVersionDataDao();

            Date currentDate = new Date(90,1,1);
            Version version = new Version(currentDate);
            versionDao.create(version);

            //insert default crops
            insertDefaultCrops();

            //insert default fertilizers
            insertDefaultFertilizers();

            //insert default regions
            insertDefaultRegions();

            //insert default region_crops
            insertDefaultRegionCrops();


        } catch (SQLException e) {
            log.error("Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    private void insertDefaultFertilizers() throws SQLException {
        Dao<Fertilizer, Integer> fertilizerDao = getFertilizerDataDao();
        Fertilizer fertilizer1 = new Fertilizer("Urea");
        fertilizerDao.create(fertilizer1);
        Fertilizer fertilizer2 = new Fertilizer("Triple super phosphate, TSP");
        fertilizerDao.create(fertilizer2);
        Fertilizer fertilizer3 = new Fertilizer("Diammonium phosphate, DAP");
        fertilizerDao.create(fertilizer3);
        Fertilizer fertilizer4 = new Fertilizer("Murate of potash, KCL");
        fertilizerDao.create(fertilizer4);
        Fertilizer fertilizer5 = new Fertilizer("NPK ");
        fertilizerDao.create(fertilizer5);
    }

    private void insertDefaultCrops() throws SQLException {
        Dao<Crop, String> cropDao = getCropDataDao();
        Crop crop1 = new Crop("Maize");
        cropDao.create(crop1);
        Crop crop2 = new Crop("Sorghum");
        cropDao.create(crop2);
        Crop crop3 = new Crop("Upland Rice");
        cropDao.create(crop3);
        Crop crop4 = new Crop("Beans");
        cropDao.create(crop4);
        Crop crop5 = new Crop("Soybeans");
        cropDao.create(crop5);
        Crop crop6 = new Crop("Groundnuts-unshelled");
        cropDao.create(crop6);
        Crop crop7 = new Crop("Banana");
        cropDao.create(crop7);
        Crop crop8 = new Crop("Fingermillet");
        cropDao.create(crop8);
        Crop crop9 = new Crop("Irish Potato");
        cropDao.create(crop9);
        Crop crop10 = new Crop("Wheat");
        cropDao.create(crop10);
    }

    private void insertDefaultRegions() throws SQLException {
        Dao<Region, Integer> regionDao = getRegionDataDao();
        Region region1 = new Region(0,"Central Uganda","Acres");
        regionDao.create(region1);
        Region region2 = new Region(1,"Eastern 1400-1800m","Acres");
        regionDao.create(region2);
        Region region3 = new Region(2,"Eastern > 1800m","Acres");
        regionDao.create(region3);
        Region region4 = new Region(3,"Eastern Uganda - Lake Kyoga Basin","Hectares");
        regionDao.create(region4);
        Region region5 = new Region(4,"Western Highland > 1800","Hectares");
        regionDao.create(region5);
        Region region6 = new Region(5,"Western Highlands Kamwenge Ibanda Bushenyi Kyenjojo 1400-1800","Acres");
        regionDao.create(region6);
        Region region7 = new Region(6,"Northern Midwest & West","Acres");
        regionDao.create(region7);
    }

    private void insertDefaultRegionCrops() throws SQLException {
        Dao<RegionCrop, Integer> regionCropDao = getRegionCropDataDao();
        RegionCrop regionCrop1 = new RegionCrop(0,cropDao.queryForId("Maize")	);                                   regionCropDao.create(regionCrop1);
        RegionCrop regionCrop2 = new RegionCrop(0,cropDao.queryForId("Banana"));                                 regionCropDao.create(regionCrop2);
        RegionCrop regionCrop3 = new RegionCrop(0,cropDao.queryForId("Upland Rice"));                           regionCropDao.create(regionCrop3);
        RegionCrop regionCrop4 = new RegionCrop(0,cropDao.queryForId("Beans"));                                  regionCropDao.create(regionCrop4);
        RegionCrop regionCrop5 = new RegionCrop(0,cropDao.queryForId("Soybeans"));                             regionCropDao.create(regionCrop5);
        RegionCrop regionCrop6 = new RegionCrop(0,cropDao.queryForId("Groundnuts-unshelled"));              regionCropDao.create(regionCrop6);
        RegionCrop regionCrop7 = new RegionCrop(1,cropDao.queryForId("Maize"));                                   regionCropDao.create(regionCrop7);
        RegionCrop regionCrop8 = new RegionCrop(1,cropDao.queryForId("Banana"));                                 regionCropDao.create(regionCrop8);
        RegionCrop regionCrop9 = new RegionCrop(1,cropDao.queryForId("Irish Potato"));                           regionCropDao.create(regionCrop9);
        RegionCrop regionCrop10 = new RegionCrop(1,cropDao.queryForId("Beans"));                                  regionCropDao.create(regionCrop10);
        RegionCrop regionCrop11 = new RegionCrop(1,cropDao.queryForId("Soybeans"));                             regionCropDao.create(regionCrop11);
        RegionCrop regionCrop12 = new RegionCrop(1,cropDao.queryForId("Groundnuts-unshelled"));              regionCropDao.create(regionCrop12);
        RegionCrop regionCrop13 = new RegionCrop(2,cropDao.queryForId("Maize"));                                   regionCropDao.create(regionCrop13);
        RegionCrop regionCrop14 = new RegionCrop(2,cropDao.queryForId("Banana"));                                 regionCropDao.create(regionCrop14);
        RegionCrop regionCrop15 = new RegionCrop(2,cropDao.queryForId("Wheat"));                                  regionCropDao.create(regionCrop15);
        RegionCrop regionCrop16 = new RegionCrop(2,cropDao.queryForId("Beans"));                                  regionCropDao.create(regionCrop16);
        RegionCrop regionCrop17 = new RegionCrop(2,cropDao.queryForId("Soybeans"));                             regionCropDao.create(regionCrop17);
        RegionCrop regionCrop18 = new RegionCrop(2,cropDao.queryForId("Groundnuts-unshelled"));              regionCropDao.create(regionCrop18);
        RegionCrop regionCrop19 = new RegionCrop(3,cropDao.queryForId("Upland Rice"));                           regionCropDao.create(regionCrop19);
        RegionCrop regionCrop20 = new RegionCrop(3,cropDao.queryForId("Maize"));                                   regionCropDao.create(regionCrop20);
        RegionCrop regionCrop21 = new RegionCrop(3,cropDao.queryForId("Sorghum"));                               regionCropDao.create(regionCrop21);
        RegionCrop regionCrop22 = new RegionCrop(3,cropDao.queryForId("Soybeans"));                             regionCropDao.create(regionCrop22);
        RegionCrop regionCrop23 = new RegionCrop(3,cropDao.queryForId("Fingermillet"));                           regionCropDao.create(regionCrop23);
        RegionCrop regionCrop24 = new RegionCrop(3,cropDao.queryForId("Beans"));                                  regionCropDao.create(regionCrop24);
        RegionCrop regionCrop25 = new RegionCrop(3,cropDao.queryForId("Groundnuts-unshelled"));              regionCropDao.create(regionCrop25);
        RegionCrop regionCrop26 = new RegionCrop(4,cropDao.queryForId("Maize"));                                   regionCropDao.create(regionCrop26);
        RegionCrop regionCrop27= new RegionCrop(4,cropDao.queryForId("Irish Potato"));                           regionCropDao.create(regionCrop27);
        RegionCrop regionCrop28 = new RegionCrop(4,cropDao.queryForId("Wheat"));                                  regionCropDao.create(regionCrop28);
        RegionCrop regionCrop29 = new RegionCrop(4,cropDao.queryForId("Beans"));                                  regionCropDao.create(regionCrop29);
        RegionCrop regionCrop30 = new RegionCrop(5,cropDao.queryForId("Maize"));                                   regionCropDao.create(regionCrop30);
        RegionCrop regionCrop31 = new RegionCrop(5,cropDao.queryForId("Banana"));                                 regionCropDao.create(regionCrop31);
        RegionCrop regionCrop32 = new RegionCrop(5,cropDao.queryForId("Irish Potato"));                           regionCropDao.create(regionCrop32);
        RegionCrop regionCrop33 = new RegionCrop(5,cropDao.queryForId("Beans"));                                  regionCropDao.create(regionCrop33);
        RegionCrop regionCrop34 = new RegionCrop(5,cropDao.queryForId("Fingermillet"));                           regionCropDao.create(regionCrop34);
        RegionCrop regionCrop35 = new RegionCrop(5,cropDao.queryForId("Soybeans"));                             regionCropDao.create(regionCrop35);
        RegionCrop regionCrop36 = new RegionCrop(6,cropDao.queryForId("Upland Rice"));                           regionCropDao.create(regionCrop36);
        RegionCrop regionCrop37 = new RegionCrop(6,cropDao.queryForId("Maize"));                                   regionCropDao.create(regionCrop37);
        RegionCrop regionCrop38 = new RegionCrop(6,cropDao.queryForId("Sorghum"));                               regionCropDao.create(regionCrop38);
        RegionCrop regionCrop39 = new RegionCrop(6,cropDao.queryForId("Soybeans"));                             regionCropDao.create(regionCrop39);
        RegionCrop regionCrop40 = new RegionCrop(6,cropDao.queryForId("Fingermillet"));                           regionCropDao.create(regionCrop40);
        RegionCrop regionCrop41 = new RegionCrop(6,cropDao.queryForId("Beans"));                                  regionCropDao.create(regionCrop41);
        RegionCrop regionCrop42 = new RegionCrop(6,cropDao.queryForId("Groundnuts-unshelled"));              regionCropDao.create(regionCrop42);

    }

    /**
     *  This is called when your application is upgraded and it has a higher version number.
     * @param sqLiteDatabase
     * @param connectionSource
     * @param i
     * @param i2
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            log.info("onUpgrade");
            TableUtils.dropTable(connectionSource, Crop.class, true);
            TableUtils.dropTable(connectionSource, Fertilizer.class, true);
            TableUtils.dropTable(connectionSource, CalcCrop.class, true);
            TableUtils.dropTable(connectionSource, CalcFertilizer.class, true);
            TableUtils.dropTable(connectionSource, CalcCropFertilizerRatio.class, true);
            TableUtils.dropTable(connectionSource, Calc.class, true);
            TableUtils.dropTable(connectionSource, Region.class, true);
            TableUtils.dropTable(connectionSource, RegionCrop.class, true);
            TableUtils.dropTable(connectionSource, Version.class, true);

            // after we drop the old databases, we create the new ones
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            log.error("Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public void onVersionChange(Calc calc) throws SQLException {
       Database newdb = calc.getDatabase();
        if (!newdb.getCrops().isEmpty()){
            TableUtils.dropTable(connectionSource, Crop.class, true);
            TableUtils.createTable(connectionSource, Crop.class);
            for (Crop crop : newdb.getCrops()) {
                getCropDataDao().create(crop);
            }
        }
    }

    public Dao<Version, String> getVersionDataDao() throws SQLException {
        if (versionDao == null) {
            versionDao = getDao(Version.class);
        }
        return versionDao;
    }
    public Dao<Crop, String> getCropDataDao() throws SQLException {
        if (cropDao == null) {
            cropDao = getDao(Crop.class);
        }
        return cropDao;
    }
    public Dao<Fertilizer, Integer> getFertilizerDataDao() throws SQLException {
        if (fertilizerDao == null) {
            fertilizerDao = getDao(Fertilizer.class);
        }
        return fertilizerDao;
    }
    public Dao<Region, Integer> getRegionDataDao() throws SQLException {
        if (regionDao == null) {
            regionDao = getDao(Region.class);
        }
        return regionDao;
    }
    public Dao<RegionCrop, Integer> getRegionCropDataDao() throws SQLException {
        if (regionCropDao == null) {
            regionCropDao = getDao(RegionCrop.class);
        }
        return regionCropDao;
    }
    public Dao<CalcCrop, Integer> getCalcCropDataDao() throws SQLException {
        if (calcCropDao == null) {
            calcCropDao = getDao(CalcCrop.class);
        }
        return calcCropDao;
    }
    public Dao<CalcFertilizer, Integer> getCalcFertilizerDataDao() throws SQLException {
        if (calcFertilizerDao == null) {
            calcFertilizerDao = getDao(CalcFertilizer.class);
        }
        return calcFertilizerDao;
    }
    public Dao<CalcCropFertilizerRatio, Integer> getCalcCropFertilizerRatioDao() throws SQLException {
        if (calcCropFertilizerRatioDao == null) {
            calcCropFertilizerRatioDao = getDao(CalcCropFertilizerRatio.class);
        }
        return calcCropFertilizerRatioDao;
    }
    public Dao<Calc, Integer> getCalculationsDataDao() throws SQLException {
        if (calculationDao == null) {
            calculationDao = getDao(Calc.class);
        }
        return calculationDao;
    }


    /**
     * Closes the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        cropDao = null;
        fertilizerDao = null;
        calcFertilizerDao = null;
        calcCropDao = null;
        calcCropFertilizerRatioDao = null;
        calculationDao = null;
        regionDao = null;
        regionCropDao = null;
        versionDao = null;
    }
}
