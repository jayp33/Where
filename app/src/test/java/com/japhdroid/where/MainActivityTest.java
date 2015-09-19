package com.japhdroid.where;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.util.ActivityController;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by User on 14.09.2015.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

    DatabaseHelper dbhelper;
    ActivityController<MainActivity> mControllerMain;
    ActivityController<ItemListActivity> mControllerItemList;

    @Before
    public void setUp() throws Exception {
        dbhelper = new DatabaseHelper(ShadowApplication.getInstance().getApplicationContext());
    }

    @Test
    public void testEmptyCatalogListViewContainsPlaceholder() throws Exception {
        mControllerMain = Robolectric.buildActivity(MainActivity.class);
        MainActivity activity = mControllerMain.create().start().resume().visible().get();
        ListView list = (ListView) activity.findViewById(R.id.list);
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = dbhelper.getCatalogTableDao();
        if (!DataProvider.ExistCatalogs(catalogDao)) {
            assertEquals(1, list.getCount());
            assertEquals(activity.getString(R.string.message_no_catalog), list.getItemAtPosition(0).toString());
        }
        mControllerMain = mControllerMain.pause().stop().destroy();
    }

    @Test
    public void testCatalogCreation() throws Exception {
        mControllerMain = Robolectric.buildActivity(MainActivity.class);
        MainActivity activity = mControllerMain.create().start().resume().visible().get();
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = dbhelper.getCatalogTableDao();
        assertFalse(DataProvider.ExistCatalogs(catalogDao));
        // Click on placeholder in MainActivity
        ListView catalogList = (ListView) activity.findViewById(R.id.list);
        ListAdapter adapter = catalogList.getAdapter();
        View catalogItemView = adapter.getView(0, null, catalogList);
        catalogList.performItemClick(catalogItemView, 0, adapter.getItemId(0));
        // Populate and confirm dialog box
        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog sAlert = shadowOf(alert);
        assertTrue(alert.isShowing());
        EditText catalogName = (EditText) sAlert.getView();
        assertEquals(activity.getString(R.string.message_create_catalog_hint), catalogName.getHint());
        catalogName.setText("#TEST");
        sAlert.clickOn(alert.getButton(AlertDialog.BUTTON_POSITIVE).getId());
        assertFalse(alert.isShowing());
        // Assert catalog creation
        List<CatalogTable> catalogs = DataProvider.getCatalogs(catalogDao);
        assertEquals(1, catalogs.size());
        assertEquals("#TEST", catalogs.get(0).getDescription());
        assertEquals(1, catalogList.getCount());
        assertEquals("#TEST", catalogList.getItemAtPosition(0).toString());
        catalogList.performItemClick(catalogItemView, 0, adapter.getItemId(0));
        Intent expectedIntent = new Intent(activity, ItemListActivity.class);
        expectedIntent.putExtra("CATALOG", "#TEST");
        assertEquals(expectedIntent, shadowOf(activity).getNextStartedActivity());
        mControllerMain = mControllerMain.pause().stop().destroy();
    }

    @Test
    public void testEmptyItemListViewContainsPlaceholder() throws Exception {
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = dbhelper.getCatalogTableDao();
        catalogDao.create(new CatalogTable("#TEST"));
        Intent intent = new Intent(ShadowApplication.getInstance().getApplicationContext(), ItemListActivity.class);
        intent.putExtra("CATALOG", "#TEST");
        mControllerItemList = Robolectric.buildActivity(ItemListActivity.class).withIntent(intent);
        ItemListActivity activity2 = mControllerItemList.create().start().resume().visible().get();
        ListView itemList = (ListView) activity2.findViewById(R.id.list);
        assertEquals(1, itemList.getCount());
        assertEquals(activity2.getString(R.string.message_no_item), itemList.getItemAtPosition(0).toString());
        mControllerItemList = mControllerItemList.pause().stop().destroy();
    }

    @After
    public void tearDown() throws Exception {
        dbhelper.close();
    }
}