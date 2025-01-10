package com.example.testing;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

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

    private GoogleMap mMap;

    TrailManager trailManager = new TrailManager();


    public FragmentTrailMaps(String trailName) {
        this.trailName = trailName;
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
        String trailName = "";
        FragmentTrailMaps fragment = new FragmentTrailMaps(trailName);
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
        mMap = googleMap;

        TextView distanceText = getView().findViewById(R.id.distanceText);
        TextView topSpeedText = getView().findViewById(R.id.topSpeedText);
        TextView averageSpeedText = getView().findViewById(R.id.averageSpeedText);


        trailManager.drawTrailFromFile(getContext(), trailName, mMap, topSpeedText, distanceText, averageSpeedText);
        Log.d("TrailManager", "onMapReady: CALLED  ");
    }
}