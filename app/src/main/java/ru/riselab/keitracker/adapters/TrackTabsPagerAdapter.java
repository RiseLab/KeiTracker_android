package ru.riselab.keitracker.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ru.riselab.keitracker.TrackMapTabFragment;
import ru.riselab.keitracker.TrackPointsTabFragment;

public class TrackTabsPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;

    private TrackPointsTabFragment mTrackPointsTabFragment;
    private TrackMapTabFragment mTrackMapTabFragment;

    public TrackTabsPagerAdapter(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mNumOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (mTrackPointsTabFragment == null) {
                mTrackPointsTabFragment = new TrackPointsTabFragment();
            }
            return mTrackPointsTabFragment;
        }
        if (mTrackMapTabFragment == null) {
            mTrackMapTabFragment = new TrackMapTabFragment();
        }
        return mTrackMapTabFragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
