package com.psyjg14.coursework2.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.model.DatabaseRepository;

import java.util.List;

public class ManageCurrentJourneyViewModel extends AndroidViewModel {
    private int buttonSelected = R.id.walkButton;

    private MutableLiveData<Boolean> isTracking = new MutableLiveData<>();

    private MutableLiveData<String> name = new MutableLiveData<>();

    private MutableLiveData<List<MovementEntity>> movements = new MutableLiveData<>();

    private DatabaseRepository databaseRepository;
    private LiveData<List<MovementEntity>> allMovements;
    String defaultName = "Journey";
    private String note = "";

    public ManageCurrentJourneyViewModel(Application application) {
        super(application);
        isTracking.setValue(false);
        databaseRepository = new DatabaseRepository(application);
        allMovements = databaseRepository.getAllMovements();
    }

    public String getName(){
        return name.getValue();
    }

    public void setName(String s){
        Log.d("COMP3018", "Setting name to: " + s);

        name.setValue(s);
    }

    private boolean isBound;


    public void onRadioButtonPressed(int buttonID){
        Log.d("COMP3018", "Button Pressed: " + buttonID + ", Walk Button: " + R.id.walkButton + ", Run Button: " + R.id.runButton + ", Cycle Button: " + R.id.cycleButton);
        buttonSelected = buttonID-1;
    }

    public LiveData<Boolean> getIsTracking() {
        return isTracking;
    }

    public void setIsTracking(boolean b) {
        isTracking.setValue(b);
    }

    public boolean getIsBound() {
        return isBound;
    }

    public void setIsBound(boolean b) {
        isBound = b;
    }

    public String getType(){
        if(buttonSelected == R.id.walkButton){
            return "Walk";
        } else if(buttonSelected == R.id.runButton){
            return "Run";
        } else if(buttonSelected == R.id.cycleButton){
            return "Cycle";
        }
        return "test";
    }

    public void setType(String type){
        if(type.equals("Walk")){
            buttonSelected = R.id.walkButton;
        } else if(type.equals("Run")){
            buttonSelected = R.id.runButton;
        } else if(type.equals("Cycle")){
            buttonSelected = R.id.cycleButton;
        }
    }

    public LiveData<List<MovementEntity>> getMovementEntities(){
        return allMovements;
    }

    public void setDefaultName(String newName){
        defaultName = newName;
    }

    public String getDefaultName(){
        return defaultName;
    }

    public String getNote(){
        return note;
    }

    public void setNote(String newNote){
        note = newNote;
    }


}
