package com.example.testing;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewmodel extends ViewModel {

    private final String TAG = "SharedViewModel";
    private final MutableLiveData<Data> dataLiveData = new MutableLiveData<>();

    public LiveData<Data> getDataLiveData() {
        return dataLiveData;
    }

    public void setData(Data data) {
        dataLiveData.setValue(data);
    }

    public void updateGpsWaypoint(GpsWaypoint gpsWaypoint) {
        Data currentData = dataLiveData.getValue();

        if (currentData == null) {
            currentData = new Data(null, null, null, null);
        }

        currentData.setGpsWaypoint(gpsWaypoint);
        dataLiveData.setValue(currentData);
        Log.d(TAG, "onViewCreated: GpsWypointUpdated");
    }

    public void updatePressureData(PressureData pressureData) {
        Data currentData = dataLiveData.getValue();

        if (currentData == null) {
            currentData = new Data(null, null, null, null);
        }

        currentData.setPressureData(pressureData);
        dataLiveData.setValue(currentData);
        Log.d(TAG, "onViewCreated: PressureDataUpdated");
    }

    public void updateGpsData(GpsData gpsData)
    {
        Data currentData = dataLiveData.getValue();

        if (currentData == null) {
            currentData = new Data(null, null, null, null);
        }

        currentData.setGpsData(gpsData);
        dataLiveData.setValue(currentData);
        Log.d(TAG, "onViewCreated: PressureDataUpdated");
    }

    public void updateAccData(JumpData accData)
    {
        Data currentData = dataLiveData.getValue();

        if(currentData == null)
        {
            currentData = new Data(null, null, null, null);
        }

        currentData.setAccData(accData);
        dataLiveData.setValue(currentData);
        Log.d(TAG, "...");;
    }
}