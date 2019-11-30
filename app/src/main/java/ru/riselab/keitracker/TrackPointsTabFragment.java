package ru.riselab.keitracker;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.riselab.keitracker.adapters.PointListAdapter;
import ru.riselab.keitracker.db.model.PointModel;
import ru.riselab.keitracker.db.viewmodel.PointViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrackPointsTabFragment extends Fragment {

    private ProgressBar mProgressBar;

    public TrackPointsTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_track_points_tab, container, false);

        mProgressBar = rootView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = rootView.findViewById(R.id.trackLocationList);
        final PointListAdapter adapter = new PointListAdapter(this.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        PointViewModel pointViewModel = new ViewModelProvider(this).get(PointViewModel.class);

        TrackActivity trackActivity = (TrackActivity) getActivity();
        Integer trackId = (trackActivity != null) ? trackActivity.getTrackId() : null;

        pointViewModel.getTrackPoints(trackId).observe(
                this.getViewLifecycleOwner(), new Observer<List<PointModel>>() {
                    @Override
                    public void onChanged(@Nullable List<PointModel> points) {
                        adapter.setPoints(points);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
        );

        return rootView;
    }

}
