package com.example.testing;

public class Data {

    private GpsWaypoint gpsWaypoint;
    private PressureData pressureData;

    public Data(GpsWaypoint gpsWaypoint, PressureData pressureData) {
        this.gpsWaypoint = gpsWaypoint;
        this.pressureData = pressureData;
    }

    public GpsWaypoint getGpsWaypoint() {
        return gpsWaypoint;
    }

    public PressureData getPressureData() {
        return pressureData;
    }

    public void setGpsWaypoint(GpsWaypoint gpsWaypoint) {
        this.gpsWaypoint = gpsWaypoint;
    }

    public void setPressureData(PressureData pressureData) {
        this.pressureData = pressureData;
    }
}
