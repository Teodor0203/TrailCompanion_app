package com.example.testing;

import java.util.ArrayList;
import java.util.List;

public class Trail {
    private List<GpsWaypoint> waypoints;
    private NEO6M gpsModule;

    public Trail(NEO6M gpsModule) {
        this.gpsModule = gpsModule;
        this.waypoints = new ArrayList<GpsWaypoint>();
    }

    public void addWaypoint(){
        GpsWaypoint currentWaypoint = gpsModule.getWaypoint();
        if (currentWaypoint != null) {
            //System.out.println("Adding waypoint: " + currentWaypoint);
            waypoints.add(currentWaypoint);
        } else {
            //System.out.println("Current waypoint is null");
        }
    }

    public List<GpsWaypoint> getWaypoints() {
        return waypoints;
    }
}