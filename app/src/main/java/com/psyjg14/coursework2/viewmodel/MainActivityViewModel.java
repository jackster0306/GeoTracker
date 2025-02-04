package com.psyjg14.coursework2.viewmodel;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.model.DatabaseRepository;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;


/**
 * ViewModel for the MainActivity, responsible for managing geofences and the data required by the MainActivity.
 */
public class MainActivityViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> addingGeofence = new MutableLiveData<>();
    private boolean requestingLocationUpdates = false;
    private Marker geofenceMarker;
    private boolean geofenceFirstPressed = true;
    private final List<LatLng> path = new ArrayList<>();
    private DatabaseRepository databaseRepository;
    private LiveData<List<GeofenceEntity>> geofencesLiveData;

    /**
     * Constructor for the MainActivityViewModel.
     *
     * @param application The application context.
     */
    public MainActivityViewModel(Application application) {
        super(application);
        databaseRepository = new DatabaseRepository(application);
        geofencesLiveData = databaseRepository.getAllGeofences();
        addingGeofence.setValue(false);
    }

    /**
     * Updates the information of a geofence in the database.
     *
     * @param geofenceEntity The geofence entity to update.
     */
    public void updateGeofence(GeofenceEntity geofenceEntity) {
        databaseRepository.updateGeofence(geofenceEntity);
    }

    /**
     * Gets all geofences from the database.
     *
     * @return LiveData containing a list of geofences.
     */
    public LiveData<List<GeofenceEntity>> getAllGeofences() {
        return geofencesLiveData;
    }

    /**
     * Gets the path associated with the geofences.
     *
     * @return List of LatLng representing the geofence path.
     */
    public List<LatLng> getPath() {
        return path;
    }

    /**
     * Gets the value indicating whether the geofence button was pressed for the first time.
     *
     * @return True if the geofence button was pressed for the first time, false otherwise.
     */
    public boolean getGeofenceFirstPressed() {
        return geofenceFirstPressed;
    }

    /**
     * Sets the marker associated with the geofence on the map.
     *
     * @param geofenceMarker The marker representing the geofence on the map.
     */
    public void setGeofenceMarker(Marker geofenceMarker) {
        this.geofenceMarker = geofenceMarker;
    }

    /**
     * Removes the geofence marker from the map.
     */
    public void removeGeofenceMarker() {
        geofenceMarker.remove();
    }

    /**
     * Gets the marker associated with the geofence on the map.
     *
     * @return The marker representing the geofence on the map.
     */
    public Marker getGeofenceMarker() {
        return geofenceMarker;
    }

    /**
     * Gets whether the user is currently adding a geofence.
     *
     * @return LiveData containing a boolean value representing whether a geofence is being added.
     */
    public LiveData<Boolean> getAddingGeofence() {
        return addingGeofence;
    }

    /**
     * Sets the value indicating whether the user is currently adding a geofence.
     *
     * @param value The boolean value indicating whether a geofence is being added.
     */
    public void setAddingGeofence(boolean value) {
        addingGeofence.setValue(value);
    }

    /**
     * Gets the boolean value indicating whether the user is currently adding a geofence.
     *
     * @return True if a geofence is being added, false otherwise.
     */
    public boolean getAddingGeofenceAsBool() {
        return addingGeofence.getValue();
    }

    /**
     * Handles the press of the geofence button, toggling the state of adding geofence.
     */
    public void onGeofenceButtonPressed() {
        if (addingGeofence.getValue() != null) {
            addingGeofence.setValue(!(addingGeofence.getValue()));
        }
        geofenceFirstPressed = false;
    }

    /**
     * Sets the value indicating whether location updates are being requested.
     *
     * @param requestingLocationUpdates True if location updates are being requested, false otherwise.
     */
    public void setRequestingLocationUpdates(boolean requestingLocationUpdates) {
        this.requestingLocationUpdates = requestingLocationUpdates;
    }

    /**
     * Sets the value indicating whether the geofence button was pressed for the first time.
     *
     * @param geofenceFirstPressed True if the geofence button was pressed for the first time, false otherwise.
     */
    public void setGeofenceFirstPressed(boolean geofenceFirstPressed) {
        this.geofenceFirstPressed = geofenceFirstPressed;
    }
}
