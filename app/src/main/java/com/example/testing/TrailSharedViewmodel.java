package com.example.testing;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TrailSharedViewmodel extends ViewModel {

    private final String TAG = "TrailManager";
    private final MutableLiveData<List<String>> trailsNameLiveData = new MutableLiveData<>();

    public MutableLiveData<List<String >> getTrailNames()
    {
        return trailsNameLiveData;
    }

    public void setTrailsName(List<String> trailList)
    {
        trailsNameLiveData.setValue(trailList);
    }

}
