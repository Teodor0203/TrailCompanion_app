package com.example.testing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewmodel extends ViewModel {

    private final MutableLiveData<GpsWaypoint> waypointMutableLiveData = new MutableLiveData<>();

    public LiveData<GpsWaypoint> getWaypoint()
    {
        return waypointMutableLiveData;
    }

    public void setWaypointMutableLiveData(GpsWaypoint gpsWaypoint)
    {
        waypointMutableLiveData.setValue(gpsWaypoint);
    }
}
