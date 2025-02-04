package com.psyjg14.coursework2.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.model.DatabaseRepository;

import java.util.List;

/**
 * ViewModel for managing specific travel details.
 */
public class SpecificTravelViewModel extends AndroidViewModel {
    private MutableLiveData<List<LatLng>> path = new MutableLiveData<>();

    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> distanceText = new MutableLiveData<>();
    private MutableLiveData<String> timeTakenText = new MutableLiveData<>();
    private MutableLiveData<String> travelTypeText = new MutableLiveData<>();
    private MutableLiveData<String> completionTimeText = new MutableLiveData<>();

    private MutableLiveData<String> noteText = new MutableLiveData<>();

    private String note;
    private String distanceUnit;

    private MutableLiveData<Boolean> isWalk = new MutableLiveData<>();
    private MutableLiveData<Boolean> isRun = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCycle = new MutableLiveData<>();
    private String travelType;

    private DatabaseRepository databaseRepository;



    /**
     * Constructor for the SpecificTravelViewModel.
     *
     * @param application The application context.
     */
    public SpecificTravelViewModel(Application application) {
        super(application);
        databaseRepository = new DatabaseRepository(application);
        isWalk.setValue(false);
        isRun.setValue(false);
        isCycle.setValue(false);
    }

    /**
     * Gets a list of all movement entities.
     *
     * @return LiveData containing a list of all movement entities.
     */
    public LiveData<List<MovementEntity>> getAllMovements() {
        return databaseRepository.getAllMovements();
    }

    /**
     * Deletes the specified movement entity.
     *
     * @param movementEntity The movement entity to be deleted.
     */
    public void deleteMovement(MovementEntity movementEntity){
        databaseRepository.deleteMovement(movementEntity);
    }

    /**
     * Updates the specified movement entity.
     *
     * @param movementEntity The movement entity to be updated.
     */
    public void updateMovement(MovementEntity movementEntity){
        databaseRepository.updateMovement(movementEntity);
    }

    /**
     * Gets a movement entity with the specified name (if it exists).
     *
     * @param name The name of the movement entity to retrieve.
     * @return LiveData containing a movement entity with the specified name.
     */
    public LiveData<MovementEntity> getMovementById(String name){
        return databaseRepository.getMovementById(name);
    }


    /**
     * Handles the selection of a radio button indicating the travel type.
     *
     * @param buttonID The ID of the selected radio button.
     */
    public void onRadioButtonPressed(int buttonID){
        if(buttonID == R.id.walkRadioButton){
            travelTypeText.setValue("Travel Type: walk");
            travelType = "walk";
        } else if(buttonID == R.id.runRadioButton){
            travelTypeText.setValue("Travel Type: run");
            travelType = "run";
        } else if(buttonID == R.id.cycleRadioButton){
            travelTypeText.setValue("Travel Type: cycle");
            travelType = "cycle";
        }
        setChecked();
    }

    /**
     * Sets the distance unit for the travel.
     *
     * @param distanceUnit The distance unit to be set.
     */
    public void setDistanceUnit(String distanceUnit){
        this.distanceUnit = distanceUnit;
    }

    /**
     * Sets the path for the travel.
     *
     * @param newPath The new path for the travel.
     */
    public void setPath(List<LatLng> newPath){
        path.postValue(newPath);
    }

    /**
     * Gets the list of LatLng points representing the path of travel.
     *
     * @return LiveData containing the list of LatLng points representing the path of the travel.
     */
    public MutableLiveData<List<LatLng>> getPath() {
        return path;
    }

    /**
     * Sets the name of the travel.
     *
     * @param newName The new name to be set for the travel.
     */
    public void setName(String newName){
        name.setValue(newName);
    }

    /**
     * Gets LiveData name of the travel.
     *
     * @return LiveData containing the name of the travel.
     */
    public MutableLiveData<String> getName() {
        return name;
    }

    /**
     * Sets the distance of the travel and updates the corresponding LiveData.
     *
     * @param distance The new distance value for the travel.
     */
    public void setDistance(double distance){
        if(distanceUnit.equals("metric")){
            distanceText.postValue("Distance Travelled: " + distance/1000 + " kilometres");
        }
        else{
            distanceText.postValue("Distance Travelled: " + distance*0.000621371 + " miles");
        }
    }

    /**
     * Gets the formatted distance text.
     *
     * @return LiveData containing the formatted distance text.
     */
    public MutableLiveData<String> getDistance() {
        return distanceText;
    }

    /**
     * Sets the time taken for the travel and updates the corresponding LiveData.
     *
     * @param timeTaken The new time taken value for the travel.
     */
    public void setTimeTaken(double timeTaken){
        long totalseconds = (long) (timeTaken/1000);
        long hours = totalseconds / 3600;
        long remainingSeconds = totalseconds % 3600;
        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds % 60;
        timeTakenText.postValue(String.format("Time Taken: %02dh %02dm %02ds", hours, minutes, seconds));
    }

    /**
     * Gets the formatted time taken text.
     *
     * @return LiveData containing the formatted time taken text.
     */
    public MutableLiveData<String> getTimeTaken() {
        return timeTakenText;
    }


    /**
     * Gets the formatted travel type text.
     *
     * @return LiveData containing the formatted travel type text.
     */
    public MutableLiveData<String> getTravelTypeText() {
        return travelTypeText;
    }


    /**
     * Sets the completion time for the travel.
     *
     * @param time The new completion time value for the travel.
     */
    public void setCompletionTime(long time){
        completionTimeText.postValue("Completion Time: " + time);
    }

    /**
     * Sets the checked state for the radio buttons based on the selected travel type.
     */
    public void setChecked(){
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

    /**
     * Gets whether the travel type is "Walk."
     *
     * @return LiveData indicating whether the travel type is "Walk."
     */
    public LiveData<Boolean> getIsWalk() {
        setChecked();
        return isWalk;
    }

    /**
     * Gets whether the travel type is "Run."
     *
     * @return LiveData indicating whether the travel type is "Run."
     */
    public LiveData<Boolean> getIsRun() {
        setChecked();
        return isRun;
    }

    /**
     * Gets whether the travel type is "Cycle."
     *
     * @return LiveData indicating whether the travel type is "Cycle."
     */
    public LiveData<Boolean> getIsCycle() {
        setChecked();

        return isCycle;
    }

    /**
     * Gets the Travel Type
     *
     * @return LiveData indicating the travel type"
     */
    public String getTravelType() {
        return travelType;
    }

    /**
     * Sets the travel type
     *
     * @param travelType The new travel type
     */
    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    /**
     * Sets the travel type text
     *
     * @param text The new travel type text
     */
    public void setTravelTypeText(String text){
        if(text.equals("walk")){
            travelTypeText.postValue("Travel Type: Walk");
        } else if(text.equals("run")){
            travelTypeText.postValue("Travel Type: Run");
        } else if(text.equals("cycle")){
            travelTypeText.postValue("Travel Type: Cycle");
        } else{
            travelTypeText.postValue("Travel Type: " + text);
        }
    }

    /**
     * Gets the note
     *
     * @return The note
     */
    public String getNote(){
        return note;
    }

    /**
     * Sets the note
     *
     * @param newNote The new note to be set
     */
    public void setNote(String newNote){
        note = newNote;
    }

    /**
     * Sets the note text to be displayed
     *
     * @param note The note text to be displayed
     */
    public void setNoteText(String note){
        noteText.postValue("Note: "+note);
    }

    /**
     * Gets the note text to be displayed
     *
     * @return LiveData containing the note text to be displayed
     */
    public MutableLiveData<String> getNoteText(){
        return noteText;
    }
}
