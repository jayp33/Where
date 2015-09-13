package com.japhdroid.where;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private String catalog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        PopulateListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PopulateListView();
    }

    private void PopulateListView() {
        catalog = getIntent().getStringExtra("CATALOG");
        ArrayAdapter<ArrayList> itemsAdapter;
        List<String> noItems = new ArrayList<>();
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = getHelper().getCatalogTableDao();
        RuntimeExceptionDao<ItemTable, Integer> itemDao = getHelper().getItemTableDao();
        List<ItemTable> items = DataProvider.getItems(itemDao, DataProvider.getCatalog(catalogDao, catalog));
        if (items != null)
            itemsAdapter = new ArrayAdapter<ArrayList>(this, android.R.layout.simple_list_item_1, (ArrayList) items);
        else {
            noItems.add(getString(R.string.message_no_item));
            itemsAdapter = new ArrayAdapter<ArrayList>(this, android.R.layout.simple_list_item_1, (ArrayList) noItems);
        }
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (item.equals(getString(R.string.message_no_item)))
                    CreateItem();
                else
                    EditItem(item);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_list, menu);
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
        if (id == R.id.action_create_item) {
            CreateItem();
        }

        return super.onOptionsItemSelected(item);
    }

    private void CreateItem() {
        EditItem(null);
    }

    private void EditItem(String itemName) {
        int itemId = -1;
        if (itemName != null) { // edit
            RuntimeExceptionDao<ItemTable, Integer> itemDao = getHelper().getItemTableDao();
            ItemTable item = DataProvider.getItem(itemDao, itemName);
            if (item == null) {
                Toast.makeText(ItemListActivity.this, "Error: Items ambiguous", Toast.LENGTH_SHORT).show();
                return;
            }
            itemId = item.getId();
        }
        Intent i = new Intent(this, ItemEditActivity.class);
        i.putExtra("CATALOG", catalog);
        i.putExtra("ITEM_ID", itemId);
        startActivity(i);
    }
}
