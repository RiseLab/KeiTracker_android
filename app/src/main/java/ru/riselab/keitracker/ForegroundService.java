package ru.riselab.keitracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

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

public class ForegroundService extends Service {

    public static final int SERVICE_ID = 1;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    private String mTrackUuid;

    private LocationDao mLocationDao;

    private Location mLastLocation;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();

        mTrackUuid = UUID.randomUUID().toString();

        AppDatabase db = AppDatabase.getDatabase(this);
        mLocationDao = db.locationDao();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mLastLocation = locationResult.getLastLocation();

                LocationModel locationModel = new LocationModel(mTrackUuid,
                        mLastLocation.getLatitude(),
                        mLastLocation.getLongitude(),
                        mLastLocation.getAltitude(),
                        new Date());
                mLocationDao.insert(locationModel);

                String locationText = getString(R.string.location_text,
                        mLastLocation.getLatitude(),
                        mLastLocation.getLongitude(),
                        new Date(mLastLocation.getTime()));
                sendNotification(locationText);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_directions_walk_black_24dp)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(SERVICE_ID, notification);

        mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void sendNotification(String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_directions_walk_black_24dp)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(SERVICE_ID, notification);
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}
