package com.example.testing;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

public class NEO6M extends Sensor{

    private final String TAG = "NEO6M";
    private GpsWaypoint waypoint = new GpsWaypoint();

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
                JSONObject dataFromNEO6M = jsonObject.getJSONObject("dataNEO6M");
                double latitude = (dataFromNEO6M.getDouble("latitude"));
                double longitude = (dataFromNEO6M.getDouble("longitude"));

                waypoint.setLatitude(latitude);
                waypoint.setLongitude(longitude);

                Log.d(TAG, "readData: Waypoint " + getWaypoint());

            } else
                Log.d(TAG, "readData: Couldn't read data!!");
        } catch (org.json.JSONException e) {

            Log.d(TAG, "readData: Error parsing JSON " + e.getMessage());
            e.printStackTrace();
            Log.d(TAG, "readData: Invalid JSON format!");
        }
    }

    public GpsWaypoint getWaypoint() {
        return waypoint;
    }

}
