package com.example.testing;

import android.graphics.Color;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class FragmentMaps extends Fragment implements OnMapReadyCallback {

    private final String TAG = "FragmentMaps";
    private GoogleMap mMap;

    private final List<LatLng> markers = new ArrayList<>();
    private final List<GpsWaypoint> waypoints = new ArrayList<>();
    private final List<PressureData> pressureValues = new ArrayList<>();
    private Button stopButton;

    private static FragmentMaps INSTANCE = null;

    public FragmentMaps() {
        // Required empty public constructor
    }

    public static FragmentMaps newInstance(String param1, String param2) {
        FragmentMaps fragment = new FragmentMaps();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INSTANCE = this;
    }

    public static FragmentMaps getInstance() {
        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        stopButton = view.findViewById(R.id.button4);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "MapFragment not found!");
        }
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        SharedViewmodel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewmodel.class);
        Log.d(TAG, "onMapReady: Map is ready");

        mMap = googleMap;

        sharedViewModel.getDataLiveData().observe(getViewLifecycleOwner(), data -> {

            GpsWaypoint waypoint = data.getGpsWaypoint();
            PressureData pressureData = data.getPressureData();

            if (waypoint != null) {
                Log.d(TAG, "Received waypoint: " + waypoint.getLatitude() + ", " + waypoint.getLongitude());

                GpsWaypoint newWaypoint = new GpsWaypoint();
                newWaypoint.setTimeStamp(waypoint.getTimeStamp());
                newWaypoint.setLatitude(waypoint.getLatitude());
                newWaypoint.setLongitude(waypoint.getLongitude());

                if (waypoints.isEmpty() || waypoints.get(waypoints.size() - 1).getTimeStamp() != waypoint.getTimeStamp()) {
                    waypoints.add(newWaypoint);
                }
            }

            if (pressureData != null) {
                Log.d(TAG, "Received pressure data: " + pressureData.getPressureValue());

                PressureData newPressureData = new PressureData();
                newPressureData.setPressureValue(pressureData.getPressureValue());
                newPressureData.setTimeStamp(pressureData.getTimeStamp());

                pressureValues.add(newPressureData);
            }

            if (!waypoints.isEmpty()) {

                for (int i = 0; i < waypoints.size(); i++) {
                    if (i > 0) {
                        LatLng start = new LatLng(waypoints.get(i - 1).getLatitude(), waypoints.get(i - 1).getLongitude());
                        LatLng end = new LatLng(waypoints.get(i).getLatitude(), waypoints.get(i).getLongitude());

                        long startTimestamp = waypoints.get(i - 1).getTimeStamp();
                        long endTimestamp = waypoints.get(i).getTimeStamp();

                        long pressureValueBetweenTwoPoints = 0;
                        int count = 0;

                        for (PressureData pressure : pressureValues) {
                            if (pressure.getTimeStamp() >= startTimestamp && pressure.getTimeStamp() <= endTimestamp) {
                                pressureValueBetweenTwoPoints += pressure.getPressureValue();
                                count++;
                            }
                        }

                        long averagePressureBetweenTwoPoints = pressureValueBetweenTwoPoints / count;

                        if (pressureData != null) {
                            pressureData.setPressureValue(averagePressureBetweenTwoPoints);
                        }

                        int segmentColor = (count > 0) ? pressureData.getSegmentColor() : Color.GRAY;
                        Log.d(TAG, "onMapReady: Segment color " + segmentColor);

                        if (mMap != null) {
                            mMap.addPolyline(new PolylineOptions()
                                    .clickable(true)
                                    .add(start, end)
                                    .color(segmentColor)
                                    .width(5));
                        }
                        Log.d(TAG, "Pressure values remaining: " + pressureValues.size());

                        pressureValues.removeIf(pressure -> pressure.getTimeStamp() >= startTimestamp && pressure.getTimeStamp() <= endTimestamp);
                    }

                    if (i < waypoints.size() - 1)
                    {
                        waypoints.remove(i - 1);
                        Log.d(TAG, "Waypoint removed: " + (i - 1));
                        i--;
                    }

                    Log.d(TAG, "Remaining waypoints: " + waypoints.size());
                    Log.d(TAG, "Remaining pressures: " + pressureValues.size());
                }
            }
        });

        if(!waypoints.isEmpty())
        {
            LatLng firstMarker = new LatLng(waypoints.get(0).getLatitude(), waypoints.get(0).getLongitude());
                mMap.addMarker(new MarkerOptions().position(firstMarker).title("Start"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstMarker, 20));
                markers.add(firstMarker);
            Log.d(TAG, "onMapReady: IT ENTERED FIRST MARKER IF");
        }

        stopButton.setOnClickListener(view -> {

            LatLng finishMarker = new LatLng(waypoints.get(waypoints.size() - 1).getLatitude(), waypoints.get(waypoints.size() - 1).getLongitude());
            Log.d(TAG, "onMapReady: Button pressed");
            if (!markers.contains(finishMarker) && ConnectionManager.getInstance() != null) {
                mMap.addMarker(new MarkerOptions().position(finishMarker).title("Finish"));
                markers.add(finishMarker);
                ConnectionManager.getInstance().stopReading();
            }
        });
    }
}
