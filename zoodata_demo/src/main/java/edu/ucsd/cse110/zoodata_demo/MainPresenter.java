package edu.ucsd.cse110.zoodata_demo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.ucsd.cse110.zoodata_demo.databinding.MainBinding;

public class MainPresenter {
    private final MainActivity activity;
    private final MainViewModel model;
    private final MainBinding view;
    private final RecyclerView recyclerView;

    public MainPresenter(MainActivity activity, MainViewModel model, MainBinding view) {
        this.activity = activity;
        this.model = model;
        this.view = view;

        var adapter = new ExhibitListAdapter(activity);

        // Set up the recycler view.
        recyclerView = view.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);

        // Set up the mocking button.
        var mockButton = view.mockButton;
        mockButton.setOnClickListener(this::onMockButtonClicked);

        model.getExhibitsWithGroups().observe(activity, adapter::setExhibitsWithGroups);
        model.getLastKnownCoords().observe(activity, adapter::setLastKnownCoords);
    }

    public void updateLastKnownCoords(Pair<Double, Double> coords) {
        model.setLastKnownCoords(coords);
    }

    @SuppressLint("SetTextI18n")
    private void onMockButtonClicked(View view) {
        // TODO: could define this layout in an XML and inflate it, instead of defining in code...
        var inputType = EditorInfo.TYPE_CLASS_NUMBER
            | EditorInfo.TYPE_NUMBER_FLAG_SIGNED
            | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL;

        final EditText latInput = new EditText(activity);
        latInput.setInputType(inputType);
        latInput.setHint("Latitude");
        latInput.setText("32.737986");

        final EditText lngInput = new EditText(activity);
        lngInput.setInputType(inputType);
        lngInput.setHint("Longitude");
        lngInput.setText("-117.169499");

        final LinearLayout layout = new LinearLayout(activity);
        layout.setDividerPadding(8);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(latInput);
        layout.addView(lngInput);

        var builder = new AlertDialog.Builder(activity)
            .setTitle("Inject a Mock Location")
            .setView(layout)
            .setPositiveButton("Submit", (dialog, which) -> {
                var lat = Double.parseDouble(latInput.getText().toString());
                var lng = Double.parseDouble(lngInput.getText().toString());
                updateLastKnownCoords(Pair.create(lat, lng));
            })
            .setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
            });
        builder.show();
    }

    @VisibleForTesting
    RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
