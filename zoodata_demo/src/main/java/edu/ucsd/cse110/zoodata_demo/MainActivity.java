package edu.ucsd.cse110.zoodata_demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import java.util.function.BiConsumer;

import edu.ucsd.cse110.zoodata_demo.databinding.MainBinding;
import edu.ucsd.cse110.zoodata_demo.location.PermissionChecker;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    @VisibleForTesting
    public MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Exhibits");

        // Check intent extras.
        var listenToGps = getIntent().getBooleanExtra("listen_to_gps", true);

        // MVP pattern: delegate responsibility for managing view and model to presenter, applying
        // dependency injection to provide the model and view to the presenter. Only responsibility
        // of MainActivity now is to wire these things up.
        MainBinding view = DataBindingUtil.setContentView(this, R.layout.main);
        MainViewModel model = new ViewModelProvider(this).get(MainViewModel.class);
        presenter = new MainPresenter(this, model, view);

        // If GPS is disabled (such as in a test), don't listen for updates from real GPS.
        if (listenToGps) setupLocationListener(presenter::updateLastKnownCoords);
    }

    @SuppressLint("MissingPermission")
    private void setupLocationListener(BiConsumer<Double, Double> handleNewCoords) {
        new PermissionChecker(this).ensurePermissions();

        // Connect location listener to the model.
        var provider = LocationManager.GPS_PROVIDER;
        var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        var locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                handleNewCoords.accept(location.getLatitude(), location.getLongitude());
            }
        };
        locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
    }
}