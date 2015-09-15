package com.japhdroid.where;

import android.widget.ListView;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertEquals;

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

    @After
    public void tearDown() throws Exception {
        mController = mController.pause().stop().destroy();
    }
}