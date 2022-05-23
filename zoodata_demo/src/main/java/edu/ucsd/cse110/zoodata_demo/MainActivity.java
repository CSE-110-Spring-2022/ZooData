package edu.ucsd.cse110.zoodata_demo;

import android.os.Bundle;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Comparator;
import java.util.stream.Collectors;

import edu.ucsd.cse110.zoodata_demo.databinding.MainBinding;
import edu.ucsd.cse110.zoodata_demo.model.ZooDatabase;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Exhibits");

        MainBinding binding = DataBindingUtil.setContentView(this, R.layout.main);

        // Set up the database.
        // Will always reload from the database, unless this is commented out.
        ZooDatabase.setForcePopulate();
        var db = ZooDatabase.getDatabase(this);

        // Set up the adapter.
        var adapter = new ExhibitListAdapter();
        var exhibitsWithGroups = db.exhibitsDao()
            .getAllWithGroups()
            .stream()
            .filter(e -> e.exhibit.isExhibit())
            .sorted(Comparator.comparing(e -> e.exhibit.name))
            .collect(Collectors.toList());
        adapter.setExhibitsWithGroups(exhibitsWithGroups);
        adapter.setLastKnownCoords(Pair.create(32.737986, -117.169499));

        // Set up the recycler view.
        var recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}