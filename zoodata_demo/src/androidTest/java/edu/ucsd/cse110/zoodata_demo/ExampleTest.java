package edu.ucsd.cse110.zoodata_demo;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStreamReader;

import edu.ucsd.cse110.zoodata_demo.db.ZooDatabase;

@RunWith(AndroidJUnit4.class)
public class ExampleTest {
    @Rule
    public TestRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    public static void busyWait() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_live_data_behaving() {
        /*
        This test will only work with the InstantTaskExecutorRule above. Otherwise,
        the liveData will not be updated *synchronously* between line 43 and 44.
         */
        var liveData = new MutableLiveData<String>();
        liveData.postValue("test");
        assertEquals("test", liveData.getValue());
    }

    @LargeTest
    @Test
    public void test_nearby_shown_on_location_update() throws IOException {
        var context = ApplicationProvider.getApplicationContext();

        // Use a mock database, not whatever is stored on the device.
        var db = Room.inMemoryDatabaseBuilder(context, ZooDatabase.class)
            .allowMainThreadQueries()
            .build();

        // Populate from the default assets (note: in your own tests, perhaps use test-only assets?)
        var exhibitsReader = new InputStreamReader(context.getAssets().open("exhibit_info.json"));
        var trailsReader = new InputStreamReader(context.getAssets().open("trail_info.json"));
        ZooDatabase.populate(context, db, exhibitsReader, trailsReader);
        ZooDatabase.injectTestDatabase(db);

        var intent = new Intent(context, MainActivity.class);

        // Disable real GPS updates so they don't mess with our testing.
        intent.putExtra(MainActivity.EXTRA_LISTEN_TO_GPS, false);

        var scenario = ActivityScenario.<MainActivity>launch(intent);

        // THIS IS CRITICAL WHEN USING LIVEDATA.
        // Without it, model.getLastKnownCoords().observe(...) will not trigger
        // because the lifecycle owner it is scoped to has not been started!
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            // GIVEN: the coordinates of the Mynah...
            var mynah = db.exhibitsDao().getExhibitWithGroupById("mynah");
            var coords = mynah.getCoords();

            Log.d("FOOBAR", String.format("Mocking location update to %s...", coords.toString()));

            // WHEN: this location is mocked...
            activity.mockLocationUpdate(coords);

        });

        // FIXED BUG: if we put both the GIVEN/WHEN and THEN in the
        // same onActivity block, the update isn't applied until AFTER
        // this assertion for some very weird reason. (Darn it, Android!)

        // We can (somehow) get around this by putting the check in a
        // second onActivity block. This creates the necessary waiting
        // such that the visibility changes have properly happened.

        // You end up seeing a sequence of events like this, where the update
        // happens AFTER the asset.
        /*
            D/FOOBAR: Updating adapter lastKnownCoords to Pair{32.73798565400121 -117.16949876733686} and notifying of changes...
            D/FOOBAR: Retrieving view holder and nearby indicator...
            D/FOOBAR: Asserting nearby indicator updated to visible...
            D/FOOBAR: Setting visibility of mynah to VISIBLE!
            D/FOOBAR: Setting visibility of dove to VISIBLE!
         */

        scenario.onActivity(activity -> {
            Log.d("FOOBAR", "Retrieving view holder and nearby indicator...");

            // THEN: the NEARBY indicator is visible for the Mynah item.
            TextView mynahNearbyIndicator = activity.getRecyclerView()
                .findViewHolderForItemId("mynah".hashCode()).itemView
                .findViewById(R.id.nearby_indicator);

            Log.d("FOOBAR", "Asserting nearby indicator updated to visible...");
            assertEquals("Mynah shown as NEARBY",
                View.VISIBLE,
                mynahNearbyIndicator.getVisibility()
            );
        });
    }
}
