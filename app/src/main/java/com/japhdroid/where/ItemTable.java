package com.japhdroid.where;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by User on 12.09.2015.
 */
@DatabaseTable(tableName = "Item")
public class ItemTable {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String description;

    @DatabaseField(canBeNull = false)
    private CatalogTable catalog;

    @DatabaseField
    private LocationTable location;

    @DatabaseField(canBeNull = false)
    private RoomTable room;

    ItemTable() {
    }

    ItemTable(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CatalogTable getCatalog() {
        return catalog;
    }

    public void setCatalog(CatalogTable catalog) {
        this.catalog = catalog;
    }

    public LocationTable getLocation() {
        return location;
    }

    public void setLocation(LocationTable location) {
        this.location = location;
    }

    public RoomTable getRoom() {
        return room;
    }

    public void setRoom(RoomTable room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "ItemTable{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
