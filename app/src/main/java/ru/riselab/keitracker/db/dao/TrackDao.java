package ru.riselab.keitracker.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.riselab.keitracker.db.model.TrackModel;

@Dao
public interface TrackDao {

    @Insert
    Long insert(TrackModel track);

    @Query("DELETE FROM track WHERE id = :id")
    void delete(Integer id);

    @Query("DELETE FROM track WHERE id in (:idList)")
    void delete(List<Integer> idList);

    @Update
    void update(TrackModel track);

    @Query("SELECT * FROM track ORDER BY id")
    LiveData<List<TrackModel>> getAllTracks();

    @Query("SELECT * FROM track WHERE id = :id")
    LiveData<TrackModel> getTrack(Integer id);
}
