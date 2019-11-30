package ru.riselab.keitracker;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ru.riselab.keitracker.adapters.TrackListAdapter;
import ru.riselab.keitracker.db.model.TrackModel;
import ru.riselab.keitracker.db.viewmodel.TrackViewModel;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_TRACK_ID = "ru.riselab.keitracker.extra.TRACK_ID";
    public static final String EXTRA_TRACK_NAME = "ru.riselab.keitracker.extra.TRACK_NAME";

    private static final int REQUEST_PERMISSIONS = 1;

    private ProgressBar mProgressBar;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mFab = findViewById(R.id.fab);
        mFab.setImageResource(isLocationServiceRunning() ? R.drawable.ic_stop_tracking : R.drawable.ic_start_tracking);

        RecyclerView recyclerView = findViewById(R.id.mainTrackList);
        final TrackListAdapter adapter = new TrackListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TrackViewModel trackViewModel = new ViewModelProvider(this).get(TrackViewModel.class);

        trackViewModel.getAllTracks().observe(this, new Observer<List<TrackModel>>() {
            @Override
            public void onChanged(@Nullable final List<TrackModel> tracks) {
                adapter.setTracks(tracks);
                mProgressBar.setVisibility(View.GONE);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void buttonLocationClick(View view) {
        if (!isLocationServiceRunning()) {
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

        } else if (!checkLocationEnabled()) {
            locationSettingsDialog();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogEditTrackView = inflater.inflate(R.layout.dialog_edit_track, null);
            EditText trackNameView = dialogEditTrackView.findViewById(R.id.trackName);
            builder.setView(dialogEditTrackView)
                    .setTitle(getString((R.string.track_dialog_new)))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String trackName = trackNameView.getText().toString();
                            if (trackName.length() == 0) {
                                trackName = getString(R.string.unnamed_track);
                            }
                            startForegroundService(trackName);
                            mFab.setImageResource(R.drawable.ic_stop_tracking);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }

    private void stopTrackingLocation() {
        if (isLocationServiceRunning()) {
            stopForegroundService();
            mFab.setImageResource(R.drawable.ic_start_tracking);
        }
    }

    private void startForegroundService(String trackName) {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra(MainActivity.EXTRA_TRACK_NAME, trackName);
        ActivityCompat.startForegroundService(this, serviceIntent);
    }

    private void stopForegroundService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

    public boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkLocationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {};
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {};
        return gpsEnabled || networkEnabled;
    }

    private void locationSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.enable_gps)
                .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
