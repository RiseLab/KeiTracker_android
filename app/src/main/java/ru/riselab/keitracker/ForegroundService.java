package ru.riselab.keitracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Random;

import ru.riselab.keitracker.db.model.PointModel;
import ru.riselab.keitracker.db.model.TrackModel;
import ru.riselab.keitracker.db.repository.PointRepository;
import ru.riselab.keitracker.db.repository.TrackRepository;

public class ForegroundService extends Service {

    public static final int SERVICE_ID = 1;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    private static final String USERS_PUBLIC_REF = "users/public";
    private static final String USERS_PRIVATE_REF = "users/private";

    private int mPrefLocationRetrievingInterval;

    private TrackRepository mTrackRepository;
    private PointRepository mPointRepository;

    private Location mLastLocation;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();

        mTrackRepository = new TrackRepository(getApplication());
        mPointRepository = new PointRepository(getApplication());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get application settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefLocationRetrievingInterval = Integer.parseInt(sharedPreferences.getString(
                SettingsActivity.KEY_PREF_LOCATION_RETRIEVING_INTERVAL, "10000"));
        boolean prefAnonymousTracking = sharedPreferences.getBoolean(
                SettingsActivity.KEY_PREF_ANONYMOUS_TRACKING, false);

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance();
        DatabaseReference firebaseDbRef = firebaseDb.getReference(USERS_PUBLIC_REF);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mLastLocation = locationResult.getLastLocation();

                // Save location data to Room DB
                PointModel pointModel = new PointModel(mTrackRepository.getLastInsertedId(),
                        mLastLocation.getLatitude(),
                        mLastLocation.getLongitude(),
                        mLastLocation.getAltitude(),
                        mLastLocation.getTime());
                mPointRepository.insert(pointModel);

                // Save location data to Firebase DB
                if (firebaseUser != null && prefAnonymousTracking){
                    firebaseDbRef.child(firebaseUser.getUid())
                            .child("checkpoints")
                            .push()
                            .setValue(pointModel);
                }

                // Show location data in notification
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
        String trackName = intent.getStringExtra(MainActivity.EXTRA_TRACK_NAME);
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Date date = new Date();
        TrackModel trackModel = new TrackModel(trackName, date.getTime(), null);
        mTrackRepository.insert(trackModel);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(String.format("%s: %s", getString(R.string.start_recording_new_track), trackName))
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
        locationRequest.setInterval(mPrefLocationRetrievingInterval);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}
