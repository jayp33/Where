package com.japhdroid.where;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class ItemEditActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private String catalogName = null;
    CatalogTable catalog = null;
    private int itemId = 0;
    ItemTable item = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);
        PopulateFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void PopulateFields() {
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = getHelper().getCatalogTableDao();
        catalogName = getIntent().getStringExtra("CATALOG");
        catalog = DataProvider.getCatalog(catalogDao, catalogName);
        itemId = getIntent().getIntExtra("ITEM_ID", -1);
        if (itemId < 0) {
            Toast.makeText(ItemEditActivity.this, "Error: Item not found", Toast.LENGTH_SHORT).show();
            return;
        }
        RuntimeExceptionDao<ItemTable, Integer> itemDao = getHelper().getItemTableDao();
        item = DataProvider.getItem(itemDao, itemId);
        EditText editItem = (EditText) findViewById(R.id.editTextItem);
        editItem.setText(item.getDescription());
        EditText editLocation = (EditText) findViewById(R.id.editTextLocation);
        editLocation.setText(item.getLocation().getDescription());
        EditText editRoom = (EditText) findViewById(R.id.editTextRoom);
        editRoom.setText(item.getRoom().getDescription());
    }

    public void SaveItem(View view) {
        EditText editItem = (EditText) findViewById(R.id.editTextItem);
        String description = editItem.getText().toString();
        EditText editLocation = (EditText) findViewById(R.id.editTextLocation);
        String location = editLocation.getText().toString();
        EditText editRoom = (EditText) findViewById(R.id.editTextRoom);
        String room = editRoom.getText().toString();
        boolean create = item == null;
        if (description.equals("") || room.equals("")) {
            Toast.makeText(ItemEditActivity.this, "Beschreibung und Lagerraum müssen angegeben werden", Toast.LENGTH_SHORT).show();
            return;
        }
        if (create)
            item = new ItemTable(description);
        else
            item.setDescription(description);
        item.setCatalog(catalog);
        RuntimeExceptionDao<LocationTable, Integer> locationDao = getHelper().getLocationTableDao();
        LocationTable _location = DataProvider.getLocation(locationDao, location, true);
        if (_location != null)
            item.setLocation(_location);
        RuntimeExceptionDao<RoomTable, Integer> roomDao = getHelper().getRoomTableDao();
        RoomTable _room = DataProvider.getRoom(roomDao, room, true);
        if (_room != null)
            item.setRoom(_room);
        RuntimeExceptionDao<ItemTable, Integer> itemDao = getHelper().getItemTableDao();
        if (create)
            DataProvider.createItem(itemDao, item);
        else
            DataProvider.updateItem(itemDao, item);
        Toast.makeText(ItemEditActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        //finish();
    }
}
