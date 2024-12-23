package com.example.testing;

import android.graphics.Color;

public class GpsData {

    private long timeStamp;
    private double speed;

    public GpsData() {}

    public void setSpeedValue(double speed) { this.speed = speed; }

    public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

    @Override
    public String toString() {
        return speed + " km/h";
    }
}
