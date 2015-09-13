package com.japhdroid.where;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PopulateListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void PopulateListView() {
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = getHelper().getCatalogTableDao();
        ArrayAdapter<ArrayList> itemsAdapter;
        List<String> noCatalog = new ArrayList<String>();
        noCatalog.add(getString(R.string.message_no_catalog));
        if (catalogDao.countOf() > 0) {
            List<CatalogTable> catalogs = catalogDao.queryForAll();
            itemsAdapter = new ArrayAdapter<ArrayList>(this, android.R.layout.simple_list_item_1, (ArrayList) catalogs);
        }
        else {
            itemsAdapter = new ArrayAdapter<ArrayList>(this, android.R.layout.simple_list_item_1, (ArrayList) noCatalog);
        }
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(itemsAdapter);
    }
}
