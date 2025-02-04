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

    /**
     * Constructor for the ManageCurrentJourneyViewModel.
     *
     * @param application The application context.
     */
    public ManageCurrentJourneyViewModel(Application application) {
        super(application);
        isTracking.setValue(false);
        databaseRepository = new DatabaseRepository(application);
        allMovements = databaseRepository.getAllMovements();
    }

    /**
     * Gets the name of the current journey.
     *
     * @return The name of the current journey.
     */
    public String getName(){
        return name.getValue();
    }

    /**
     * Sets the name of the current journey.
     *
     * @param s The name of the current journey.
     */
    public void setName(String s){
        name.setValue(s);
    }

    private boolean isBound;


    /**
     * Gets the button selected.
     *
     * @return The button selected.
     */
    public void onRadioButtonPressed(int buttonID){
        buttonSelected = buttonID-1;
    }

    /**
     * Gets if the user is tracking a journey.
     * @return isTracking if the user is tracking a journey.
     */
    public LiveData<Boolean> getIsTracking() {
        return isTracking;
    }

    /**
     * Sets if the user is tracking a journey.
     * @param b
     */
    public void setIsTracking(boolean b) {
        isTracking.setValue(b);
    }

    /**
     * Gets if the service is bound
     * @return isBound if the service is bound
     */
    public boolean getIsBound() {
        return isBound;
    }

    /**
     * Sets if the service is bound
     * @param b
     */
    public void setIsBound(boolean b) {
        isBound = b;
    }

    /**
     * Gets the string related to the button selected.
     *
     * @return The String correlating to the button selected.
     */
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

    /**
     * Sets the button selected.
     *
     * @param type The button selected as a string.
     */
    public void setType(String type){
        if(type.equals("Walk")){
            buttonSelected = R.id.walkButton;
        } else if(type.equals("Run")){
            buttonSelected = R.id.runButton;
        } else if(type.equals("Cycle")){
            buttonSelected = R.id.cycleButton;
        }
    }

    /**
     * Gets all movement entities in teh database
     * @return allMovements all movement entities in the database
     */
    public LiveData<List<MovementEntity>> getMovementEntities(){
        return allMovements;
    }

    /**
     * Sets the default name of the journey
     * @param newName the default name of the journey
     */
    public void setDefaultName(String newName){
        defaultName = newName;
    }

    /**
     * Gets the default name of the journey
     * @return defaultName the default name of the journey
     */
    public String getDefaultName(){
        return defaultName;
    }

    /**
     * Gets the note associated with the journey
     * @return note the note associated with the journey
     */
    public String getNote(){
        return note;
    }

    /**
     * Sets the note associated with the journey
     * @param newNote the note associated with the journey
     */
    public void setNote(String newNote){
        note = newNote;
    }


}
