package com.psyjg14.coursework2.model;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.psyjg14.coursework2.R;

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

    public void onShowMapPressed(View view) {
        if (view.getContext() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();

            // Find the existing map fragment in the main activity
            SupportMapFragment existingMapFragment = (SupportMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.dataMap);

            if (existingMapFragment != null) {
                // If the map fragment already exists, you can perform any additional configuration here
                // For example, you can add markers or customize the map

                // Show the existing map fragment using a FragmentTransaction
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.replace(android.R.id.content, existingMapFragment);
                transaction.addToBackStack(null);  // Add to the back stack to handle the back button
                transaction.commit();
            }
        }
    }
}
