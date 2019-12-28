package ru.riselab.keitracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_PREF_ANONYMOUS_TRACKING =
            "pref_anonymous_tracking";
    public static final String KEY_PREF_LOCATION_RETRIEVING_INTERVAL =
            "pref_location_retrieving_interval";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.settings);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
