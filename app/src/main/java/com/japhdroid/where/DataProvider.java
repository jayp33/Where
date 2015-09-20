package com.japhdroid.where;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 13.09.2015.
 */
public class DataProvider {

    public static boolean ExistCatalogs(RuntimeExceptionDao<CatalogTable, Integer> dao) {
        return dao.queryForAll().size() > 0;
    }

    public static CatalogTable getCatalog(RuntimeExceptionDao<CatalogTable, Integer> dao, String catalogName) {
        List<CatalogTable> catalogs = dao.queryForEq("description", catalogName);
        if (catalogs.size() == 1)
            return catalogs.get(0);
        return null;
    }

    public static List<CatalogTable> getCatalogs(RuntimeExceptionDao<CatalogTable, Integer> dao) {
        return dao.queryForAll();
    }

    public static int createItem(RuntimeExceptionDao<ItemTable, Integer> dao, ItemTable item) {
        return dao.create(item);
    }

    public static int updateItem(RuntimeExceptionDao<ItemTable, Integer> dao, ItemTable item) {
        return dao.update(item);
    }

    public static ItemTable getItem(RuntimeExceptionDao<ItemTable, Integer> dao, int itemId) {
        return dao.queryForId(itemId);
    }

    public static ItemTable getItem(RuntimeExceptionDao<ItemTable, Integer> dao, String itemName) {
        List<ItemTable> items = dao.queryForEq("description", itemName);
        if (items.size() > 1)
            return null;
        return items.get(0);
    }

    public static ItemTable getItem(RuntimeExceptionDao<ItemTable, Integer> dao, String itemName, CatalogTable catalog) {
        Map<String, Object> fieldValues = new HashMap<String, Object>(2);
        fieldValues.put("description", itemName);
        fieldValues.put("catalog_id", catalog);
        List<ItemTable> result = dao.queryForFieldValues(fieldValues);
        if (result.size() == 1)
            return result.get(0);
        return null;
    }

    public static List<ItemTable> getItems(RuntimeExceptionDao<ItemTable, Integer> dao, CatalogTable catalog) {
        return dao.queryForEq("catalog_id", catalog);
    }

    public static List<ItemTable> getItems(RuntimeExceptionDao<ItemTable, Integer> dao, String itemName) {
        return dao.queryForEq("description", itemName);
    }

    public static List<ItemTable> getItems(RuntimeExceptionDao<ItemTable, Integer> dao, String itemName, CatalogTable catalog) {
        if (catalog == null)
            return null;
        List<ItemTable> items = dao.queryForEq("description", itemName);
        for (ItemTable item : items)
            if (!item.getCatalog().equals(catalog))
                items.remove(item);
        return items;
    }

    public static LocationTable getLocation(RuntimeExceptionDao<LocationTable, Integer> dao, String locationName, boolean createIfNotExist) {
        List<LocationTable> locations = dao.queryForEq("description", locationName);
        if (locations.size() == 0 && !createIfNotExist)
            return null;
        if (locations.size() == 1)
            return locations.get(0);
        if (locations.size() == 0 && createIfNotExist) {
            LocationTable location = new LocationTable(locationName);
            dao.create(location);
            return location;
        }
        return null;
    }

    public static RoomTable getRoom(RuntimeExceptionDao<RoomTable, Integer> dao, String roomName, boolean createIfNotExist) {
        List<RoomTable> rooms = dao.queryForEq("description", roomName);
        if (rooms.size() == 0 && !createIfNotExist)
            return null;
        if (rooms.size() == 1)
            return rooms.get(0);
        if (rooms.size() == 0 && createIfNotExist) {
            RoomTable room = new RoomTable(roomName);
            dao.create(room);
            return room;
        }
        return null;
    }
}
