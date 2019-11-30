package ru.riselab.keitracker;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.riselab.keitracker.db.model.LocationModel;
import ru.riselab.keitracker.db.viewmodel.LocationViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrackMapTabFragment extends Fragment implements OnMapReadyCallback, GoogleMap.SnapshotReadyCallback {

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

        // TODO: create repository for map if possible
        locationViewModel.getTrackLocations(trackUuid).observe(
                this.getViewLifecycleOwner(), points -> {
                    if (points.size() != 0 && mMap != null) {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        PolylineOptions polylineOptions = new PolylineOptions();
                        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

                        mMap.clear();

                        for (LocationModel point : points) {
                            LatLng latLng = new LatLng(
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
                            mMap.addCircle(circleOptions);

                            builder.include(latLng);

                            polylineOptions.add(latLng)
                                    .color(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null))
                                    .width(10)
                                    .zIndex(10);
                        }

                        mMap.addPolyline(polylineOptions);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    }
                }
        );

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;
    }

    public void getSnapshot() {
        if (mMap != null) {
            mMap.snapshot(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        // TODO: refactor
        File imagesFolder = new File(getActivity().getCacheDir(), "images");
        Uri uri = null;

        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
        } catch (IOException e) {
            // TODO: process exception
        }

        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sended_from));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
        }
    }
}
