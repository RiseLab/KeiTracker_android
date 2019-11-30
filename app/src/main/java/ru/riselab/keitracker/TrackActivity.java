package ru.riselab.keitracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import ru.riselab.keitracker.adapters.TrackTabsPagerAdapter;
import ru.riselab.keitracker.db.repository.PointRepository;
import ru.riselab.keitracker.db.repository.TrackRepository;

public class TrackActivity extends AppCompatActivity {

    private TrackMapTabFragment mTrackMapTabFragment;

    private Integer mTrackId;
    private TrackRepository mTrackRepository;
    private PointRepository mPointRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        mTrackRepository = new TrackRepository(getApplication());
        mPointRepository = new PointRepository(getApplication());

        Intent intent = getIntent();
        mTrackId = intent.getIntExtra(MainActivity.EXTRA_TRACK_ID, 0);

        TabLayout trackTabLayout = findViewById(R.id.trackTabLayout);

        trackTabLayout.addTab(trackTabLayout.newTab().setIcon(R.drawable.ic_track_points));
        trackTabLayout.addTab(trackTabLayout.newTab().setIcon(R.drawable.ic_track_map));

        final ViewPager trackViewPager = findViewById(R.id.trackViewPager);
        final TrackTabsPagerAdapter trackTabsPagerAdapter = new TrackTabsPagerAdapter(
                getSupportFragmentManager(), trackTabLayout.getTabCount());

        mTrackMapTabFragment = (TrackMapTabFragment) trackTabsPagerAdapter.getItem(1);

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

    public Integer getTrackId() {
        return mTrackId;
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
                mTrackMapTabFragment.getSnapshot();
                return true;
            case R.id.track_option_delete:
                // TODO: check if track is active
                mTrackRepository.delete(mTrackId);
                mPointRepository.deleteTrackPoints(mTrackId);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
