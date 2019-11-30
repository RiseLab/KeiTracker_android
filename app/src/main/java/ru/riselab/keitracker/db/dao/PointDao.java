package ru.riselab.keitracker.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ru.riselab.keitracker.db.model.PointModel;

@Dao
public interface PointDao {

    @Insert
    void insert(PointModel point);

    @Query("DELETE FROM point WHERE track_id = :trackId")
    void deleteTrackPoints(Integer trackId);

    @Query("SELECT * FROM point WHERE track_id = :trackId")
    LiveData<List<PointModel>> getTrackPoints(Integer trackId);
}
