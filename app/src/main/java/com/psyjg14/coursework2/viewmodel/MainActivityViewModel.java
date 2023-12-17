package com.psyjg14.coursework2.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.Marker;
import androidx.lifecycle.MutableLiveData;


public class MainActivityViewModel extends ViewModel {
        private MutableLiveData<Boolean> addingGeofence = new MutableLiveData<>();
        private boolean requestingLocationUpdates = false;
        private Marker geofenceMarker;

        private boolean geofenceFirstPressed = true;

        private boolean isBound = false;

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

}
