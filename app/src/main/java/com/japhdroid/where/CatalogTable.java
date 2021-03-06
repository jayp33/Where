package com.japhdroid.where;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by User on 12.09.2015.
 */
@DatabaseTable(tableName = "Catalog")
public class CatalogTable {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, unique = true)
    private String description;

    CatalogTable() {
    }

    CatalogTable(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (!o.getClass().equals(this.getClass()))
            return false;
        return ((CatalogTable) o).description.equals(this.description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
