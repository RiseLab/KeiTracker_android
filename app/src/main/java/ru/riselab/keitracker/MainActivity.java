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
import ru.riselab.keitracker.db.viewmodel.TrackViewModel;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_TRACK_ID = "ru.riselab.keitracker.extra.TRACK_ID";
    public static final String EXTRA_TRACK_NAME = "ru.riselab.keitracker.extra.TRACK_NAME";

    private static final int RC_SIGN_IN = 11;

    private static final int REQUEST_PERMISSIONS = 1;

    private Menu mMenu;

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

        trackViewModel.getAllTracks().observe(this, tracks -> {
            adapter.setTracks(tracks);
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
                toggleMenuSignOptions(true);
                Toast.makeText(
                        this, R.string.sign_in_success_message,
                        Toast.LENGTH_LONG).show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getMenuInflater().inflate(R.menu.main_options, menu);
        toggleMenuSignOptions(user != null);
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
                            toggleMenuSignOptions(false);
                            Toast.makeText(this, R.string.sign_out_success_message,
                                    Toast.LENGTH_LONG).show();
                        });
                return true;
            case R.id.main_option_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
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
                        if (trackName.length() == 0) {
                            trackName = getString(R.string.unnamed_track);
                        }
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

    private void toggleMenuSignOptions(boolean isSignedIn) {
        mMenu.findItem(R.id.main_option_sign_in).setVisible(!isSignedIn);
        mMenu.findItem(R.id.main_option_sign_out).setVisible(isSignedIn);
    }
}
