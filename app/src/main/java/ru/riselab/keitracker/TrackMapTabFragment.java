package ru.riselab.keitracker;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.riselab.keitracker.db.AppDatabase;
import ru.riselab.keitracker.db.dao.LocationDao;
import ru.riselab.keitracker.db.model.LocationModel;
import ru.riselab.keitracker.db.viewmodel.LocationViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrackMapTabFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    public TrackMapTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_track_map_tab, container, false);

        LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        TrackActivity trackActivity = (TrackActivity) getActivity();
        String trackUuid = (trackActivity != null) ? trackActivity.getTrackUuid() : "";

        // TODO: create repository for map and refactor
        locationViewModel.getTrackLocations(trackUuid).observe(
                this.getViewLifecycleOwner(), new Observer<List<LocationModel>>() {
                    @Override
                    public void onChanged(List<LocationModel> points) {
                        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
                        if (points.size() != 0) {
                            if (mMap != null) {
                                mMap.clear();
                            }
                            LatLng latLng = new LatLng(
                                    points.get(0).getLatitude(),
                                    points.get(0).getLongitude()
                            );
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                            PolylineOptions polylineOptions = new PolylineOptions();
                            for (LocationModel point : points) {
                                latLng = new LatLng(
                                        point.getLatitude(),
                                        point.getLongitude()
                                );
                                CircleOptions circleOptions = new CircleOptions()
                                        .center(latLng)
                                        .radius(3)
                                        .strokeWidth(10)
                                        .strokeColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null))
                                        .fillColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null))
                                        .zIndex(11);
                                polylineOptions.add(latLng)
                                        .color(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null))
                                        .width(10)
                                        .zIndex(10);
                                mMap.addCircle(circleOptions);
                            }
                            mMap.addPolyline(polylineOptions);
                        }
                    }
                }
        );

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
