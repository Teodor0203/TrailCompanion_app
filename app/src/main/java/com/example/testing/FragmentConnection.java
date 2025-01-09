package com.example.testing;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentConnection#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentConnection extends Fragment {

    private NEO6M gpsModule;
    private DHT11 dht11Sensor;
    private RFP602 rfp602sensor;

    private MPU6050 mpu6050sensor;
    private GpsWaypoint gpsWaypoint;
    private GpsData gpsdata;
    private PressureData pressureData;
    private JumpData jumpData;
    private final String TAG = "FragmentConnection";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentConnection() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentConnection.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentConnection newInstance(String param1, String param2) {
        FragmentConnection fragment = new FragmentConnection();
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

        gpsModule = new NEO6M("NEO6M", true);
        dht11Sensor = new DHT11("DHT11", true);
        rfp602sensor = new RFP602("RFP602", true, getContext());
        mpu6050sensor = new MPU6050("MPU6050", true);


        return inflater.inflate(R.layout.fragment_connection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView speedometer = view.findViewById(R.id.textView2);
        Button startButton = view.findViewById(R.id.button2);

        TextView deviceConected = view.findViewById(R.id.textView4);
        Button connectDevice = view.findViewById(R.id.button);

        ProgressBar loadingConnetion = view.findViewById(R.id.progressBar);
        loadingConnetion.getIndeterminateDrawable().setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
        loadingConnetion.setVisibility(View.INVISIBLE);

        SharedViewmodel sharedViewmodel = new ViewModelProvider(requireActivity()).get(SharedViewmodel.class);

        if (startButton != null) {
            startButton.setVisibility(View.INVISIBLE);
        }

        connectDevice.setOnClickListener(v -> {
            /*ConnectionManager connectionManager = new ConnectionManager(getContext(), loadingConnetion, deviceConected, startButton);
            connectionManager.enableBluetooth(getActivity());
            connectionManager.connectToESP();*/
            ConnectionManager.getInstance(getContext(), loadingConnetion, deviceConected, startButton).enableBluetooth(getActivity());
            ConnectionManager.getInstance().connectToESP(getContext(), loadingConnetion, deviceConected, startButton);
        });

        startButton.setOnClickListener(view1 -> {

            ConnectionManager.getInstance().startReading();
            if (ConnectionManager.getInstance().isConnected())
            {
                Log.d(TAG, "onViewCreated: Connected");
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
                                    Log.d(TAG, "onViewCreated: Reading DHT");
                                }
                                else if (mpu6050sensor.getSensorType().equals(sensorType)) {

                                        mpu6050sensor.readData(jsonData.toString());

                                        jumpData = mpu6050sensor.getAccelerometerData();

                                        sharedViewmodel.updateAccData(jumpData);

                                }
                                else if (rfp602sensor.getSensorType().equals(sensorType))
                                {
                                    rfp602sensor.readData(jsonData.toString());

                                    pressureData = rfp602sensor.getPressureData();

                                    sharedViewmodel.updatePressureData(pressureData);
                                }

                                else if(gpsModule.getSensorType().equals(sensorType))
                                {
                                    gpsModule.readData(jsonData.toString());

                                    gpsWaypoint = gpsModule.getWaypoint();

                                    gpsdata = gpsModule.getGpsData();

                                    sharedViewmodel.updateGpsData(gpsdata);

                                    sharedViewmodel.updateGpsWaypoint(gpsWaypoint);

                                    Log.d(TAG, "onViewCreated: " + gpsModule.getWaypoint().toString());

                                }
                            }
                            catch (JSONException e)
                            {
                                Log.d(TAG, "Error parsing JSON message: " + message);
                            }
                        }
                    }
                    else {
                        Log.d(TAG, "Data is null or empty");
                    }
                });
            }
            else
            {
                Log.d(TAG, "onViewCreated: Nu este");
            }

            getParentFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_in
            ).replace(R.id.rootContainer, new FragmentMaps()).commit();
        });
    }
}