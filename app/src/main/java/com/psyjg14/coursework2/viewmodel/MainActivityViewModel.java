package com.psyjg14.coursework2.viewmodel;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.model.DatabaseRepository;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;


public class MainActivityViewModel extends AndroidViewModel {
        private MutableLiveData<Boolean> addingGeofence = new MutableLiveData<>();
        private boolean requestingLocationUpdates = false;
        private Marker geofenceMarker;

        private boolean geofenceFirstPressed = true;


        private final List<LatLng> path = new ArrayList<>();

        private DatabaseRepository databaseRepository;
        private LiveData<List<GeofenceEntity>> geofencesLiveData;

        public MainActivityViewModel(Application application) {
            super(application);
            databaseRepository = new DatabaseRepository(application);
            geofencesLiveData = databaseRepository.getAllGeofences();
            addingGeofence.setValue(false);
        }

        public void updateGeofence(GeofenceEntity geofenceEntity) {
            databaseRepository.updateGeofence(geofenceEntity);
        }

        public LiveData<List<GeofenceEntity>> getAllGeofences() {
            return geofencesLiveData;
        }

        public List<LatLng> getPath() {
            return path;
        }


        public boolean getGeofenceFirstPressed() {
                return geofenceFirstPressed;
        }


        public void setGeofenceMarker(Marker geofenceMarker) {
            this.geofenceMarker = geofenceMarker;
        }

        public void removeGeofenceMarker() {
            geofenceMarker.remove();
        }

        public Marker getGeofenceMarker() {
                return geofenceMarker;
        }


        public LiveData<Boolean> getAddingGeofence() {
            return addingGeofence;
        }
        public void setAddingGeofence(boolean value) {
            addingGeofence.setValue(value);
         }

        public boolean getAddingGeofenceAsBool() {
                return addingGeofence.getValue();
        }

        public void onGeofenceButtonPressed() {
            if(addingGeofence.getValue() != null) {
                addingGeofence.setValue(!(addingGeofence.getValue()));
            }
            geofenceFirstPressed = false;
            //geofenceMarker = null;
        }


        public void setRequestingLocationUpdates(boolean requestingLocationUpdates) {
                this.requestingLocationUpdates = requestingLocationUpdates;
        }

    public void setGeofenceFirstPressed(boolean geofenceFirstPressed) {
        this.geofenceFirstPressed = geofenceFirstPressed;
    }
}
