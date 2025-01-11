package com.example.testing;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FragmentMaps extends Fragment implements OnMapReadyCallback {

    private final String TAG = "FragmentMaps";
    private GoogleMap mMap;

    private final List<LatLng> markers = new ArrayList<>();
    private final List<GpsWaypoint> waypoints = new ArrayList<>();
    private final List<PressureData> pressureValues = new ArrayList<>();
    private Button stopButton;
    private double totalDistance = 0.0;
    private double topSpeed= 0.0;
    private double averageSpeed = 0.0;
    private double totalSpeed = 0.0;
    private int speedCount = 0;
    private DialogInterface.OnClickListener dialogClickListener;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
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
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        SharedViewmodel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewmodel.class);
        Log.d(TAG, "onMapReady: Map is ready");


        mMap = googleMap;

        sharedViewModel.getDataLiveData().observe(getViewLifecycleOwner(), data -> {

            GpsWaypoint waypoint = data.getGpsWaypoint();
            PressureData pressureData = data.getPressureData();
            GpsData gpsData = data.getGpsData();
            JumpData jumpData = data.getAccData();

            TextView speedometer = getView().findViewById(R.id.textView2);



            if(speedometer != null && gpsData != null)
            {
                speedometer.setText(gpsData.getSpeed() + " km/h");

                speedCount++;

                double currentSpeed = gpsData.getSpeed();
                totalSpeed += currentSpeed;

                Log.d(TAG, "CurrentSpeed " + currentSpeed);

                if(currentSpeed > topSpeed)
                {
                    topSpeed = currentSpeed;
                    Log.d(TAG, "TOP SPEED " + topSpeed);
                    TrailManager.getInstance().addSpeed(topSpeed, averageSpeed);
                }

                averageSpeed = (speedCount > 0) ? Math.floor(totalSpeed/speedCount * 100) / 100 : 0.0;
                Log.d(TAG, "!!!!!AVERAGE SPEED!!!!! " + averageSpeed);

            }

            if (waypoint != null) {
                //Log.d(TAG, "Received waypoint: " + waypoint.getLatitude() + ", " + waypoint.getLongitude());

                GpsWaypoint newWaypoint = new GpsWaypoint();
                TrailManager.getInstance().addWaypoint(newWaypoint);
                newWaypoint.setTimeStamp(waypoint.getTimeStamp());
                newWaypoint.setLatitude(waypoint.getLatitude());
                newWaypoint.setLongitude(waypoint.getLongitude());

                if (waypoints.isEmpty() || waypoints.get(waypoints.size() - 1).getTimeStamp() != waypoint.getTimeStamp()) {
                    waypoints.add(newWaypoint);

                    if (waypoints.size() == 1)
                    {
                        LatLng firstMarker = new LatLng(newWaypoint.getLatitude(), newWaypoint.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(firstMarker).title("Start"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstMarker, 20));
                        markers.add(firstMarker);
                    }


                    if(jumpData.getJumpDetected() == 1)
                    {
                        LatLng jumpStart = new LatLng(newWaypoint.getLatitude(), newWaypoint.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(jumpStart)
                                .title("Jump")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                    }
                }
            }

            if (pressureData != null) {
                PressureData newPressureData = new PressureData();
                TrailManager.getInstance().addPressureValue(newPressureData);
                newPressureData.setPressureValue(pressureData.getPressureValue());
                newPressureData.setTimeStamp(pressureData.getTimeStamp());

                pressureValues.add(newPressureData);
            }

            if (!waypoints.isEmpty()) {

                for (int i = 0; i < waypoints.size(); i++) {
                    if (i > 0) {
                        LatLng start = new LatLng(waypoints.get(i - 1).getLatitude(), waypoints.get(i - 1).getLongitude());
                        LatLng end = new LatLng(waypoints.get(i).getLatitude(), waypoints.get(i).getLongitude());

                        totalDistance += calculateDistance(waypoints.get(i-1), waypoints.get(i));



                        Log.d(TAG, "onMapReady: DISTANCE " + totalDistance);

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

                        long averagePressureBetweenTwoPoints = (count > 0) ? (pressureValueBetweenTwoPoints / count) : 0;

                        if (pressureData != null) {
                            pressureData.setPressureValue(averagePressureBetweenTwoPoints);
                        }

                        int segmentColor = pressureData.getSegmentColor();

                        if (mMap != null) {
                            mMap.addPolyline(new PolylineOptions()
                                    .clickable(true)
                                    .add(start, end)
                                    .color(segmentColor)
                                    .width(5));
                        }

                        if(mMap != null)
                        {
                            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style_2));
                        }

                        pressureValues.removeIf(pressure -> pressure.getTimeStamp() >= startTimestamp && pressure.getTimeStamp() <= endTimestamp);
                    }

                    if (i < waypoints.size() - 1 && i>0)
                    {
                        waypoints.remove(i - 1);
                        i--;
                    }
                }
            }
        });

        if(!waypoints.isEmpty())
        {
            LatLng firstMarker = new LatLng(waypoints.get(0).getLatitude(), waypoints.get(0).getLongitude());
            mMap.addMarker(new MarkerOptions().position(firstMarker).title("Start"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstMarker, 20));
            markers.add(firstMarker);
        }

        stopButton.setOnClickListener(view -> {

            if(!waypoints.isEmpty())
            {
                LatLng finishMarker = new LatLng(waypoints.get(waypoints.size() - 1).getLatitude(), waypoints.get(waypoints.size() - 1).getLongitude());
                if (!markers.contains(finishMarker) && ConnectionManager.getInstance() != null) {
                    mMap.addMarker(new MarkerOptions().position(finishMarker).title("Finish"));
                    markers.add(finishMarker);
                    ConnectionManager.getInstance().stopReading();
                }
            }
            TrailManager.getInstance().addSpeed(topSpeed, averageSpeed);
            TrailManager.getInstance().addDistance(totalDistance);

            showDialogBox();
        });
    }

    public void showDialogBox() {

        if (getActivity() != null && isAdded()) {

            dialogClickListener = new DialogInterface.OnClickListener() {
                @SuppressLint("ShowToast")
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {


                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Toast.makeText(getContext(), "Trail was saved", Toast.LENGTH_LONG).show();
                            TrailManager.getInstance().saveTrailToFile(getContext(), ("Trail " + TrailManager.getInstance().loadIndexFromFile(getContext())));
                            getParentFragmentManager().beginTransaction().setCustomAnimations(
                                    R.anim.slide_in,
                                    R.anim.fade_out,
                                    R.anim.fade_in,
                                    R.anim.slide_in
                            ).replace(R.id.rootContainer, new FragmentMainMenu()).commit();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            Toast.makeText(getContext(), "Trail was not saved", Toast.LENGTH_LONG).show();
                            getParentFragmentManager().beginTransaction().setCustomAnimations(
                                    R.anim.slide_in,
                                    R.anim.fade_out,
                                    R.anim.fade_in,
                                    R.anim.slide_in
                            ).replace(R.id.rootContainer, new FragmentMainMenu()).commit();
                            dialogInterface.dismiss();
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage("Do you want to save the ride?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        }
    }

    public double calculateDistance(GpsWaypoint waypoint1, GpsWaypoint waypoint2) {
        final int R = 6371; //Earth's radius in km

        double lat1 = Math.toRadians(waypoint1.getLatitude());
        double lon1 = Math.toRadians(waypoint1.getLongitude());
        double lat2 = Math.toRadians(waypoint2.getLatitude());
        double lon2 = Math.toRadians(waypoint2.getLongitude());

        //Haversine formula
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c; // în km

        return Math.floor(distance * 1000) / 1000;
    }
}