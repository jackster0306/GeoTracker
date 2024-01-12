package com.psyjg14.coursework2.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.RadioGroup;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.model.DatabaseRepository;

import java.util.List;

public class ViewDataViewModel extends AndroidViewModel {

    private MutableLiveData<String> distanceTravelled = new MutableLiveData<>();
    private MutableLiveData<String> timeTaken = new MutableLiveData<>();
    private MutableLiveData<String> numOfSessions = new MutableLiveData<>();

    private MutableLiveData<Integer> timeSelectedData = new MutableLiveData<>();
    private MutableLiveData<Integer> typeSelectedData = new MutableLiveData<>();
    private MutableLiveData<List<LatLng>> path = new MutableLiveData<>();

    private String distanceUnit;

    private DatabaseRepository databaseRepository;

    public ViewDataViewModel(Application application) {
        super(application);
        databaseRepository = new DatabaseRepository(application);
        timeSelectedData.setValue(R.id.allButton);
        typeSelectedData.setValue(R.id.allButton);
        path.setValue(null);
    }

    public LiveData<MovementEntity> getLastMovement(){
        return databaseRepository.getLastMovementEntity();
    }

    public LiveData<List<MovementEntity>> getMovementsByTime(long startTime, long endTime){
        return databaseRepository.getMovementEntitiesByTime(startTime, endTime);
    }

    public LiveData<List<MovementEntity>> getMovementsByTimeAndType(String movementType, long startTime, long endTime){
        return databaseRepository.getMovementEntitiesByTimeAndType(movementType, startTime, endTime);
    }

    public MutableLiveData<Integer> getTimeSelected() {
        return timeSelectedData;
    }

    public MutableLiveData<Integer> getTypeSelected() {
        return typeSelectedData;
    }

    public String getDistanceUnit(){
        return distanceUnit;
    }

    public void onRadioButtonPressed(RadioGroup group, int buttonID){
        Log.d("COMP3018", "Button Pressed: " + buttonID);
        int groupID = group.getId();
        if(groupID == R.id.timeRadioGroup){
            timeSelectedData.setValue(buttonID);
        } else if(groupID == R.id.typeRadioGroup){
            typeSelectedData.setValue(buttonID);
        }
        updateDistanceAndTime();
    }

    public void updateDistanceAndTime(){



    }

    public void setDistanceTravelled(String distanceTravelledString) {
        distanceTravelled.setValue(distanceTravelledString);
    }

    public void setNumOfSessions(String numOfSessionsString) {
        numOfSessions.setValue(numOfSessionsString);
    }

    public void setTimeTaken(String timeTakenString) {
        timeTaken.setValue(timeTakenString);
    }

    public String getType(int typeSelected){
        if(typeSelected == R.id.walkButton){
            return "walk";
        } else if(typeSelected == R.id.runButton){
            return "run";
        } else if(typeSelected == R.id.cycleButton){
            return "cycle";
        } else{
            return "all";
        }
    }

    public MutableLiveData<String> getDistanceTravelled() {
        return distanceTravelled;
    }

    public MutableLiveData<String> getTimeTaken() {
        return timeTaken;
    }

    public MutableLiveData<String> getNumOfSessions() {
        return numOfSessions;
    }

    public void setPath(List<LatLng> newPath){
        path.setValue(newPath);
    }

    public LiveData<List<LatLng>> getPath(){
        return path;
    }

    public void setDistanceUnit(String distanceUnit){
        this.distanceUnit = distanceUnit;
    }
}
