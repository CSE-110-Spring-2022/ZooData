package edu.ucsd.cse110.zoodata_demo;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import edu.ucsd.cse110.zoodata.ExhibitWithGroup;
import edu.ucsd.cse110.zoodata_demo.db.ZooDatabase;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<List<ExhibitWithGroup>> exhibitsWithGroups;
    private final MutableLiveData<Pair<Double, Double>> lastKnownCoords;

    public MainViewModel(@NonNull Application application) {
        super(application);

        // Will always reload from the database, unless this is commented out.
        ZooDatabase.setForcePopulate();
        var db = ZooDatabase.getDatabase(application.getApplicationContext());

        exhibitsWithGroups = db.exhibitsDao().getExhibitsWithGroupsLive();
        lastKnownCoords = new MutableLiveData<>(null);
    }

    public LiveData<List<ExhibitWithGroup>> getExhibitsWithGroups() {
        return exhibitsWithGroups;
    }

    public LiveData<Pair<Double, Double>> getLastKnownCoords() {
        return lastKnownCoords;
    }

    public void setLastKnownCoords(Pair<Double, Double> coords) {
        lastKnownCoords.setValue(coords);
    }
}
