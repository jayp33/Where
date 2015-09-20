package com.japhdroid.where;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
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
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    ActivityController<ItemEditActivity> mControllerItemEdit;

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
        ItemListActivity activity = mControllerItemList.create().start().resume().visible().get();
        ListView itemList = (ListView) activity.findViewById(R.id.list);
        assertEquals(1, itemList.getCount());
        assertEquals(activity.getString(R.string.message_no_item), itemList.getItemAtPosition(0).toString());
        mControllerItemList = mControllerItemList.pause().stop().destroy();
    }

    @Test
    public void testCatalogsContainCorrectItems() throws Exception {
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = dbhelper.getCatalogTableDao();
        catalogDao.create(new CatalogTable("#TEST1"));
        catalogDao.create(new CatalogTable("#TEST2"));
        RuntimeExceptionDao<ItemTable, Integer> itemDao = dbhelper.getItemTableDao();
        ItemTable item = new ItemTable("#TEST1_1");
        item.setCatalog(DataProvider.getCatalog(catalogDao, "#TEST1"));
        item.setLocation(new LocationTable(""));
        item.setRoom(new RoomTable("#ROOM1"));
        itemDao.create(item);
        item = new ItemTable("#TEST1_2");
        item.setCatalog(DataProvider.getCatalog(catalogDao, "#TEST1"));
        item.setLocation(new LocationTable("#LOCATION1"));
        item.setRoom(new RoomTable("#ROOM2"));
        itemDao.create(item);

        Intent intent = new Intent(ShadowApplication.getInstance().getApplicationContext(), ItemListActivity.class);
        intent.putExtra("CATALOG", "#TEST1");
        mControllerItemList = Robolectric.buildActivity(ItemListActivity.class).withIntent(intent);
        ItemListActivity activity = mControllerItemList.create().start().resume().visible().get();
        ListView itemList = (ListView) activity.findViewById(R.id.list);
        assertEquals(2, itemList.getCount());
        Map<String, String> datum = new HashMap<String, String>(2);
        datum.put("item", "#TEST1_1");
        datum.put("location", "#ROOM1");
        assertEquals(datum, itemList.getItemAtPosition(0));
        datum = new HashMap<String, String>(2);
        datum.put("item", "#TEST1_2");
        datum.put("location", "#LOCATION1\n#ROOM2");
        assertEquals(datum, itemList.getItemAtPosition(1));
        mControllerItemList = mControllerItemList.pause().stop().destroy();

        intent = new Intent(ShadowApplication.getInstance().getApplicationContext(), ItemListActivity.class);
        intent.putExtra("CATALOG", "#TEST2");
        mControllerItemList = Robolectric.buildActivity(ItemListActivity.class).withIntent(intent);
        activity = mControllerItemList.create().start().resume().visible().get();
        itemList = (ListView) activity.findViewById(R.id.list);
        assertEquals(1, itemList.getCount());
        assertEquals(activity.getString(R.string.message_no_item), itemList.getItemAtPosition(0).toString());
        mControllerItemList = mControllerItemList.pause().stop().destroy();
    }

    @Test
    public void testItemCreation() throws Exception {
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = dbhelper.getCatalogTableDao();
        catalogDao.create(new CatalogTable("#TEST"));

        Intent intent = new Intent(ShadowApplication.getInstance().getApplicationContext(), ItemListActivity.class);
        intent.putExtra("CATALOG", "#TEST");
        mControllerItemList = Robolectric.buildActivity(ItemListActivity.class).withIntent(intent);
        ItemListActivity activity = mControllerItemList.create().start().resume().visible().get();
        ListView itemList = (ListView) activity.findViewById(R.id.list);
        assertEquals(1, itemList.getCount());
        assertEquals(activity.getString(R.string.message_no_item), itemList.getItemAtPosition(0).toString());
        mControllerItemList = mControllerItemList.pause().stop();

        intent = new Intent(ShadowApplication.getInstance().getApplicationContext(), ItemEditActivity.class);
        intent.putExtra("CATALOG", "#TEST");
        intent.putExtra("ITEM_ID", -1);
        mControllerItemEdit = Robolectric.buildActivity(ItemEditActivity.class).withIntent(intent);
        ItemEditActivity activityEdit = mControllerItemEdit.create().start().resume().visible().get();
        EditText etItem = (EditText) activityEdit.findViewById(R.id.editTextItem);
        assertEquals("", etItem.getText().toString());
        EditText etLocation = (EditText) activityEdit.findViewById(R.id.editTextLocation);
        assertEquals("", etLocation.getText().toString());
        EditText etRoom = (EditText) activityEdit.findViewById(R.id.editTextRoom);
        assertEquals("", etRoom.getText().toString());
        etItem.setText("#TEST1");
        etLocation.setText("#LOCATION");
        etRoom.setText("#ROOM");
        Button saveButton = (Button) activityEdit.findViewById(R.id.buttonSaveItem);
        saveButton.performClick();
        mControllerItemEdit = mControllerItemEdit.pause().stop().destroy();

        mControllerItemList = mControllerItemList.restart().resume();
        itemList = (ListView) activity.findViewById(R.id.list);
        assertEquals(1, itemList.getCount());
        Map<String, String> datum = new HashMap<String, String>(2);
        datum.put("item", "#TEST1");
        datum.put("location", "#LOCATION\n#ROOM");
        assertEquals(datum, itemList.getItemAtPosition(0));
        mControllerItemList = mControllerItemList.pause().stop().destroy();
    }

    @Test
    public void testItemCreationWithoutRoom() throws Exception {
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = dbhelper.getCatalogTableDao();
        catalogDao.create(new CatalogTable("#TEST"));

        Intent intent = new Intent(ShadowApplication.getInstance().getApplicationContext(), ItemListActivity.class);
        intent.putExtra("CATALOG", "#TEST");
        mControllerItemList = Robolectric.buildActivity(ItemListActivity.class).withIntent(intent);
        ItemListActivity activity = mControllerItemList.create().start().resume().visible().get();
        ListView itemList = (ListView) activity.findViewById(R.id.list);
        assertEquals(1, itemList.getCount());
        assertEquals(activity.getString(R.string.message_no_item), itemList.getItemAtPosition(0).toString());
        mControllerItemList = mControllerItemList.pause().stop();

        intent = new Intent(ShadowApplication.getInstance().getApplicationContext(), ItemEditActivity.class);
        intent.putExtra("CATALOG", "#TEST");
        intent.putExtra("ITEM_ID", -1);
        mControllerItemEdit = Robolectric.buildActivity(ItemEditActivity.class).withIntent(intent);
        ItemEditActivity activityEdit = mControllerItemEdit.create().start().resume().visible().get();
        EditText etItem = (EditText) activityEdit.findViewById(R.id.editTextItem);
        assertEquals("", etItem.getText().toString());
        EditText etLocation = (EditText) activityEdit.findViewById(R.id.editTextLocation);
        assertEquals("", etLocation.getText().toString());
        EditText etRoom = (EditText) activityEdit.findViewById(R.id.editTextRoom);
        assertEquals("", etRoom.getText().toString());
        etItem.setText("#TEST1");
        etLocation.setText("#LOCATION");
        Button saveButton = (Button) activityEdit.findViewById(R.id.buttonSaveItem);
        saveButton.performClick();
        assertEquals("Beschreibung und Lagerraum müssen angegeben werden", ShadowToast.getTextOfLatestToast());
        activityEdit.finish();
        mControllerItemEdit = mControllerItemEdit.pause().stop().destroy();

        mControllerItemList = mControllerItemList.restart().resume();
        itemList = (ListView) activity.findViewById(R.id.list);
        assertEquals(1, itemList.getCount());
        assertEquals(activity.getString(R.string.message_no_item), itemList.getItemAtPosition(0));
        mControllerItemList = mControllerItemList.pause().stop().destroy();
    }

    @After
    public void tearDown() throws Exception {
        dbhelper.close();
    }
}