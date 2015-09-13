package com.japhdroid.where;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        String catalog = getIntent().getStringExtra("CATALOG");
        RuntimeExceptionDao<ItemTable, Integer> itemDao = getHelper().getItemTableDao();
        ArrayAdapter<ArrayList> itemsAdapter;
        List<String> noItems = new ArrayList<>();
        List<ItemTable> items = itemDao.queryForAll();
        for (ItemTable item : items) {
            if (!item.getCatalog().getDescription().equals(catalog))
                items.remove(item);
        }
        if (items.size() > 0)
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
                    EditItem(true);
                else
                    EditItem(false);
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
            EditItem(true);
        }

        return super.onOptionsItemSelected(item);
    }

    private void EditItem(boolean create) {
        Toast.makeText(ItemListActivity.this, "Click", Toast.LENGTH_SHORT).show();
    }
}
