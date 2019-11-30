package ru.riselab.keitracker.db.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import ru.riselab.keitracker.db.AppDatabase;
import ru.riselab.keitracker.db.dao.TrackDao;
import ru.riselab.keitracker.db.model.TrackModel;

public class TrackRepository {

    private TrackDao mTrackDao;
    private LiveData<List<TrackModel>> mAllTracks;

    public TrackRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTrackDao = db.trackDao();
        mAllTracks = mTrackDao.getAllTracks();
    }

    public LiveData<List<TrackModel>> getAllTracks() {
        return mAllTracks;
    }

    public void insert(final TrackModel track) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTrackDao.insert(track);
        });
    }

    public void delete(final Integer trackId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTrackDao.delete(trackId);
        });
    }
}
