package com.psyjg14.coursework2.viewmodel;

import android.app.Application;
import android.widget.RadioGroup;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.model.DatabaseRepository;

import java.util.List;

/**
 * ViewModel for managing the ViewDataActivity, containing LiveData for the activity.
 */
public class ViewDataViewModel extends AndroidViewModel {

    private MutableLiveData<String> distanceTravelled = new MutableLiveData<>();
    private MutableLiveData<String> timeTaken = new MutableLiveData<>();
    private MutableLiveData<String> numOfSessions = new MutableLiveData<>();

    private MutableLiveData<Integer> timeSelectedData = new MutableLiveData<>();
    private MutableLiveData<Integer> typeSelectedData = new MutableLiveData<>();
    private MutableLiveData<List<LatLng>> path = new MutableLiveData<>();

    private String distanceUnit;

    private DatabaseRepository databaseRepository;

    /**
     * Constructor for the ViewDataViewModel.
     *
     * @param application The application context.
     */
    public ViewDataViewModel(Application application) {
        super(application);
        databaseRepository = new DatabaseRepository(application);
        timeSelectedData.setValue(R.id.allButton);
        typeSelectedData.setValue(R.id.allButton);
        path.setValue(null);
    }

    /**
     * Gets the last movement entity recorded.
     *
     * @return LiveData containing the last movement entity recorded.
     */
    public LiveData<MovementEntity> getLastMovement(){
        return databaseRepository.getLastMovementEntity();
    }

    /**
     * Gets a list of movement entities within a specified time range.
     *
     * @param startTime The start time of the range.
     * @param endTime   The end time of the range.
     * @return LiveData containing a list of movement entities within the specified time range.
     */
    public LiveData<List<MovementEntity>> getMovementsByTime(long startTime, long endTime){
        return databaseRepository.getMovementEntitiesByTime(startTime, endTime);
    }

    /**
     * Gets a list of movement entities with a specified type within a time range.
     *
     * @param movementType The type of movement (walk, run, cycle, etc.).
     * @param startTime    The start time of the range.
     * @param endTime      The end time of the range.
     * @return LiveData containing a list of movement entities with the specified type within the time range.
     */
    public LiveData<List<MovementEntity>> getMovementsByTimeAndType(String movementType, long startTime, long endTime){
        return databaseRepository.getMovementEntitiesByTimeAndType(movementType, startTime, endTime);
    }

    /**
     * Gets the selected time filter.
     *
     * @return MutableLiveData containing the selected time filter.
     */
    public MutableLiveData<Integer> getTimeSelected() {
        return timeSelectedData;
    }

    /**
     * Gets the selected type filter.
     *
     * @return MutableLiveData containing the selected type filter.
     */
    public MutableLiveData<Integer> getTypeSelected() {
        return typeSelectedData;
    }

    /**
     * Gets the distance unit (metric or imperial).
     *
     * @return The distance unit.
     */
    public String getDistanceUnit(){
        return distanceUnit;
    }

    /**
     * Handles the selection of radio buttons in a RadioGroup.
     *
     * @param group    The RadioGroup.
     * @param buttonID The ID of the selected radio button.
     */
    public void onRadioButtonPressed(RadioGroup group, int buttonID){
        int groupID = group.getId();
        if(groupID == R.id.timeRadioGroup){
            timeSelectedData.setValue(buttonID);
        } else if(groupID == R.id.typeRadioGroup){
            typeSelectedData.setValue(buttonID);
        }
    }

    /**
     * Sets the distance travelled information.
     *
     * @param distanceTravelledString The distance travelled information.
     */
    public void setDistanceTravelled(String distanceTravelledString) {
        distanceTravelled.setValue(distanceTravelledString);
    }

    /**
     * Sets the number of sessions.
     *
     * @param numOfSessionsString The number of sessions information.
     */
    public void setNumOfSessions(String numOfSessionsString) {
        numOfSessions.setValue(numOfSessionsString);
    }

    /**
     * Sets the time taken information.
     *
     * @param timeTakenString The time taken information.
     */
    public void setTimeTaken(String timeTakenString) {
        timeTaken.setValue(timeTakenString);
    }

    /**
     * Gets the movement type based on the selected type button.
     *
     * @param typeSelected The ID of the selected type button.
     * @return The movement type.
     */
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

    /**
     * Gets the distance travelled information.
     *
     * @return MutableLiveData containing the distance travelled information.
     */
    public MutableLiveData<String> getDistanceTravelled() {
        return distanceTravelled;
    }

    /**
     * Getsthe time taken information.
     *
     * @return MutableLiveData containing the time taken information.
     */
    public MutableLiveData<String> getTimeTaken() {
        return timeTaken;
    }

    /**
     * Gets the number of sessions.
     *
     * @return MutableLiveData containing the number of sessions information.
     */
    public MutableLiveData<String> getNumOfSessions() {
        return numOfSessions;
    }

    /**
     * Sets the path information.
     *
     * @param newPath The path information.
     */
    public void setPath(List<LatLng> newPath){
        path.setValue(newPath);
    }

    /**
     * Gets the path information.
     *
     * @return LiveData containing the path information.
     */
    public LiveData<List<LatLng>> getPath(){
        return path;
    }

    /**
     * Sets the distance unit.
     *
     * @param distanceUnit The distance unit.
     */
    public void setDistanceUnit(String distanceUnit){
        this.distanceUnit = distanceUnit;
    }
}
