package com.psyjg14.coursework2.model;

import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Handler;

public class TravelDataItem {
    private MutableLiveData<String> dateCompleted = new MutableLiveData<>();

    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> travelType = new MutableLiveData<>();

    public TravelDataItem(String name, String travelType, String completionString) {
        this.name.setValue(name);
        this.travelType.setValue(travelType);
        this.dateCompleted.setValue(completionString);
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
