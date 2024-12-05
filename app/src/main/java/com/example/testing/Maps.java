package com.example.testing;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Maps#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Maps extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Trail trail;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Maps() {
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
    public static Maps newInstance(String param1, String param2) {
        Maps fragment = new Maps();
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

        FragmentManager fragmentManager = getFragmentManager(); //I want to get the trail from MainMenu.
        MainMenu mainMenuFragment = (MainMenu) fragmentManager.findFragmentById(R.id.main_menu_fragment);
        trail = mainMenuFragment.getTrail();
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
            Log.e("MapFragment", "MapFragment not found!");
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("ConnectionManager", "onMapReady: MapReady");
        mMap = googleMap;

        List<GpsWaypoint> waypoints = new ArrayList<>();
        for (GpsWaypoint gpsWaypoint : trail.getWaypoints())
        {
            waypoints.add(gpsWaypoint);
        }

        for (int i = 1; i < waypoints.size(); i++)
        {
            LatLng marker = new LatLng(waypoints.get(i).getLatitude(), waypoints.get(i).getLongitude());
            LatLng previousMarker = new LatLng(waypoints.get(i - 1).getLatitude(), waypoints.get(i - 1).getLongitude());

            if (!marker.equals(previousMarker))
            {
                mMap.addMarker(new MarkerOptions().position(marker).title("Marker " + (i + 1)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 12));
            }
            else
            {
                Log.e("ConnectionManager", "Duplicate marker at index " + i);
            }
        }

        if (!waypoints.isEmpty())
        {
            LatLng firstMarker = new LatLng(waypoints.get(0).getLatitude(), waypoints.get(0).getLongitude());
            mMap.addMarker(new MarkerOptions().position(firstMarker).title("Marker 1"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstMarker, 12));
        }
    }
}