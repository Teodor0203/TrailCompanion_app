package com.example.testing;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

public class MPU6050 extends Sensor{

    private JumpData jumpData;
    private final String TAG = "Accelerometer";
    private long airTime = 0;
    private boolean jumpActive = false;
    private float jumpDuration = 0.0f;

    public MPU6050(String sensorType, boolean isActive){
        super(sensorType, isActive);
    }

    @Override
    public void readData(String jsonData, TextView textView) {

    }

    @Override
    public void readData(String jsonData) {

        try {
            JSONObject jsonObject = new JSONObject(jsonData);


            if (jsonObject.getString( "sensorType").equals(getSensorType())) {
                JSONObject dataFromMPU6050 = jsonObject.getJSONObject("dataMPU6050");
                int jumpDetected = (dataFromMPU6050.getInt("jumpDetected"));
                long timeStamp = jsonObject.getLong("timeStamp");

                jumpData = new JumpData();
                jumpData.setJumpDetected(jumpDetected);

                Log.d(TAG, "readData: JUMP DETECTED: " + jumpDetected);
                if (!jumpActive && jumpDetected == 1) {
                    jumpActive = true;

                    airTime = timeStamp;

                    Log.d(TAG, "Jump at " + airTime);;
                }
                if (jumpActive && jumpDetected == -1) {

                    jumpDuration = (timeStamp - airTime) / 1000f;

                    Log.d(TAG, "time stamp " + timeStamp);

                    jumpData.setAirTime(jumpDuration);

                    Log.d(TAG, "You jumped!!! Total air time " + jumpDuration+ "s.");

                    jumpActive = false;
                    airTime = 0;
                }
                Log.d(TAG, "readData: Waiting for jump");;

            } else
                Log.d(TAG, getSensorType() + "-> couldn't read data"); ;

        } catch (org.json.JSONException e) {

            System.out.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
            Log.d(TAG, "Invalid JSON format!"); ;
        }
    }

    public JumpData getAccelerometerData() {
        return jumpData;
    }
}
