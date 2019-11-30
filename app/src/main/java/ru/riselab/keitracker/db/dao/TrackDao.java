package ru.riselab.keitracker.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ru.riselab.keitracker.db.model.TrackModel;

@Dao
public interface TrackDao {

    @Insert
    Long insert(TrackModel track);

    @Query("DELETE FROM track WHERE id = :id")
    void delete(Integer id);

    @Query("SELECT * FROM track ORDER BY id")
    LiveData<List<TrackModel>> getAllTracks();
}
