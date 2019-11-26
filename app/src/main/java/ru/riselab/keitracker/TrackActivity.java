package ru.riselab.keitracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import ru.riselab.keitracker.adapters.TrackTabsPagerAdapter;

public class TrackActivity extends AppCompatActivity {

    private String trackUuid = "";

    public String getTrackUuid() {
        return trackUuid;
    }

    public void setTrackUuid(String trackUuid) {
        this.trackUuid = trackUuid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Intent intent = getIntent();
        setTrackUuid(intent.getStringExtra(MainActivity.EXTRA_TRACK_UUID));

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
}
