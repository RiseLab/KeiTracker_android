package ru.riselab.keitracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import ru.riselab.keitracker.adapters.TrackTabsPagerAdapter;
import ru.riselab.keitracker.db.model.TrackModel;
import ru.riselab.keitracker.db.repository.TrackRepository;
import ru.riselab.keitracker.db.viewmodel.TrackViewModel;

public class TrackActivity extends AppCompatActivity {

    private TrackMapTabFragment mTrackMapTabFragment;

    private Integer mTrackId;
    private TrackModel mTrackModel;
    private TrackRepository mTrackRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        mTrackRepository = new TrackRepository(getApplication());

        Intent intent = getIntent();
        mTrackId = intent.getIntExtra(MainActivity.EXTRA_TRACK_ID, 0);

        TrackViewModel trackViewModel = new ViewModelProvider(this).get(TrackViewModel.class);
        trackViewModel.getTrack(mTrackId).observe(this, trackModel -> {
            mTrackModel = trackModel;
            if (trackModel != null) {
                setTitle(trackModel.getName());
            }
        });

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        switch (item.getItemId()) {
            case R.id.track_option_share:
                mTrackMapTabFragment.getSnapshot();
                return true;
            case R.id.track_option_edit:
                View dialogEditTrackView = inflater.inflate(R.layout.dialog_edit_track, null);
                EditText trackNameView = dialogEditTrackView.findViewById(R.id.trackName);
                trackNameView.setText(mTrackModel.getName());
                builder.setView(dialogEditTrackView)
                        .setTitle(getString((R.string.track_dialog_edit)))
                        .setPositiveButton(R.string.ok, (dialog, which) -> {
                            String trackNameValue = trackNameView.getText().toString();

                            if (trackNameValue.length() > 0) {
                                mTrackModel.setName(trackNameValue);
                                mTrackRepository.update(mTrackModel);
                            }

                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
