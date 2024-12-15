package com.example.testing;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMaps#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMaps extends Fragment implements OnMapReadyCallback {

    private final String TAG = "FragmentMaps";
    private GoogleMap mMap;

    private List<LatLng> markers = new ArrayList<>();
    private  List<GpsWaypoint> waypoints = new ArrayList<>();
    private List<PressureData> pressureValues = new ArrayList<>();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static FragmentMaps INSTANCE = null;

    public FragmentMaps() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Maps.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMaps newInstance(String param1, String param2) {
        FragmentMaps fragment = new FragmentMaps();
        Bundle args = new Bundle();

        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        INSTANCE = this;
    }

    public static FragmentMaps getInstance() {
        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "MapFragment not found!");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        SharedViewmodel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewmodel.class);
        Log.d(TAG, "onMapReady: MapReady");

        mMap = googleMap;

        sharedViewModel.getDataLiveData().observe(getViewLifecycleOwner(), data -> {

            GpsWaypoint waypoint = data.getGpsWaypoint();
            PressureData pressureData = data.getPressureData();

            if(waypoint != null)
            {
                GpsWaypoint newWaypoint = new GpsWaypoint();
                newWaypoint.setTimeStamp(waypoint.getTimeStamp());
                newWaypoint.setLatitude(waypoint.getLatitude());
                newWaypoint.setLongitude(waypoint.getLongitude());

                if(waypoints.isEmpty() || waypoints.get(waypoints.size()-1).getTimeStamp() != waypoint.getTimeStamp())
                    waypoints.add(newWaypoint);
            }

            if(pressureData != null)
            {
                PressureData newPressureData = new PressureData();
                newPressureData.setPressureValue(pressureData.getPressureValue());
                newPressureData.setTimeStamp(pressureData.getTimeStamp());

                pressureValues.add(newPressureData);
            }

            if (waypoint != null) {
                if (mMap != null) {
                    if (waypoints.isEmpty() || waypoints.get(waypoints.size() - 1).getTimeStamp() != waypoint.getTimeStamp()) {
                        LatLng firstMarker = new LatLng(waypoints.get(0).getLatitude(), waypoints.get(0).getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstMarker, 17));
                        //Log.d(TAG, "onMapReady: Primul punct: " + firstMarker);
                    }

                    for (int i = 0; i < waypoints.size(); i++) {

                        LatLng marker = new LatLng(waypoints.get(i).getLatitude(), waypoints.get(i).getLongitude());
                        markers.add(marker);

                        if (i > 0) {
                            LatLng previousMarker = new LatLng(waypoints.get(i - 1).getLatitude(), waypoints.get(i - 1).getLongitude());
                            LatLng start = new LatLng(waypoints.get(i - 1).getLatitude(), waypoints.get(i - 1).getLongitude());
                            LatLng end = new LatLng(waypoints.get(i).getLatitude(), waypoints.get(i).getLongitude());
                            //Log.d(TAG, markers.toString());

                            if (waypoints.get(i).getTimeStamp() >= pressureData.getTimeStamp() && pressureData.getTimeStamp() >= waypoints.get(i - 1).getTimeStamp()) {
                                Log.d(TAG, "onMapReady: primul waypoint " + waypoints.get(i - 1).getTimeStamp());
                                Log.d(TAG, "onMapReady: al doilea waypoint " + waypoints.get(i).getTimeStamp());

                                long startTimestamp = waypoints.get(i - 1).getTimeStamp();
                                long endTimestamp = waypoints.get(i).getTimeStamp();

                                long sum = 0;
                                int count = 0;

                                for (PressureData pressure : pressureValues) { // Iterează prin lista de obiecte PressureData
                                    if (pressure.getTimeStamp() >= startTimestamp && pressure.getTimeStamp() <= endTimestamp) {
                                        Log.d(TAG, "Using pressure value: " + pressure.getPressureValue() + " with timestamp: " + pressure.getTimeStamp());
                                        sum += pressure.getPressureValue();
                                        count++;
                                    }
                                }

                                long averagePressure = (count >0) ? sum/count : 0;
                                PressureData averagePressureData = new PressureData();
                                averagePressureData.setPressureValue(averagePressure);

                                // Determină culoarea segmentului
                                int segmentColor = (count > 0) ? averagePressureData.getSegmentColor() : Color.GRAY;
                                Log.d(TAG, "Average pressure: " + averagePressure);
                                Log.d(TAG, "Segment color: " + segmentColor);

                                // Desenează linia pe hartă
                                mMap.addPolyline(new PolylineOptions()
                                        .clickable(true)
                                        .add(start, end)
                                        .color(segmentColor)
                                        .width(5));
                            }

                            //Log.d(TAG, "onMapReady: TimeStamp: " + waypoints.get(i).getTimeStamp());

                            if (!marker.equals(previousMarker))
                            {
                                mMap.addMarker(new MarkerOptions().position(marker).title("Marker " + (i + 1)));
                            }
                            else
                            {
                                //Log.e(TAG, "Duplicate marker at index " + i);
                            }
                        } else
                        {
                            LatLng defaultMarker = new LatLng(waypoints.get(0).getLatitude(), waypoints.get(0).getLongitude());
                            mMap.addMarker(new MarkerOptions().position(defaultMarker).title("Marker 1"));
                        }
                    }
                }
            }
        });
    }
}