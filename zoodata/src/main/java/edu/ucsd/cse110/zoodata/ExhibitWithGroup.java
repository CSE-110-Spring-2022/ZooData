package edu.ucsd.cse110.zoodata;

import android.annotation.SuppressLint;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.Optional;

/**
 * Represents a group, with an optional group.
 */
public class ExhibitWithGroup {
    @Embedded public Exhibit exhibit;
    @Relation(
        parentColumn = "group_id",
        entityColumn = "id"
    )
    public Exhibit group = null;

    public String getExhibitName() {
        return exhibit.name;
    }

    public String getGroupName() {
        if (group == null) return " ";
        return group.name;
    }

    @SuppressLint("DefaultLocale")
    public String getCoords() {
        Double lat, lng;
        if (group != null) {
            lat = group.lat;
            lng = group.lng;
        } else {
            lat = exhibit.lat;
            lng = exhibit.lng;
        }
        return String.format("%3.6f, %3.6f", lat, lng);
    }
}
