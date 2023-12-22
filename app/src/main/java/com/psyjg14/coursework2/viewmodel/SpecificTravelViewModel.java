package com.psyjg14.coursework2.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class SpecificTravelViewModel extends ViewModel {
    private MutableLiveData<List<LatLng>> path = new MutableLiveData<>();

    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> distanceText = new MutableLiveData<>();
    private MutableLiveData<String> timeTakenText = new MutableLiveData<>();
    private MutableLiveData<String> travelType = new MutableLiveData<>();
    private MutableLiveData<String> completionTimeText = new MutableLiveData<>();

    public void setPath(List<LatLng> newPath){
        path.postValue(newPath);
    }

    public MutableLiveData<List<LatLng>> getPath() {
        return path;
    }

    public void setName(String newName){
        name.setValue(newName);
    }
    public MutableLiveData<String> getName() {
        return name;
    }

    public void setDistance(double distance){
        distanceText.postValue("Distance Travelled: " + distance/1000 + " kilometres");
    }
    public MutableLiveData<String> getDistance() {
        return distanceText;
    }

    public void setTimeTaken(double timeTaken){
        timeTakenText.postValue("Time Taken: " + timeTaken + " seconds");
    }
    public MutableLiveData<String> getTimeTaken() {
        return timeTakenText;
    }

    public void setTravelType(String type){
        travelType.postValue("Travel Type: " + type);
    }

    public MutableLiveData<String> getTravelType() {
        return travelType;
    }


    public void setCompletionTime(long time){
        completionTimeText.postValue("Completion Time: " + time);
    }
    public MutableLiveData<String> getCompletionTime() {
        return completionTimeText;
    }




}
