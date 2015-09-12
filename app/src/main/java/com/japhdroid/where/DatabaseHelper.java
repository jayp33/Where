package com.japhdroid.where;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by User on 12.09.2015.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "where.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    // the DAO objects we use to access the tables
    private Dao<CatalogTable, Integer> catalogDao = null;
    private RuntimeExceptionDao<CatalogTable, Integer> catalogRuntimeDao = null;
    private Dao<ItemTable, Integer> itemDao = null;
    private RuntimeExceptionDao<ItemTable, Integer> itemRuntimeDao = null;
    private Dao<LocationTable, Integer> locationDao = null;
    private RuntimeExceptionDao<LocationTable, Integer> locationRuntimeDao = null;
    private Dao<RoomTable, Integer> roomDao = null;
    private RuntimeExceptionDao<RoomTable, Integer> roomRuntimeDao = null;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String databaseName){
        super(context, databaseName, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, CatalogTable.class);
            TableUtils.createTable(connectionSource, ItemTable.class);
            TableUtils.createTable(connectionSource, LocationTable.class);
            TableUtils.createTable(connectionSource, RoomTable.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            throw new SQLException("Upgrade not implemented");
/*
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, CatalogTable.class, true);
            TableUtils.dropTable(connectionSource, ItemTable.class, true);
            TableUtils.dropTable(connectionSource, LocationTable.class, true);
            TableUtils.dropTable(connectionSource, RoomTable.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);
*/
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our tables classes. It will create it or just give the cached
     * value.
     */
    public Dao<CatalogTable, Integer> getCatalogDao() throws SQLException {
        if (catalogDao == null) {
            catalogDao = getDao(CatalogTable.class);
        }
        return catalogDao;
    }

    public Dao<ItemTable, Integer> getItemDao() throws SQLException {
        if (itemDao == null) {
            itemDao = getDao(ItemTable.class);
        }
        return itemDao;
    }

    public Dao<LocationTable, Integer> getLocationDao() throws SQLException {
        if (locationDao == null) {
            locationDao = getDao(LocationTable.class);
        }
        return locationDao;
    }

    public Dao<RoomTable, Integer> getRoomDao() throws SQLException {
        if (roomDao == null) {
            roomDao = getDao(RoomTable.class);
        }
        return roomDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our table classes. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<CatalogTable, Integer> getCatalogTableDao() {
        if (catalogRuntimeDao == null) {
            catalogRuntimeDao = getRuntimeExceptionDao(CatalogTable.class);
        }
        return catalogRuntimeDao;
    }

    public RuntimeExceptionDao<ItemTable, Integer> getItemTableDao() {
        if (itemRuntimeDao == null) {
            itemRuntimeDao = getRuntimeExceptionDao(ItemTable.class);
        }
        return itemRuntimeDao;
    }

    public RuntimeExceptionDao<LocationTable, Integer> getLocationTableDao() {
        if (locationRuntimeDao == null) {
            locationRuntimeDao = getRuntimeExceptionDao(LocationTable.class);
        }
        return locationRuntimeDao;
    }

    public RuntimeExceptionDao<RoomTable, Integer> getRoomTableDao() {
        if (roomRuntimeDao == null) {
            roomRuntimeDao = getRuntimeExceptionDao(RoomTable.class);
        }
        return roomRuntimeDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        catalogDao = null;
        catalogRuntimeDao = null;
        itemDao = null;
        itemRuntimeDao = null;
        locationDao = null;
        locationRuntimeDao = null;
        roomDao = null;
        roomRuntimeDao = null;
    }
}
