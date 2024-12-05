package com.example.testing;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMaps#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMaps extends Fragment implements OnMapReadyCallback {

    private final String TAG = "MapsFragment";
    private GoogleMap mMap;
    private Trail trail;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: MapReady");
        mMap = googleMap;

        trail = FragmentMainMenu.getInstance().getTrail();

        List<GpsWaypoint> waypoints = new ArrayList<>();
        for (GpsWaypoint gpsWaypoint : trail.getWaypoints())
        {
            waypoints.add(gpsWaypoint);
        }

        if (!waypoints.isEmpty())
        {
            LatLng firstMarker = new LatLng(waypoints.get(0).getLatitude(), waypoints.get(0).getLongitude());
            mMap.addMarker(new MarkerOptions().position(firstMarker).title("Marker 1"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstMarker, 12));
        }

        List<LatLng> polylinePoints = new ArrayList<>();

        for (int i = 1; i < waypoints.size(); i++)
        {
            LatLng marker = new LatLng(waypoints.get(i).getLatitude(), waypoints.get(i).getLongitude());
            LatLng previousMarker = new LatLng(waypoints.get(i - 1).getLatitude(), waypoints.get(i - 1).getLongitude());

            if (!marker.equals(previousMarker))
            {
                mMap.addMarker(new MarkerOptions().position(marker).title("Marker " + (i + 1)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 12));
                polylinePoints.add(marker);
            }
            else
            {
                Log.e(TAG, "Duplicate marker at index " + i);
            }
        }

        if(!polylinePoints.isEmpty())
        {
            mMap.addPolyline(new PolylineOptions()
                    .addAll(polylinePoints)
                    .color(Color.BLUE)
                    .width(5));
        }
    }
}