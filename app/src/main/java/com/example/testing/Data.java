package com.example.testing;

public class Data {


    //region Data
    private GpsWaypoint gpsWaypoint;
    private PressureData pressureData;
    private GpsData gpsData;
    private JumpData accData;
    //endregion

    public Data(GpsWaypoint gpsWaypoint, PressureData pressureData, GpsData gpsData, JumpData accData) {
        this.gpsWaypoint = gpsWaypoint;
        this.pressureData = pressureData;
        this.gpsData = gpsData;
        this.accData = accData;
    }

    public GpsWaypoint getGpsWaypoint() {
        return gpsWaypoint;
    }

    public PressureData getPressureData() {
        return pressureData;
    }

    public GpsData getGpsData() { return gpsData; }

    public JumpData getAccData() { return accData; }

    public void setGpsWaypoint(GpsWaypoint gpsWaypoint) {
        this.gpsWaypoint = gpsWaypoint;
    }

    public void setPressureData(PressureData pressureData) {
        this.pressureData = pressureData;
    }

    public void setGpsData(GpsData gpsData) { this.gpsData = gpsData; }

    public void setAccData(JumpData accData) { this.accData = accData; }
}
