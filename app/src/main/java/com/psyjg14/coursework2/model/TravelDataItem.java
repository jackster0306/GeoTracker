package com.psyjg14.coursework2.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


/**
 * The TravelDataItem class represents a single item of travel data, including the name, travel type, and date of completion.
 * It is designed to be used in a RecyclerView.
 */
public class TravelDataItem {
    private MutableLiveData<String> dateCompleted = new MutableLiveData<>();

    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> travelType = new MutableLiveData<>();

    /**
     * Constructs a TravelDataItem with the specified parameters.
     *
     * @param name             The name associated with the travel data item.
     * @param travelType       The type of travel associated with the item.
     * @param completionString The date of completion as a string.
     */
    public TravelDataItem(String name, String travelType, String completionString) {
        this.name.setValue(name);
        this.travelType.setValue(travelType);
        this.dateCompleted.setValue(completionString);
    }

    /**
     * Gets the name of the travel data item.
     *
     * @return LiveData containing the name.
     */
    public LiveData<String> getName() {
        return name;
    }

    /**
     * Gets the travel type of the travel data item.
     *
     * @return LiveData containing the travel type.
     */
    public LiveData<String> getTravelType() {
        return travelType;
    }

    /**
     * Gets the date of completion of the travel data item.
     *
     * @return LiveData containing the date of completion.
     */
    public LiveData<String> getDateCompleted() {
        return dateCompleted;
    }
}

