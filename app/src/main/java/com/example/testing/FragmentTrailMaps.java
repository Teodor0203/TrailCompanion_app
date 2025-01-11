package com.example.testing;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentTrailMaps#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTrailMaps extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String trailName;
    private ProgressBar progressBar;
    TrailManager trailManager = new TrailManager();

    private GoogleMap mMap;

    public FragmentTrailMaps(){}

    public FragmentTrailMaps(String trailName, ProgressBar progressBar) {
        this.trailName = trailName;
        this.progressBar = progressBar;
        Log.d("FragmentTRAILS", "FragmentTrailMaps: TRAIL NAME" + trailName);
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTrailMaps.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTrailMaps newInstance(String param1, String param2) {
        FragmentTrailMaps fragment = new FragmentTrailMaps();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trail_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.trail_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("TrailManager", "MapFragment not found!");
        }

        return view;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        if(progressBar != null)
        {
            progressBar.setVisibility(View.INVISIBLE);
        }

        mMap = googleMap;

        TextView distanceText = getView().findViewById(R.id.distanceText);
        TextView topSpeedText = getView().findViewById(R.id.topSpeedText);
        TextView averageSpeedText = getView().findViewById(R.id.averageSpeedText);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style_2));

        Button mapTypeButton = getView().findViewById(R.id.button5);

        mapTypeButton.setOnClickListener(view -> {
            changeMapType(mMap, mapTypeButton);
        });



        trailManager.drawTrailFromFile(getContext(), trailName, mMap, topSpeedText, distanceText, averageSpeedText);
        Log.d("TrailManager", "onMapReady: CALLED  ");
    }

    public void changeMapType(GoogleMap map, Button mapTypeButton)
    {


        if(map.getMapType() == GoogleMap.MAP_TYPE_NORMAL && mapTypeButton.getText().equals("Terrain"))
        {
            mapTypeButton.setTextColor(getResources().getColor(R.color.white, null));
            mapTypeButton.setText("Satellite");
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style_2));
        }
        else if (map.getMapType() == GoogleMap.MAP_TYPE_TERRAIN && mapTypeButton.getText().equals("Satellite"))
        {
            mapTypeButton.setTextColor(getResources().getColor(R.color.white, null));
            mapTypeButton.setText("Normal");
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        else if (map.getMapType() == GoogleMap.MAP_TYPE_SATELLITE && mapTypeButton.getText().equals("Normal"))
        {
            mapTypeButton.setTextColor(getResources().getColor(R.color.white, null));
            mapTypeButton.setText("Terrain");
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }
}