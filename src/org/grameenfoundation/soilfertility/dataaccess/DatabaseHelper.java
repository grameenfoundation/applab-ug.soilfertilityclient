package org.grameenfoundation.soilfertility.dataaccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.grameenfoundation.soilfertility.model.*;

import java.sql.SQLException;

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
    private static final int DATABASE_VERSION = 1;

    // the DAO objects we use to access the database tables
    private Dao<Crop, Integer> cropDao = null;
    private Dao<Fertilizer, Integer> fertilizerDao = null;
    private Dao<CalcCrop, Integer> calcCropDao = null;
    private Dao<CalcFertilizer, Integer> calcFertilizerDao = null;
    private Dao<CalcCropFertilizerRatio, Integer> calcCropFertilizerRatioDao = null;
    private Dao<Calc, Integer> calculationDao = null;

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
            //insert default crops
            insertDefaultCrops();
            //insert default fertilizers
            insertDefaultFertilizers();
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
    }

    private void insertDefaultCrops() throws SQLException {
        Dao<Crop, Integer> cropDao = getCropDataDao();
        Crop crop1 = new Crop("Maize");
        cropDao.create(crop1);
        Crop crop2 = new Crop("Sorghum");
        cropDao.create(crop2);
        Crop crop3 = new Crop("Upland rice, paddy");
        cropDao.create(crop3);
        Crop crop4 = new Crop("Beans");
        cropDao.create(crop4);
        Crop crop5 = new Crop("Soybeans");
        cropDao.create(crop5);
        Crop crop6 = new Crop("Groundnuts, unshelled");
        cropDao.create(crop6);
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
            // after we drop the old databases, we create the new ones
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            log.error("Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Crop, Integer> getCropDataDao() throws SQLException {
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
    }
}
