package ru.riselab.keitracker;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference prefAnonymousTracking = preferenceScreen.findPreference(
                SettingsActivity.KEY_PREF_ANONYMOUS_TRACKING);
        if (user == null && prefAnonymousTracking != null) {
            prefAnonymousTracking.setSummary(R.string.sign_in_for_enable);
            prefAnonymousTracking.setEnabled(false);
        }
    }
}
