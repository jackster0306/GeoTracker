package com.psyjg14.coursework2.viewmodel;

import android.util.Log;
import android.widget.RadioGroup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.psyjg14.coursework2.R;

public class ManageCurrentJourneyViewModel extends ViewModel {
    private int buttonSelected = R.id.walkButton;

    private MutableLiveData<Boolean> isTracking = new MutableLiveData<>();

    private MutableLiveData<String> name = new MutableLiveData<>();

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
        buttonSelected = buttonID;
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
        return "Walk";
    }


}
