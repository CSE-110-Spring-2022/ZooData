package edu.ucsd.cse110.zoodata_demo;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.view.View;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

import edu.ucsd.cse110.zoodata_demo.db.ZooDatabase;

@RunWith(AndroidJUnit4.class)
public class ExampleTest {
    @Rule
    public TestRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /** NOTE: THIS ISN'T WORKING **/
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

            // WHEN: this location is mocked...
            activity.mockLocationUpdate(coords);

            // THEN "NEARBY" is visible for Bali Mynah.
            var mynahNearbyIndicator = activity.getRecyclerView()
                .findViewHolderForItemId("mynah".hashCode()).itemView
                .findViewById(R.id.nearby_indicator);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            assertEquals("Mynah shown as NEARBY",
                View.VISIBLE,
                mynahNearbyIndicator.getVisibility()
            );
        });
    }
}
