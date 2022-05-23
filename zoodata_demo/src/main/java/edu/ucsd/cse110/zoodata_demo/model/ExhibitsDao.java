package edu.ucsd.cse110.zoodata_demo.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import edu.ucsd.cse110.zoodata.ExhibitWithGroup;
import edu.ucsd.cse110.zoodata.Exhibit;

@Dao
public interface ExhibitsDao {
    @Insert
    void insert(Exhibit... exhibits);

    @Insert
    void insert(List<Exhibit> exhibits);

    @Query("SELECT COUNT(*) from exhibits")
    long count();

    @Transaction
    @Query("SELECT * FROM exhibits")
    List<ExhibitWithGroup> getAllWithGroups();
}
