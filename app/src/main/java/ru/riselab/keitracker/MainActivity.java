package ru.riselab.keitracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ru.riselab.keitracker.adapters.TrackListAdapter;
import ru.riselab.keitracker.db.dao.LocationDao;
import ru.riselab.keitracker.db.pojo.Track;
import ru.riselab.keitracker.db.viewmodel.LocationViewModel;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 1;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";

    private FloatingActionButton mFab;

    private boolean mTrackingLocation;

    private LocationDao mLocationDao;
    private LocationViewModel mLocationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFab = findViewById(R.id.fab);

        /*if (savedInstanceState != null) {
            mTrackingLocation = savedInstanceState.getBoolean(TRACKING_LOCATION_KEY);
        }*/

        RecyclerView recyclerView = findViewById(R.id.mainTrackList);
        final TrackListAdapter adapter = new TrackListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mLocationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        mLocationViewModel.getAllTracks().observe(this, new Observer<List<Track>>() {
            @Override
            public void onChanged(@Nullable final List<Track> tracks) {
                adapter.setTracks(tracks);
            }
        });

        // TODO: remove on release
        Stetho.initializeWithDefaults(getApplicationContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTrackingLocation();
                } else {
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        /*outState.putBoolean(TRACKING_LOCATION_KEY, mTrackingLocation);*/
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*if (mTrackingLocation) {
            stopTrackingLocation();
            mTrackingLocation = true;
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if (mTrackingLocation) {
            stopTrackingLocation();
        }*/
    }

    public void buttonLocationClick(View view) {
        if (!mTrackingLocation) {
            startTrackingLocation();
        } else {
            stopTrackingLocation();
        }
    }

    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS);
        } else {
            startForegroundService();
            mTrackingLocation = true;
            mFab.setImageResource(R.drawable.ic_stop_tracking);
        }
    }

    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            stopForegroundService();
            mTrackingLocation = false;
            mFab.setImageResource(R.drawable.ic_start_tracking);
        }
    }

    private void startForegroundService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Recording a New Track");
        ActivityCompat.startForegroundService(this, serviceIntent);
    }

    private void stopForegroundService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }
}
