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
        if (pressureValue >= 0 && pressureValue <= 10) {
            segmentColor = Color.parseColor("#ff35c025"); //Green
        } else if (pressureValue > 10 && pressureValue <= 25) {
            segmentColor = Color.parseColor("#ffece816"); //Yellow
        } else if (pressureValue > 25 && pressureValue <= 45 ) {
            segmentColor = Color.parseColor("#ffec8716"); //Orange
        } else if(pressureValue > 50){
            segmentColor = Color.parseColor("#ffda2424"); //Red
        }

        return segmentColor;
    }

    @Override
    public String toString() {
        return "pressureValue=" + pressureValue + ", timeStamp=" + timeStamp;
    }
}
