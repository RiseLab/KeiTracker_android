package ru.riselab.keitracker;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

import ru.riselab.keitracker.adapters.TrackListAdapter;
import ru.riselab.keitracker.db.repository.PointRepository;
import ru.riselab.keitracker.db.repository.TrackRepository;
import ru.riselab.keitracker.db.viewmodel.TrackViewModel;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_TRACK_ID = "ru.riselab.keitracker.extra.TRACK_ID";
    public static final String EXTRA_TRACK_NAME = "ru.riselab.keitracker.extra.TRACK_NAME";
    public static final String EXTRA_NOTIFICATION_ACTION = "ru.riselab.keitracker.extra.NOTIFICATION_ACTION";

    public static final int CMD_STOP = 1;

    private static final int RC_SIGN_IN = 11;

    private static final int REQUEST_PERMISSIONS = 1;

    private Menu mMenu;

    private ProgressBar mProgressBar;
    private FloatingActionButton mFab;

    private TrackListAdapter mTrackListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mFab = findViewById(R.id.fab);
        mFab.setImageResource(isLocationServiceRunning() ? R.drawable.ic_stop_tracking : R.drawable.ic_start_tracking);

        int notificationAction = getIntent().getIntExtra(EXTRA_NOTIFICATION_ACTION, 0);
        if (notificationAction == CMD_STOP) {
            stopTrackingLocation();
        }

        RecyclerView recyclerView = findViewById(R.id.mainTrackList);
        mTrackListAdapter = new TrackListAdapter(this);
        recyclerView.setAdapter(mTrackListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TrackViewModel trackViewModel = new ViewModelProvider(this).get(TrackViewModel.class);

        trackViewModel.getAllTracks().observe(this, tracks -> {
            mTrackListAdapter.setTracks(tracks);
            mProgressBar.setVisibility(View.GONE);
        });
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                toggleMenuSignOptions();
                Toast.makeText(
                        this, R.string.sign_in_success_message,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.main_options, menu);
        toggleMenuSignOptions();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_option_sign_in:
                List<AuthUI.IdpConfig> providers = Collections.singletonList(
                        new AuthUI.IdpConfig.GoogleBuilder().build());
                startActivityForResult(
                        AuthUI.getInstance().
                                createSignInIntentBuilder().
                                setAvailableProviders(providers)
                                .setIsSmartLockEnabled(false)
                                .build(),
                        RC_SIGN_IN);
                return true;
            case R.id.main_option_sign_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> {
                            toggleMenuSignOptions();
                            Toast.makeText(this, R.string.sign_out_success_message,
                                    Toast.LENGTH_LONG).show();
                        });
                return true;
            case R.id.main_option_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.main_option_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            case R.id.main_option_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString((R.string.delete_selected_tracks)))
                        .setPositiveButton(R.string.ok, (dialog, which) -> {
                            TrackRepository trackRepository = new TrackRepository(getApplication());
                            PointRepository pointRepository = new PointRepository(getApplication());

                            List<Integer> selectedTracks = mTrackListAdapter.getSelectedTracks();

                            pointRepository.deleteTrackPoints(selectedTracks);
                            trackRepository.delete(selectedTracks);

                            toggleMenuEditMode(false);
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        String trackName = trackNameView.getText().toString();
                        startForegroundService(trackName);
                        mFab.setImageResource(R.drawable.ic_stop_tracking);
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
                .setPositiveButton(R.string.settings, (dialog, which) -> startActivity(
                        new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void toggleMenuSignOptions() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mMenu.findItem(R.id.main_option_sign_in).setVisible(user == null);
        mMenu.findItem(R.id.main_option_sign_out).setVisible(user != null);
    }

    public void toggleMenuEditMode(boolean isEditMode) {
        mMenu.setGroupVisible(R.id.main_options_primary, !isEditMode);
        mMenu.setGroupVisible(R.id.main_options_edit, isEditMode);

        if (!isEditMode) {
            mFab.show();
            toggleMenuSignOptions();
        } else {
            mFab.hide();
        }
    }

    public void setItemsSelectedText(Integer first, Integer second) {
        mMenu.findItem(R.id.main_option_selected)
                .setTitle(String.format(getString(R.string.items_selected), first, second));
    }
}
