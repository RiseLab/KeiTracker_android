package ru.riselab.keitracker.db.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import ru.riselab.keitracker.db.AppDatabase;
import ru.riselab.keitracker.db.dao.LocationDao;
import ru.riselab.keitracker.db.model.LocationModel;
import ru.riselab.keitracker.db.pojo.Track;

public class LocationRepository {

    private LocationDao mLocationDao;
    private LiveData<List<Track>> mAllTracks;

    public LocationRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mLocationDao = db.locationDao();
        mAllTracks = mLocationDao.getAllTracks();
    }

    public LiveData<List<Track>> getAllTracks() {
        return mAllTracks;
    }

    public LiveData<List<LocationModel>> getTrackLocations(String trackUuid) {
        return mLocationDao.getTrackLocations(trackUuid);
    }

    public void insert(final LocationModel location) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mLocationDao.insert(location);
        });
    }
}
