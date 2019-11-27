package ru.riselab.keitracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import ru.riselab.keitracker.adapters.TrackTabsPagerAdapter;
import ru.riselab.keitracker.db.repository.LocationRepository;

public class TrackActivity extends AppCompatActivity {

    private String mTrackUuid = "";
    private LocationRepository mLocationRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        mLocationRepository = new LocationRepository(getApplication());

        Intent intent = getIntent();
        mTrackUuid = intent.getStringExtra(MainActivity.EXTRA_TRACK_UUID);

        TabLayout trackTabLayout = findViewById(R.id.trackTabLayout);

        trackTabLayout.addTab(trackTabLayout.newTab().setIcon(R.drawable.ic_track_points));
        trackTabLayout.addTab(trackTabLayout.newTab().setIcon(R.drawable.ic_track_map));

        final ViewPager trackViewPager = findViewById(R.id.trackViewPager);
        final TrackTabsPagerAdapter trackTabsPagerAdapter = new TrackTabsPagerAdapter(
                getSupportFragmentManager(), trackTabLayout.getTabCount());

        trackViewPager.setAdapter(trackTabsPagerAdapter);

        trackViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(trackTabLayout));

        trackTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                trackViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public String getTrackUuid() {
        return mTrackUuid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.track_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.track_option_share:
                // TODO: capture map and open share dialog
                return true;
            case R.id.track_option_delete:
                // TODO: check if track is active
                mLocationRepository.deleteTrackLocations(mTrackUuid);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
