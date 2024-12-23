package com.example.testing;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class NEO6M extends Sensor{

    private final String TAG = "NEO6M";

    private GpsData gpsData;
    private GpsWaypoint waypoint;

    public NEO6M(String sensorType, boolean isActive){
        super(sensorType, isActive);
    }

    @Override
    public void readData(String jsonData, TextView textView) {

    }

    @Override
    public void readData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            if (jsonObject.getString("sensorType").equals(getSensorType())) {
                DecimalFormat df = new DecimalFormat("0.00");


                JSONObject dataFromNEO6M = jsonObject.getJSONObject("dataNEO6M");
                double latitude = (dataFromNEO6M.getDouble("latitude"));
                double longitude = (dataFromNEO6M.getDouble("longitude"));
                double speed =  dataFromNEO6M.getDouble("speed");
                double speedTruncated = Math.floor(speed * 100) / 100;
                long timeStamp = jsonObject.getLong("timeStamp");

                waypoint = new GpsWaypoint();
                gpsData = new GpsData();
                gpsData.setSpeedValue(speedTruncated);
                waypoint.setLatitude(latitude);
                waypoint.setLongitude(longitude);
                waypoint.setTimeStamp(timeStamp);

                Log.d(TAG, "readData: Waypoint " + getWaypoint());

            } else
                Log.d(TAG, "readData: Couldn't read data!!");
        } catch (org.json.JSONException e) {

            Log.d(TAG, "readData: Error parsing JSON " + e.getMessage());
            e.printStackTrace();
            Log.d(TAG, "readData: Invalid JSON format!");
        }
    }

    public GpsData getGpsData() { return gpsData; }

    public GpsWaypoint getWaypoint() {
        return waypoint;
    }
}
