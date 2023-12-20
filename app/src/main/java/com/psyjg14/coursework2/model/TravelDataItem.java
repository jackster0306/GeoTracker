package com.psyjg14.coursework2.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TravelDataItem {
    private MutableLiveData<Long> completionTime = new MutableLiveData<>();

    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> distance = new MutableLiveData<>();
    private MutableLiveData<String> timeTaken = new MutableLiveData<>();
    private MutableLiveData<String> travelType = new MutableLiveData<>();

    public TravelDataItem(String name, float distance, double timeTaken, String travelType, long completionTime) {
        this.name.postValue(name);
        this.distance.postValue("Distance: "+distance);
        this.timeTaken.postValue("Time Taken: " + timeTaken);
        this.travelType.postValue(travelType);
        this.completionTime.postValue(completionTime);
    }
    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getDistance() {
        return distance;
    }

    public LiveData<String> getTimeTaken() {
        return timeTaken;
    }

    public LiveData<String> getTravelType() {
        return travelType;
    }

    public LiveData<Long> getCompletionTime() {
        return completionTime;
    }
}
