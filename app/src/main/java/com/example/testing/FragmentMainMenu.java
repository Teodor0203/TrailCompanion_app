package com.example.testing;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMainMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMainMenu extends Fragment {
    private NEO6M gpsModule;
    private static Trail trail;
    private DHT11 dht11Sensor;
    private final String TAG = "FragmentMainMenu";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam2, mParam1;

    public static void updateTrail(Trail newTrail) {
        trail = newTrail;
    }

    public FragmentMainMenu() {

    }

    private static FragmentMainMenu INSTANCE = null;

    public static FragmentMainMenu newInstance(String param1, String param2) {
        FragmentMainMenu fragment = new FragmentMainMenu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INSTANCE = this;

        gpsModule = new NEO6M("NEO6M", true);
        dht11Sensor = new DHT11("DHT11", true);
        trail = new Trail(gpsModule);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static FragmentMainMenu getInstance()
    {
        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button goButton = view.findViewById(R.id.button3);

        goButton.setOnClickListener(view1 -> {

            if (ConnectionManager.getInstance().isConnected())
            {
                Log.d("ConnectionManager", "onViewCreated: S-a conectat");
                ConnectionManager.getInstance().readData(data -> {
                    if(data != null && !data.isEmpty())
                    {
                        String[] messages = data.split("\n");
                        for(String message : messages)
                        {
                            try
                            {
                                JSONObject jsonData = new JSONObject(message);
                                String sensorType = jsonData.getString("sensorType");

                                if(dht11Sensor.getSensorType().equals(sensorType))
                                {
                                    dht11Sensor.readData(jsonData.toString());
                                }
                                else if(gpsModule.getSensorType().equals(sensorType))
                                {
                                    gpsModule.readData(jsonData.toString());

                                    trail.addWaypoint();

                                    FragmentMainMenu.updateTrail(trail);

                                    Log.d("MapsFragment", "onViewCreated: " + trail.getWaypoints().toString());
                                }
                            }
                            catch (JSONException e)
                            {
                                Log.d("ConnectionManager", "Error parsing JSON message: " + message);
                            }
                        }
                    }
                    else {
                        Log.d("MainActivity", "Data is null or empty");
                    }
                });
            }
            else
            {
                Log.d("Connectionmanager", "onViewCreated: Nu este");
            }

            getParentFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_in
            ).replace(R.id.main_menu_fragment, new FragmentMaps()).commit();
        });
    }

    public static Trail getTrail()
    {
        return trail;
    }
}