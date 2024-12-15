package com.example.testing;

public class GpsWaypoint {

    private double latitude;
    private double longitude;
    private long timeStamp;

    public GpsWaypoint() {
    }

    public synchronized void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public synchronized void setLongitude(double longitude) { this.longitude = longitude; }

    public synchronized void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

    public long getTimeStamp() { return timeStamp; }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "timeStamp= " + timeStamp + "latitude=" + latitude + ", longitude=" + longitude;
    }
}
