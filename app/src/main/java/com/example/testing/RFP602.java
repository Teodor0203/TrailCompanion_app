package com.example.testing;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

public class RFP602 extends Sensor {

    private final String TAG = "RFP602";
    private PressureData pressureData;

    public RFP602(String sensorType, boolean isActive) {
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
                JSONObject data = jsonObject.getJSONObject("dataRFP602");
                long analogPressureValue = data.getLong("analogPressureValue");
                long timeStamp = jsonObject.getLong("timeStamp");

                pressureData = new PressureData();
                pressureData.setTimeStamp(timeStamp);
                pressureData.setPressureValue(analogPressureValue);


                /*File file = new File(context.getFilesDir(), "rfpValues.csv");

                try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true))))
                {
                    writer.write(timeStamp + ", " + analogPressureValue);
                    writer.newLine();
                    writer.flush();
                    Log.d(TAG, "readData: Date scrise! " + file.getAbsolutePath());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }*/

                Log.d("FragmentMainMenu", "readData: " + timeStamp + ": " + analogPressureValue);
            } else
                Log.d(TAG, "Couldn't read data");
        } catch (org.json.JSONException e) {
            Log.d(TAG, "Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
            Log.d(TAG, "readData: Invalid JSON format!");
        }
    }

    public PressureData getPressureData() {
        return pressureData;
    }

    @Override
    public void saveDataToFile(String jsonData) {
        super.saveDataToFile(jsonData);
    }
}
