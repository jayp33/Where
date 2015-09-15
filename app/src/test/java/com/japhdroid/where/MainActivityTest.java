package com.japhdroid.where;

import android.app.AlertDialog;
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

    ActivityController<MainActivity> mController;

    @Before
    public void setUp() throws Exception {
        mController = Robolectric.buildActivity(MainActivity.class);
    }

    @Test
    public void testEmptyCatalogListViewContainsPlaceholder() throws Exception {
        MainActivity activity = mController.create().start().resume().visible().get();
        ListView list = (ListView) activity.findViewById(R.id.list);
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = activity.getHelper().getCatalogTableDao();
        if (!DataProvider.ExistCatalogs(catalogDao)) {
            assertEquals(list.getCount(), 1);
            assertEquals(list.getItemAtPosition(0).toString(), activity.getString(R.string.message_no_catalog));
        }
    }

    @Test
    public void testEmptyItemListViewContainsPlaceholder() throws Exception {
        MainActivity activity = mController.create().start().resume().visible().get();
        RuntimeExceptionDao<CatalogTable, Integer> catalogDao = activity.getHelper().getCatalogTableDao();
        assertFalse(DataProvider.ExistCatalogs(catalogDao));
        // Click on placeholder in MainActivity
        ListView catalogList = (ListView) activity.findViewById(R.id.list);
        ListAdapter adapter = catalogList.getAdapter();
        View itemView = adapter.getView(0, null, catalogList);
        catalogList.performItemClick(itemView, 0, adapter.getItemId(0));
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

        // TODO Open created catalog
        // Assert placeholder in ItemListActivity
    }

    @After
    public void tearDown() throws Exception {
        mController = mController.pause().stop().destroy();
    }
}