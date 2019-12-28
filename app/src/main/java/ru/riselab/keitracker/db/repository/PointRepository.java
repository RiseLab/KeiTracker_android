package ru.riselab.keitracker.db.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import ru.riselab.keitracker.db.AppDatabase;
import ru.riselab.keitracker.db.dao.PointDao;
import ru.riselab.keitracker.db.model.PointModel;

public class PointRepository {

    private PointDao mPointDao;
    private LiveData<List<PointModel>> mTrackPoints;

    public PointRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mPointDao = db.pointDao();
    }

    public LiveData<List<PointModel>> getTrackPoints(Integer trackId) {
        return mPointDao.getTrackPoints(trackId);
    }

    public void insert(final PointModel point) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mPointDao.insert(point);
        });
    }

    public void deleteTrackPoints(final Integer trackId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mPointDao.deleteTrackPoints(trackId);
        });
    }

    public void deleteTrackPoints(final List<Integer> trackIdList) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mPointDao.deleteTrackPoints(trackIdList);
        });
    }
}
