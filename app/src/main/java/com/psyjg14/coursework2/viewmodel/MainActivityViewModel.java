package com.psyjg14.coursework2.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;


public class MainActivityViewModel extends ViewModel {
        private Location lastLocation;
        private MutableLiveData<Boolean> addingGeofence = new MutableLiveData<>();
        private boolean requestingLocationUpdates = false;
        private Marker geofenceMarker;

        private boolean geofenceFirstPressed = true;

        private boolean isBound = false;

        private float totalDistance = 0.0f;
        private long startTime = 0;
        private long endTime = 0;

    private final List<LatLng> path = new ArrayList<>();

    List<GeofenceEntity> geofences = new ArrayList<>();


    public List<GeofenceEntity> getGeofences() {
        return geofences;
    }

    public void setGeofences(List<GeofenceEntity> geofences) {
        this.geofences = geofences;
    }

    public void addGeofenceToList(GeofenceEntity geofence) {
        geofences.add(geofence);
    }

    public void removeGeofenceFromList(GeofenceEntity geofence) {
        geofences.remove(geofence);
    }

    public List<LatLng> getPath() {
        return path;
    }

    public void addLatLngToPath(LatLng latLng) {
        path.add(latLng);
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

        public boolean getAddingGeofenceAsBool() {
                return addingGeofence.getValue();
        }

        public void onGeofenceButtonPressed() {
            if(addingGeofence.getValue() != null) {
                addingGeofence.setValue(!(addingGeofence.getValue()));
            }
            geofenceFirstPressed = false;
            geofenceMarker = null;
        }

        public boolean getRequestingLocationUpdates() {
                return requestingLocationUpdates;
        }

        public void setRequestingLocationUpdates(boolean requestingLocationUpdates) {
                this.requestingLocationUpdates = requestingLocationUpdates;
        }


    /**
     * Getter for the flag indicating whether the service is bound.
     *
     * @return boolean representing whether the service is bound.
     */
    public boolean getIsBound() {
        return isBound;
    }

    /**
     * Setter for the flag indicating whether the service is bound.
     *
     * @param isBound The new value for the isBound flag.
     */
    public void setIsBound(boolean isBound) {
        this.isBound = isBound;
    }


    public void setLastLocation(Location lastLocation) {
                this.lastLocation = lastLocation;
        }

        public Location getLastLocation() {
                return lastLocation;
        }

        public void setTotalDistance(float totalDistance) {
                this.totalDistance = totalDistance;
        }

        public float getTotalDistance() {
                return totalDistance;
        }

        public void setStartTime(long startTime) {
                this.startTime = startTime;
        }

        public long getStartTime() {
                return startTime;
        }

        public void setEndTime(long endTime) {
                this.endTime = endTime;
        }

        public long getEndTime() {
                return endTime;
        }
}
