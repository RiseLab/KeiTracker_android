package ru.riselab.keitracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Date;
import java.util.UUID;

import ru.riselab.keitracker.db.AppDatabase;
import ru.riselab.keitracker.db.dao.LocationDao;
import ru.riselab.keitracker.db.model.LocationModel;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 1;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";

    private Button mLocationButton;
    private TextView mLocationTextView;

    private boolean mTrackingLocation;
    private String mTrackUuid;
    private LocationDao mLocationDao;
    private Location mLastLocation;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationButton = findViewById(R.id.button_location);
        mLocationTextView = findViewById(R.id.textview_location);

        AppDatabase db = AppDatabase.getDatabase(this);
        mLocationDao = db.locationDao();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        if (savedInstanceState != null) {
//            mTrackingLocation = savedInstanceState.getBoolean(TRACKING_LOCATION_KEY);
//        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mLastLocation = locationResult.getLastLocation();
                if (mTrackingLocation) {
                    String locationText = getString(R.string.location_text,
                            mLastLocation.getLatitude(),
                            mLastLocation.getLongitude(),
                            mLastLocation.getTime());

                    mLocationTextView.setText(locationText);
                    sendNotification(locationText);
                }
            }
        };

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

//        outState.putBoolean(TRACKING_LOCATION_KEY, mTrackingLocation);
    }

    @Override
    protected void onPause() {
        super.onPause();

//        if (mTrackingLocation) {
//            stopTrackingLocation();
//            mTrackingLocation = true;
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (mTrackingLocation) {
//            stopTrackingLocation();
//        }
    }

    public void buttonLocationClick(View view) {
        mTrackUuid = UUID.randomUUID().toString();
        LocationModel currentLocation = new LocationModel(mTrackUuid,
                10.5, 10.5, 2.0, new Date());
        mLocationDao.insert(currentLocation);

        /*if (!mTrackingLocation) {
            startTrackingLocation();
        } else {
            stopTrackingLocation();
        }*/
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS);
        } else {
            startForegroundService();
            mTrackingLocation = true;
            mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
            mLocationButton.setText(R.string.stop_tracking);
        }
    }

    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            stopForegroundService();
            mTrackingLocation = false;
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mLocationButton.setText(R.string.start_tracking);
            mLocationTextView.setText("");
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

    private void sendNotification(String text) {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this,
                ForegroundService.CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_directions_walk_black_24dp)
//                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(ForegroundService.SERVICE_ID, notification);
    }
}
