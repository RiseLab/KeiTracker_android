package ru.riselab.keitracker.db.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ru.riselab.keitracker.db.model.PointModel;
import ru.riselab.keitracker.db.repository.PointRepository;

public class PointViewModel extends AndroidViewModel {

    private PointRepository mRepository;

    private LiveData<List<PointModel>> mTrackPoints;

    public PointViewModel(@NonNull Application application) {
        super(application);
        mRepository = new PointRepository(application);
    }

    public LiveData<List<PointModel>> getTrackPoints(Integer trackId) {
        return mRepository.getTrackPoints(trackId);
    }
}
