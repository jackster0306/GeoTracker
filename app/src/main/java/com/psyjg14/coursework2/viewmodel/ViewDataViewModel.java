package com.psyjg14.coursework2.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.RadioGroup;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.google.android.gms.maps.model.LatLng;
import com.psyjg14.coursework2.DatabaseSingleton;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.AppDatabase;
import com.psyjg14.coursework2.database.dao.MovementDao;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.view.MainActivity;

import java.util.Calendar;
import java.util.List;

public class ViewDataViewModel extends AndroidViewModel {

    private MutableLiveData<String> distanceTravelled = new MutableLiveData<>();
    private MutableLiveData<String> timeTaken = new MutableLiveData<>();
    private MutableLiveData<String> numOfSessions = new MutableLiveData<>();

    private int timeSelected = R.id.allButton;
    private int typeSelected = R.id.allButton;

    private final MovementDao movementDao;

    public ViewDataViewModel(Application application) {
        super(application);
        AppDatabase appDatabase = DatabaseSingleton.getDatabaseInstance(application.getApplicationContext());
        movementDao = appDatabase.movementDao();
    }

    public void onRadioButtonPressed(RadioGroup group, int buttonID){
        Log.d("COMP3018", "Button Pressed: " + buttonID);
        int groupID = group.getId();
        if(groupID == R.id.timeRadioGroup){
            timeSelected = buttonID;
        } else if(groupID == R.id.typeRadioGroup){
            typeSelected = buttonID;
        }
        updateDistanceAndTime();
    }

    public void updateDistanceAndTime(){
        Calendar calendar = Calendar.getInstance();
        long endTime = System.currentTimeMillis();
        long startTime;
        if(timeSelected == R.id.dayButton){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            startTime = calendar.getTimeInMillis();
        } else if(timeSelected == R.id.weekButton){
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            startTime = calendar.getTimeInMillis();
        } else if(timeSelected == R.id.monthButton){
            calendar.add(Calendar.MONTH, -1);
            startTime = calendar.getTimeInMillis();
        } else if(timeSelected == R.id.yearButton){
            calendar.add(Calendar.YEAR, -1);
            startTime = calendar.getTimeInMillis();
        } else{
            startTime = 0;
        }

        if(typeSelected == R.id.allButton){
            new Thread(()-> {
                List<MovementEntity> movements = movementDao.getMovementEntitiesByTime(startTime, endTime);
                float distance = 0;
                long time = 0;
                if(movements.size() > 0){
                    for(MovementEntity movement : movements){
                        distance += movement.distanceTravelled;
                        time += movement.timeTaken;
                    }
                }
                distanceTravelled.postValue(String.format("Distance Travelled: %.2f", distance/1000) + " kilometres");
                timeTaken.postValue(String.format("Time Taken: %d", time) + " seconds");
                numOfSessions.postValue("Number of Sessions: " + movements.size());
            }).start();
        }else{
            new Thread(()-> {
                List<MovementEntity> movements = movementDao.getMovementEntitiesByTimeAndType(getType(), startTime, endTime);
                float distance = 0;
                long time = 0;
                if(movements.size() > 0){
                    for(MovementEntity movement : movements){
                        distance += movement.distanceTravelled;
                        time += movement.timeTaken;
                    }
                }
                distanceTravelled.postValue(String.format("Distance Travelled: %.2f", distance/1000) + " kilometres");
                timeTaken.postValue(String.format("Time Taken: %d", time) + " seconds");
                numOfSessions.postValue("Number of Sessions: " + movements.size());
            }).start();
        }



    }

    public String getType(){
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

    public LiveData<List<LatLng>> getPath(){
        MutableLiveData<List<LatLng>> path = new MutableLiveData<>();
        new Thread(() -> {
            MovementEntity entity = movementDao.getLastEntity();
            if(entity != null){
                path.postValue(entity.path);
            }
        }).start();
        return path;
    }
}
