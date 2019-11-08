package ru.riselab.keitracker.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import ru.riselab.keitracker.db.model.LocationModel;

@Dao
public interface LocationDao {

    @Insert
    void insert(LocationModel location);

    @Query("SELECT * FROM location WHERE id = :id")
    LocationModel getLocation(Integer id);
}
