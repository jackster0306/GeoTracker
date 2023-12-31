package com.psyjg14.coursework2.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;


public class MainActivityViewModel extends ViewModel {
        private MutableLiveData<Boolean> addingGeofence = new MutableLiveData<>();
        private boolean requestingLocationUpdates = false;
        private Marker geofenceMarker;

        private boolean geofenceFirstPressed = true;


        private final List<LatLng> path = new ArrayList<>();

        List<GeofenceEntity> geofences = new ArrayList<>();


        public List<GeofenceEntity> getGeofences() {
            return geofences;
        }

        public void setGeofences(List<GeofenceEntity> geofences) {
            this.geofences = geofences;
        }

        public List<LatLng> getPath() {
            return path;
        }


        public boolean getGeofenceFirstPressed() {
                return geofenceFirstPressed;
        }

        public MainActivityViewModel() {
                addingGeofence.setValue(false);
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
}
