package com.japhdroid.where;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
        if (id == R.id.action_create_catalog) {
            CreateCatalogDialog();
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (item.equals(getString(R.string.message_no_catalog)))
                    CreateCatalogDialog();
                else {
                    Intent i = new Intent(MainActivity.this, ItemListActivity.class);
                    i.putExtra("CATALOG", item);
                    startActivity(i);
                }
            }
        });
    }

    private void CreateCatalogDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.message_create_catalog_title));
        final EditText input = new EditText(this);
        input.setHint(getString(R.string.message_create_catalog_hint));
        builder.setView(input);
        builder.setPositiveButton(getString(R.string.buttonText_create_catalog), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String catalogName = input.getText().toString();
                if (catalogName.length() > 0)
                    CreateCatalog(catalogName);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void CreateCatalog(String catalogName) {
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = getHelper().getCatalogTableDao();
        CatalogTable catalog = new CatalogTable(catalogName);
        catalogDao.create(catalog);
        PopulateListView();
        Toast.makeText(MainActivity.this,
                String.format(getString(R.string.message_create_catalog_complete),catalogName),
                Toast.LENGTH_SHORT).show();
    }
}
