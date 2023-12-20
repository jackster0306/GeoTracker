package com.psyjg14.coursework2.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class SpecificTravelViewModel extends ViewModel {
    private MutableLiveData<List<LatLng>> path = new MutableLiveData<>();

    public MutableLiveData<List<LatLng>> getPath() {
        return path;
    }

    public void setPath(List<LatLng> newPath){
        path.postValue(newPath);
    }
}
