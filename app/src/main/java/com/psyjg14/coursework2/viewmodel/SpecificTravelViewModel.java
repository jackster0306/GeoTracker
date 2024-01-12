package com.psyjg14.coursework2.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.model.DatabaseRepository;

import java.util.List;

public class SpecificTravelViewModel extends AndroidViewModel {
    private MutableLiveData<List<LatLng>> path = new MutableLiveData<>();

    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> distanceText = new MutableLiveData<>();
    private MutableLiveData<String> timeTakenText = new MutableLiveData<>();
    private MutableLiveData<String> travelTypeText = new MutableLiveData<>();
    private MutableLiveData<String> completionTimeText = new MutableLiveData<>();

    private String distanceUnit;

    private MutableLiveData<Boolean> isWalk = new MutableLiveData<>();
    private MutableLiveData<Boolean> isRun = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCycle = new MutableLiveData<>();
    private String travelType;

    private DatabaseRepository databaseRepository;
    private LiveData<List<MovementEntity>> movements;

    public SpecificTravelViewModel(Application application) {
        super(application);
        databaseRepository = new DatabaseRepository(application);
        movements = databaseRepository.getAllMovements();
        isWalk.setValue(false);
        isRun.setValue(false);
        isCycle.setValue(false);
    }

    public LiveData<List<MovementEntity>> getAllMovements() {
        return movements;
    }

    public void deleteMovement(MovementEntity movementEntity){
        databaseRepository.deleteMovement(movementEntity);
    }

    public void updateMovement(MovementEntity movementEntity){
        databaseRepository.updateMovement(movementEntity);
    }

    public void insertMovement(MovementEntity movementEntity){
        databaseRepository.insertMovement(movementEntity);
    }

    public LiveData<MovementEntity> getMovementById(String name){
        return databaseRepository.getMovementById(name);
    }



    public void onRadioButtonPressed(int buttonID){
        Log.d("COMP3018", "onRadioButtonPressed: " + buttonID);
        Log.d("COMP3018", "walk button: " + R.id.walkRadioButton + " run button: " + R.id.runRadioButton + " cycle button: " + R.id.cycleRadioButton);

        if(buttonID == R.id.walkRadioButton){
            Log.d("COMP3018", "onRadioButtonPressed: walk");
            travelTypeText.setValue("Travel Type: walk");
            travelType = "walk";
        } else if(buttonID == R.id.runRadioButton){
            Log.d("COMP3018", "onRadioButtonPressed: run");
            travelTypeText.setValue("Travel Type: run");
            travelType = "run";
        } else if(buttonID == R.id.cycleRadioButton){
            Log.d("COMP3018", "onRadioButtonPressed: cycle");
            travelTypeText.setValue("Travel Type: cycle");
            travelType = "cycle";
        }
        setChecked();
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

    public void setTravelTypeText(String type){
        travelTypeText.postValue("Travel Type: " + type);
    }

    public MutableLiveData<String> getTravelTypeText() {
        return travelTypeText;
    }


    public void setCompletionTime(long time){
        completionTimeText.postValue("Completion Time: " + time);
    }
    public MutableLiveData<String> getCompletionTime() {
        return completionTimeText;
    }

    public void setChecked(){
        Log.d("COMP3018", "setChecked: " + travelTypeText.getValue());
//        if(travelTypeText.getValue().equals("Travel Type: walk")){
//            Log.d("COMP3018", "setChecked: walk");
//            isWalk.setValue(true);
//        } else if(travelTypeText.getValue().equals("Travel Type: run")){
//            Log.d("COMP3018", "setChecked: run");
//            isRun.setValue(true);
//        } else if(travelTypeText.getValue().equals("Travel Type: cycle")){
//            Log.d("COMP3018", "setChecked: cycle");
//            isCycle.setValue(true);
//        }
        if(travelType.equals("walk")){
            isWalk.setValue(true);
            isRun.setValue(false);
            isCycle.setValue(false);
        } else if(travelType.equals("run")){
            isRun.setValue(true);
            isWalk.setValue(false);
            isCycle.setValue(false);
        } else if(travelType.equals("cycle")){
            isCycle.setValue(true);
            isWalk.setValue(false);
            isRun.setValue(false);
        }
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

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }
}
