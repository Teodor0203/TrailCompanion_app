package com.example.testing;

import android.telephony.CarrierConfigManager;

public class Data {

    private GpsWaypoint gpsWaypoint;
    private PressureData pressureData;

    private GpsData gpsData;

    public Data(GpsWaypoint gpsWaypoint, PressureData pressureData, GpsData gpsData) {
        this.gpsWaypoint = gpsWaypoint;
        this.pressureData = pressureData;
        this.gpsData = gpsData;
    }

    public GpsWaypoint getGpsWaypoint() {
        return gpsWaypoint;
    }

    public PressureData getPressureData() {
        return pressureData;
    }

    public GpsData getGpsData() { return gpsData; }

    public void setGpsWaypoint(GpsWaypoint gpsWaypoint) {
        this.gpsWaypoint = gpsWaypoint;
    }

    public void setPressureData(PressureData pressureData) {
        this.pressureData = pressureData;
    }

    public void setGpsData(GpsData gpsData) { this.gpsData = gpsData; }
}
