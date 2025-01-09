package com.example.testing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TrailManager
{
    private static TrailManager INSTANCE;
    private List<GpsWaypoint> waypoints;
    private List<GpsWaypoint> jumpWaypoints;
    private List<PressureData> pressureValues;
    private double distance;
    private double topSpeed, averageSpeed;
    private static final String TAG = "TrailManager";

    public TrailManager()
    {
        this.waypoints = new ArrayList<>();
        this.jumpWaypoints = new ArrayList<>();
        this.pressureValues = new ArrayList<>();
        Log.d(TAG, "TrailManager: CONSTRUCTOR");
    }

    public static TrailManager getInstance()
    {
        if (INSTANCE == null) {
            INSTANCE = new TrailManager();

        }
        return INSTANCE;
    }
    public void addWaypoint(GpsWaypoint waypoint)
    {
        waypoints.add(waypoint);
    }

    public void addJumpPoint(GpsWaypoint jumpWaypoint) { jumpWaypoints.add(jumpWaypoint);}

    public void addPressureValue(PressureData pressureData)
    {
        pressureValues.add(pressureData);
    }

    public void addDistance(double distance) { this.distance = distance; }

    public void addSpeed(double topSpeed, double averageSpeed)
    {
        this.topSpeed = topSpeed;
        this.averageSpeed = averageSpeed;
    }

    public void saveTrailToFile(Context context, String fileName)
    {
        try
        {
            JSONArray segments = new JSONArray();

            for(int i = 1; i < waypoints.size(); i++)
            {
                GpsWaypoint start = waypoints.get(i-1);
                GpsWaypoint end = waypoints.get(i);
                

                long totalPressure= 0;
                int count = 0;

                for(PressureData pressure : pressureValues)
                {
                    if(pressure.getTimeStamp() >= start.getTimeStamp() && pressure.getTimeStamp() <= end.getTimeStamp())
                    {
                        totalPressure += pressure.getPressureValue();
                        count++;
                    }
                }

                long averagePressure = (count > 0) ? totalPressure/count : 0;

                JSONObject segment = new JSONObject();
                segment.put("start", new JSONArray().put(start.getLatitude()).put(start.getLongitude()));
                segment.put("end", new JSONArray().put(end.getLatitude()).put(end.getLongitude()));
                segment.put("distance", distance);
                segment.put("pressure", averagePressure);
                segment.put("topSpeed", topSpeed);
                segment.put("averageSpeed", averageSpeed);

                segments.put(segment);
            }

            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(segments.toString().getBytes());
            fos.close();

            FileOutputStream fos1 = context.openFileOutput("trails.txt", Context.MODE_APPEND);
            fos1.write(("\n"+fileName).getBytes());
            fos1.close();

            Log.d(TAG, "Trail saved successfully to " + fileName);

            int index = loadIndexFromFile(context);
            index++;
            saveIndexToFile(context, index);
        } catch (Exception e) {
            Log.e("TrailManager", "Error saving trail: " + e.getMessage(), e);
        }
    }

    @SuppressLint("SetTextI18n")
    public void drawTrailFromFile(Context context, String fileName, GoogleMap mMap, TextView topSpeed, TextView distance, TextView averageSpeed)
    {
        Log.d("MAMA", "drawTrailFromFile: READING");
        try
        {
            FileInputStream fis = context.openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null)
            {
                jsonBuilder.append(line);
            }

            fis.close();

            JSONArray segments = new JSONArray(jsonBuilder.toString());

            for(int i = 0; i < segments.length(); i++)
            {
                JSONObject segment = segments.getJSONObject(i);
                JSONArray start = segment.getJSONArray("start");
                JSONArray end = segment.getJSONArray("end");
                long pressure = segment.getLong("pressure");
                double fileTopSpeed = segment.getDouble("topSpeed");
                double fileAverageSpeed = segment.getDouble("averageSpeed");
                double fileDistance = segment.getDouble("distance");

                LatLng startLatLng = new LatLng(start.getDouble(0), start.getDouble(1));
                LatLng endLatLng = new LatLng(end.getDouble(0), end.getDouble(1));

                Log.d(TAG, "drawTrailFromFile: START" + start.getDouble(0));
                Log.d(TAG, "drawTrailFromFile: END " + end.getDouble(0) );

                PressureData pressureData = new PressureData();
                pressureData.setPressureValue(pressure);
                int segmentColor = pressureData.getSegmentColor();

                if(i == 0)
                {
                    mMap.addMarker(new MarkerOptions().position(startLatLng).title("Start"));
                }

                if(i == segments.length()-1)
                {
                    mMap.addMarker(new MarkerOptions().position(endLatLng).title("Finish"));
                }

                mMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .add(startLatLng, endLatLng)
                        .color(segmentColor)
                        .width(5));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 20));

                if(topSpeed != null && averageSpeed != null && distance != null)
                {
                    topSpeed.setText(" " + fileTopSpeed + " km/h.");
                    averageSpeed.setText(" " + fileAverageSpeed + " km/h.");
                    distance.setText(" " + fileDistance + " km.");
                }
                else
                {
                    topSpeed.setText("NaN");
                    averageSpeed.setText("NaN");
                    distance.setText("NaN");
                }
            }

            Log.d(TAG, "Trail reconstructed successfully from " + fileName);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error reconstructing trail: " + e.getMessage(), e);
        }
    }

    public int loadIndexFromFile(Context context)
    {
        int lastIndex = 1;

        try
        {
            FileInputStream fis = context.openFileInput("index.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            if(line != null)
            {
                lastIndex = Integer.parseInt(line);
                Log.d(TAG, "loadIndexFromFile: last index" + lastIndex);
            }
            fis.close();
        }
        catch (Exception e)
        {
            Log.e(TAG,"Error loading index: " + e.getMessage(), e);
        }

        return lastIndex;
    }

    private void saveIndexToFile(Context context, int index)
    {
        try
        {
            FileOutputStream fos = context.openFileOutput("index.txt", Context.MODE_PRIVATE);
            fos.write(Integer.toString(index).getBytes());
            fos.close();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error saving index: " + e.getMessage(), e);
        }
    }

}
