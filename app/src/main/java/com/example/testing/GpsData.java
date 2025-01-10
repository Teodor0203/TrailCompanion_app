package com.example.testing;

import android.graphics.Color;

public class GpsData {

    private double speed;

    public GpsData() {}

    public void setSpeedValue(double speed) { this.speed = speed; }

    public double getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return speed + " km/h";
    }
}
