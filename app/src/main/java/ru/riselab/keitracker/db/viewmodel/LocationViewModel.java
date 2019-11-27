package ru.riselab.keitracker.db.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ru.riselab.keitracker.db.model.LocationModel;
import ru.riselab.keitracker.db.pojo.Track;
import ru.riselab.keitracker.db.repository.LocationRepository;

public class LocationViewModel extends AndroidViewModel {

    private LocationRepository mRepository;

    private LiveData<List<Track>> mAllTracks;

    public LocationViewModel(@NonNull Application application) {
        super(application);
        mRepository = new LocationRepository(application);
        mAllTracks = mRepository.getAllTracks();
    }

    public LiveData<List<Track>> getAllTracks() {
        return mAllTracks;
    }

    public LiveData<List<LocationModel>> getTrackLocations(String trackUuid) {
        return mRepository.getTrackLocations(trackUuid);
    }
}
