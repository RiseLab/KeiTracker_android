package ru.riselab.keitracker.db.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ru.riselab.keitracker.db.model.TrackModel;
import ru.riselab.keitracker.db.repository.TrackRepository;

public class TrackViewModel extends AndroidViewModel {

    private TrackRepository mRepository;

    private LiveData<List<TrackModel>> mAllTracks;

    public TrackViewModel(@NonNull Application application) {
        super(application);
        mRepository = new TrackRepository(application);
        mAllTracks = mRepository.getAllTracks();
    }

    public LiveData<List<TrackModel>> getAllTracks() {
        return mAllTracks;
    }
}
