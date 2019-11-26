package ru.riselab.keitracker;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.riselab.keitracker.adapters.PointListAdapter;
import ru.riselab.keitracker.db.model.LocationModel;
import ru.riselab.keitracker.db.viewmodel.LocationViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrackPointsTabFragment extends Fragment {


    public TrackPointsTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_track_points_tab, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.trackLocationList);
        final PointListAdapter adapter = new PointListAdapter(this.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        TrackActivity trackActivity = (TrackActivity) getActivity();
        String trackUuid = (trackActivity != null) ? trackActivity.getTrackUuid() : "";

        locationViewModel.getTrackLocations(trackUuid).observe(
                this.getViewLifecycleOwner(), new Observer<List<LocationModel>>() {
                    @Override
                    public void onChanged(@Nullable List<LocationModel> points) {
                        adapter.setPoints(points);
                    }
                }
        );

        return rootView;
    }

}
