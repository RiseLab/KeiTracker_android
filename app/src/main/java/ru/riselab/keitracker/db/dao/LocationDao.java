package ru.riselab.keitracker.db.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import ru.riselab.keitracker.db.model.LocationModel;
import ru.riselab.keitracker.db.pojo.Track;

@Dao
public interface LocationDao {

    @Insert
    void insert(LocationModel location);

    @Query("SELECT * FROM location WHERE id = :id")
    LocationModel getLocation(Integer id);

    @Query("SELECT track_uuid, MIN(fixed_at) first_time, MAX(fixed_at) last_time FROM location GROUP BY track_uuid ORDER BY first_time")
    LiveData<List<Track>> getAllTracks();

    @Query("SELECT * FROM location WHERE track_uuid = :trackUuid")
    LiveData<List<LocationModel>> getTrackLocations(String trackUuid);

    @Query("DELETE FROM location WHERE track_uuid = :trackUuid")
    void deleteTrackLocations(String trackUuid);
}
