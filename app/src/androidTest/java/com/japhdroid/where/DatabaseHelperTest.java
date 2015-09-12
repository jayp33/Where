package com.japhdroid.where;

import android.test.AndroidTestCase;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

/**
 * Created by User on 12.09.2015.
 */
public class DatabaseHelperTest extends AndroidTestCase {

    public void ClearAllTables() {
        DatabaseHelper helper = new DatabaseHelper(getContext(), "whereTest.db");

        RuntimeExceptionDao<ItemTable, Integer> itemDao = helper.getItemTableDao();
        if (itemDao.countOf() > 0) {
            List<ItemTable> items = itemDao.queryForAll();
            for (ItemTable item : items) {
                itemDao.delete(item);
            }
        }

        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = helper.getCatalogTableDao();
        if (catalogDao.countOf() > 0) {
            List<CatalogTable> catalogs = catalogDao.queryForAll();
            for (CatalogTable catalog : catalogs) {
                catalogDao.delete(catalog);
            }
        }

        RuntimeExceptionDao<LocationTable, Integer> locationDao = helper.getLocationTableDao();
        if (locationDao.countOf() > 0) {
            List<LocationTable> locations = locationDao.queryForAll();
            for (LocationTable location : locations) {
                locationDao.delete(location);
            }
        }

        RuntimeExceptionDao<RoomTable, Integer> roomDao = helper.getRoomTableDao();
        if (roomDao.countOf() > 0) {
            List<RoomTable> rooms = roomDao.queryForAll();
            for (RoomTable room : rooms) {
                roomDao.delete(room);
            }
        }
    }

    public void testItemCreation() throws Exception {
        ClearAllTables();
        DatabaseHelper helper = new DatabaseHelper(getContext(), "whereTest.db");
        RuntimeExceptionDao<ItemTable, Integer> itemDao = helper.getItemTableDao();
        ItemTable item = new ItemTable("Item 1");
        CatalogTable catalog = new CatalogTable("Catalog 1");
        item.setCatalog(catalog);
        LocationTable location = new LocationTable("Location 1");
        item.setLocation(location);
        RoomTable room = new RoomTable("Room 1");
        item.setRoom(room);
        itemDao.create(item);
        itemDao = null;
        item = null;
        catalog = null;
        location = null;
        room = null;
        itemDao = helper.getItemTableDao();
        List<ItemTable> items = itemDao.queryForEq("description", "Item 1");
        assertEquals(1, items.size());
        item = items.get(0);
        assertEquals("Item 1", item.getDescription());
        assertEquals("Catalog 1", item.getCatalog().getDescription());
        assertEquals("Location 1", item.getLocation().getDescription());
        assertEquals("Room 1", item.getRoom().getDescription());

        // Teardown
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = helper.getCatalogTableDao();
        List<CatalogTable> catalogs = catalogDao.queryForEq("description", "Catalog 1");
        assertEquals(1, catalogs.size());
        catalogDao.delete(catalogs.get(0));
        RuntimeExceptionDao<LocationTable, Integer> locationDao = helper.getLocationTableDao();
        List<LocationTable> locations = locationDao.queryForEq("description", "Location 1");
        assertEquals(1, locations.size());
        locationDao.delete(locations.get(0));
        RuntimeExceptionDao<RoomTable, Integer> roomDao = helper.getRoomTableDao();
        List<RoomTable> rooms = roomDao.queryForEq("description", "Room 1");
        assertEquals(1, rooms.size());
        roomDao.delete(rooms.get(0));
        itemDao.delete(item);
    }

    public void testItemDeletionKeepsOtherObjects() throws Exception {
        ClearAllTables();
        DatabaseHelper helper = new DatabaseHelper(getContext(), "whereTest.db");
        RuntimeExceptionDao<ItemTable, Integer> itemDao = helper.getItemTableDao();
        ItemTable item = new ItemTable("Item 1");
        CatalogTable catalog = new CatalogTable("Catalog 1");
        item.setCatalog(catalog);
        LocationTable location = new LocationTable("Location 1");
        item.setLocation(location);
        RoomTable room = new RoomTable("Room 1");
        item.setRoom(room);
        itemDao.create(item);
        itemDao = null;
        item = null;
        catalog = null;
        location = null;
        room = null;
        itemDao = helper.getItemTableDao();
        List<ItemTable> items = itemDao.queryForEq("description", "Item 1");
        assertEquals(1, items.size());
        item = items.get(0);
        assertEquals("Item 1", item.getDescription());
        assertEquals("Catalog 1", item.getCatalog().getDescription());
        assertEquals("Location 1", item.getLocation().getDescription());
        assertEquals("Room 1", item.getRoom().getDescription());

        itemDao.delete(item);

        // Teardown
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = helper.getCatalogTableDao();
        List<CatalogTable> catalogs = catalogDao.queryForEq("description", "Catalog 1");
        assertEquals(1, catalogs.size());
        RuntimeExceptionDao<LocationTable, Integer> locationDao = helper.getLocationTableDao();
        List<LocationTable> locations = locationDao.queryForEq("description", "Location 1");
        assertEquals(1, locations.size());
        RuntimeExceptionDao<RoomTable, Integer> roomDao = helper.getRoomTableDao();
        List<RoomTable> rooms = roomDao.queryForEq("description", "Room 1");
        assertEquals(1, rooms.size());
        catalogDao.delete(catalogs.get(0));
        locationDao.delete(locations.get(0));
        roomDao.delete(rooms.get(0));
    }
}