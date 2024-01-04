package com.psyjg14.coursework2.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

    private MutableLiveData<Boolean> isWalk = new MutableLiveData<>();
    private MutableLiveData<Boolean> isRun = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCycle = new MutableLiveData<>();

    public SpecificTravelViewModel(){
        isWalk.setValue(false);
        isRun.setValue(false);
        isCycle.setValue(false);
    }



    public void onRadioButtonPressed(int buttonID){
        Log.d("COMP3018", "onRadioButtonPressed: " + buttonID);
        Log.d("COMP3018", "walk button: " + R.id.walkRadioButton + " run button: " + R.id.runRadioButton + " cycle button: " + R.id.cycleRadioButton);

        if(buttonID == R.id.walkRadioButton){
            Log.d("COMP3018", "onRadioButtonPressed: walk");
            travelType.setValue("Travel Type: walk");
        } else if(buttonID == R.id.runRadioButton){
            Log.d("COMP3018", "onRadioButtonPressed: run");
            travelType.setValue("Travel Type: run");
        } else if(buttonID == R.id.cycleRadioButton){
            Log.d("COMP3018", "onRadioButtonPressed: cycle");
            travelType.setValue("Travel Type: cycle");
        }
    }

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

    public void setChecked(){
        Log.d("COMP3018", "setChecked: " + travelType.getValue());
        if(travelType.getValue().equals("Travel Type: walk")){
            Log.d("COMP3018", "setChecked: walk");
            isWalk.setValue(true);
        } else if(travelType.getValue().equals("Travel Type: run")){
            Log.d("COMP3018", "setChecked: run");
            isRun.setValue(true);
        } else if(travelType.getValue().equals("Travel Type: cycle")){
            Log.d("COMP3018", "setChecked: cycle");
            isCycle.setValue(true);
        }
//        if(type.equals("walk")){
//            isWalk.postValue(true);
//        } else if(type.equals("run")){
//            isRun.postValue(true);
//        } else if(type.equals("cycle")){
//            isCycle.postValue(true);
//        }
    }

    public LiveData<Boolean> getIsWalk() {
        setChecked();
        return isWalk;
    }

    public LiveData<Boolean> getIsRun() {
        setChecked();
        return isRun;
    }

    public LiveData<Boolean> getIsCycle() {
        setChecked();

        return isCycle;
    }




}
