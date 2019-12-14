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

    private Integer mLastInsertedId;

    public TrackRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTrackDao = db.trackDao();
        mAllTracks = mTrackDao.getAllTracks();
    }

    public LiveData<List<TrackModel>> getAllTracks() {
        return mAllTracks;
    }

    public LiveData<TrackModel> getTrack(Integer id) {
        return mTrackDao.getTrack(id);
    }

    public void insert(final TrackModel track) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mLastInsertedId = mTrackDao.insert(track).intValue();
        });
    }

    public void delete(final Integer trackId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTrackDao.delete(trackId);
        });
    }

    public void update(final TrackModel track) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTrackDao.update(track);
        });
    }

    public Integer getLastInsertedId() {
        return mLastInsertedId;
    }
}
