package com.psyjg14.coursework2.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;
import com.psyjg14.coursework2.R;

import java.util.List;

public class SpecificTravelViewModel extends ViewModel {
    private MutableLiveData<List<LatLng>> path = new MutableLiveData<>();

    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> distanceText = new MutableLiveData<>();
    private MutableLiveData<String> timeTakenText = new MutableLiveData<>();
    private MutableLiveData<String> travelType = new MutableLiveData<>();
    private MutableLiveData<String> completionTimeText = new MutableLiveData<>();

    private String distanceUnit;

    public void setDistanceUnit(String distanceUnit){
        this.distanceUnit = distanceUnit;
    }

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
        if(distanceUnit.equals("metric")){
            distanceText.postValue("Distance Travelled: " + distance/1000 + " kilometres");
        }
        else{
            distanceText.postValue("Distance Travelled: " + distance*0.000621371 + " miles");
        }
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
