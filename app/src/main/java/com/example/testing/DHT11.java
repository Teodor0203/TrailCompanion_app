package com.example.testing;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class DHT11 extends Sensor{

    private final String TAG = "DHT11";

    public DHT11(String sensorType, boolean isActive)
    {
        super(sensorType, isActive);
    }

    @Override
    public void readData(String jsonData, TextView textView)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(jsonData);

            if (jsonObject.getString("sensorType").equals(getSensorType()))
            {
                DecimalFormat formatDouble = new DecimalFormat("#.00");

                JSONObject dataFromDHT11 = jsonObject.getJSONObject("dataDHT11");
                double humidity = dataFromDHT11.getDouble("humidity");
                double temperature = dataFromDHT11.getDouble("temperature");
                double feelTemperature = dataFromDHT11.getDouble("feelTemperature");
                String temperatureFormated = formatDouble.format(feelTemperature);

                Log.d(TAG, "readData: " + temperature + feelTemperature);
                textView.setText("Temperature: " + temperatureFormated  + " GRADEEEE" + "\n" + humidity );
            }
        }
        catch (JSONException e)
        {
            Log.d(TAG, "Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
            Log.d(TAG, "Invalid JSON format!") ;
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
}
