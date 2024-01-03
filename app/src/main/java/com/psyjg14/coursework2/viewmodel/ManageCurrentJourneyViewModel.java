package com.psyjg14.coursework2.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.psyjg14.coursework2.DatabaseSingleton;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.AppDatabase;
import com.psyjg14.coursework2.database.dao.MovementDao;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.view.MainActivity;

import java.util.List;

public class ManageCurrentJourneyViewModel extends ViewModel {
    private int buttonSelected = R.id.walkButton;

    private MutableLiveData<Boolean> isTracking = new MutableLiveData<>();

    private MutableLiveData<String> name = new MutableLiveData<>();

    private MutableLiveData<List<MovementEntity>> movements = new MutableLiveData<>();

    public ManageCurrentJourneyViewModel() {
        isTracking.setValue(false);
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

    public LiveData<List<MovementEntity>> getMovementEntities(Context context){

        new Thread(()-> {
            AppDatabase db = DatabaseSingleton.getDatabaseInstance(context);
            MovementDao movementDao = db.movementDao();
            List<MovementEntity> newMovements = movementDao.getAllMovements();
            movements.postValue(newMovements);
        }).start();
        return movements;
    }


}
