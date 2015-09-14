package com.japhdroid.where;

import android.widget.ListView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Created by User on 14.09.2015.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {
    @Test
    public void testEmptyListViewContainsPlaceholder() throws Exception {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        ListView list = (ListView) activity.findViewById(R.id.list);
        assertEquals(list.getCount(), 1);
        assertEquals(list.getItemAtPosition(0).toString(), activity.getString(R.string.message_no_catalog));
    }
}