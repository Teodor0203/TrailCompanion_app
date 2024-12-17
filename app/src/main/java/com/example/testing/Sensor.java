package com.example.testing;

import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Sensor {

    final private String sensorType;
    final private boolean isActive;


    public Sensor(String sensorType, boolean isActive) {
        this.sensorType = sensorType;
        this.isActive = true;
    }

    public String getSensorType() {
        return sensorType;
    }


    public abstract void readData(String jsonData, TextView textView);

    public abstract void readData(String jsonData);

    public void saveDataToFile(String jsonData) {
        if (jsonData == null)
            return;

        try {
            final String FILE_PATH = "D:/Desktop/JavaProject/src/" + getSensorType() + ".txt";

            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true));

            if (jsonData != null)
                writer.write(jsonData);

            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
