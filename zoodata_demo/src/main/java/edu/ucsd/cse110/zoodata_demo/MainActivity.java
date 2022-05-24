package edu.ucsd.cse110.zoodata_demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.Consumer;

import edu.ucsd.cse110.zoodata_demo.databinding.MainBinding;
import edu.ucsd.cse110.zoodata_demo.location.PermissionChecker;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();
    public static final String EXTRA_LISTEN_TO_GPS = "listen_to_gps";

    @VisibleForTesting
    public MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Exhibits");

        // Check intent extras for flags.
        var listenToGps = getIntent().getBooleanExtra(EXTRA_LISTEN_TO_GPS, true);

        // MVP pattern: delegate responsibility for managing view and model to presenter, applying
        // dependency injection to provide the model and view to the presenter. Only responsibility
        // of MainActivity now is to wire these things up.

        var binding = MainBinding.inflate(getLayoutInflater());
        var view = binding.getRoot();
        setContentView(view);

        MainViewModel model = new ViewModelProvider(this).get(MainViewModel.class);
        presenter = new MainPresenter(this, model, binding);

        // If GPS is disabled (such as in a test), don't listen for updates from real GPS.
        if (listenToGps) setupLocationListener(presenter::updateLastKnownCoords);
    }

    @SuppressLint("MissingPermission")
    private void setupLocationListener(Consumer<Pair<Double, Double>> handleNewCoords) {
        new PermissionChecker(this).ensurePermissions();

        // Connect location listener to the model.
        var provider = LocationManager.GPS_PROVIDER;
        var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        var locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                var coords = Pair.create(
                    location.getLatitude(),
                    location.getLongitude()
                );
                handleNewCoords.accept(coords);
            }
        };
        locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
    }

    @VisibleForTesting
    void mockLocationUpdate(Pair<Double, Double> coords) {
        runOnUiThread(() -> {
            presenter.updateLastKnownCoords(coords);
        });
    }

    @VisibleForTesting
    RecyclerView getRecyclerView() {
        return presenter.getRecyclerView();
    }
}