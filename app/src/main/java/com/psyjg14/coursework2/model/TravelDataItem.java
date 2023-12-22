package com.psyjg14.coursework2.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TravelDataItem {
    private MutableLiveData<String> dateCompleted = new MutableLiveData<>();

    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> travelType = new MutableLiveData<>();

    public TravelDataItem(String name, String travelType, long completionTime) {
        this.name.postValue(name);
        this.travelType.postValue(travelType);
        Date date = new Date(completionTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss", Locale.UK);
        this.dateCompleted.postValue("Date Completed: "+ dateFormat.format(date));
    }
    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getTravelType() {
        return travelType;
    }

    public LiveData<String> getDateCompleted() {
        return dateCompleted;
    }



}
