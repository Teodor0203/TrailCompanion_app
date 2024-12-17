package com.example.testing;

import android.graphics.Color;

public class PressureData {

    private long pressureValue, timeStamp;
    private int segmentColor;

    public PressureData() {}

    public void setPressureValue(long pressureValue) { this.pressureValue = pressureValue; }

    public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

    public long getTimeStamp() {
        return timeStamp;
    }

    public long getPressureValue() {
        return pressureValue;
    }

    public int getSegmentColor()
    {
        if (pressureValue >= 0 && pressureValue <= 500) {
            segmentColor = Color.parseColor("#ff35c025"); // Verde
        } else if (pressureValue > 500 && pressureValue <= 1000) {
            segmentColor = Color.parseColor("#ffece816"); // Galben
        } else if (pressureValue > 1000 && pressureValue <= 1500) {
            segmentColor = Color.parseColor("#ffec8716"); // Portocaliu
        } else if(pressureValue > 1800){
            segmentColor = Color.parseColor("#ffda2424");
        }

        return segmentColor;
    }

    @Override
    public String toString() {
        return "pressureValue=" + pressureValue + ", timeStamp=" + timeStamp;
    }
}
