package edu.ucsd.cse110.zoodata_demo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import edu.ucsd.cse110.zoodata_demo.databinding.MainBinding;
import edu.ucsd.cse110.zoodata_demo.location.PermissionChecker;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    private MainViewModel viewModel;
    private final PermissionChecker permissionChecker = new PermissionChecker(this);

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Exhibits");

        var disableGps = getIntent().getBooleanExtra("disable_gps", false);

        MainBinding binding = DataBindingUtil.setContentView(this, R.layout.main);

        // Set up the adapter.
        var adapter = new ExhibitListAdapter();

        // Set up the view model and bind to the adapter.
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getExhibitsWithGroups().observe(this, adapter::setExhibitsWithGroups);
        viewModel.getLastKnownCoords().observe(this, adapter::setLastKnownCoords);

        // Set up the recycler view.
        var recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set up the mocking button.
        var mockButton = binding.mockButton;
        mockButton.setOnClickListener(this::onMockButtonClicked);

        // Set up location.
        // Check permissions before proceeding.
        permissionChecker.ensurePermissions();

        var provider = LocationManager.GPS_PROVIDER;
        var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        var locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(LOG_TAG, String.format("Location changed: %s", location));
                var pair = Pair.create(location.getLatitude(), location.getLongitude());
                viewModel.setLastKnownCoords(pair);
            }
        };

        if (!disableGps) {
            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
        }
    }

    /**
     * TODO: should refactor this out into its own class (presenter?) probably...
     */
    @SuppressLint("SetTextI18n")
    public void onMockButtonClicked(View view) {
        // TODO: define this layout in an XML and inflate it, instead of defining in code.
        var inputType = EditorInfo.TYPE_CLASS_NUMBER
            | EditorInfo.TYPE_NUMBER_FLAG_SIGNED
            | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL;

        final EditText latInput = new EditText(this);
        latInput.setInputType(inputType);
        latInput.setHint("Latitude");
        latInput.setText("32.746303");

        final EditText lngInput = new EditText(this);
        lngInput.setInputType(inputType);
        lngInput.setHint("Longitude");
        lngInput.setText("-117.166595");

        final LinearLayout layout = new LinearLayout(this);
        layout.setDividerPadding(8);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(latInput);
        layout.addView(lngInput);

        var builder = new AlertDialog.Builder(this)
            .setTitle("Inject a Mock Location")
            .setView(layout)
            .setPositiveButton("Submit", (dialog, which) -> {
                var lat = Double.parseDouble(latInput.getText().toString());
                var lng = Double.parseDouble(lngInput.getText().toString());
                mockLocation(lat, lng);
            })
            .setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
            });
        builder.show();
    }

    @VisibleForTesting
    public void mockLocation(double lat, double lng ) {
        runOnUiThread(() -> {
            viewModel.setLastKnownCoords(Pair.create(lat, lng));
        });
    }
}