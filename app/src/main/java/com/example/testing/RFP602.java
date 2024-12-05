package com.example.testing;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

public class RFP602 extends Sensor {

    private String segmentColor;
    private final String TAG = "RFP602";

    public RFP602(String sensorType, boolean isActive)
    {
        super(sensorType, isActive);
    }

    @Override
    public void readData(String jsonData, TextView textView) {

        try
        {
            JSONObject jsonObject = new JSONObject(jsonData);
            if (jsonObject.getString("sensorType").equals(getSensorType()))
            {
                JSONObject data = jsonObject.getJSONObject("dataRFP602");
                double analogPressureValue = data.getDouble("analogPressureValue");
                String brakingSegmentcolor;

                if (analogPressureValue < 10)
                {
                    brakingSegmentcolor = "-> No pressure";
                }
                else if (analogPressureValue < 200)
                {
                    brakingSegmentcolor = "-> Green";
                }
                else if (analogPressureValue < 350)
                {
                    brakingSegmentcolor = "-> Yellow";
                }
                else if (analogPressureValue < 450)
                {
                    brakingSegmentcolor = "-> Orange";
                }
                else
                {
                    brakingSegmentcolor = "-> Red";
                }

                this.segmentColor = brakingSegmentcolor;
            }
            else
                Log.d(TAG, "Couldn't read data");
        }
        catch (org.json.JSONException e)
        {
            Log.d(TAG, "Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
            Log.d(TAG, "readData: Invalid JSON format!");
        }
    }

    @Override
    public void readData(String jsonData) {

    }

    @Override
    public void saveDataToFile(String jsonData)
    {
        super.saveDataToFile(jsonData);
    }

    public String getSegmentColor() {
        return segmentColor;
    }
}
