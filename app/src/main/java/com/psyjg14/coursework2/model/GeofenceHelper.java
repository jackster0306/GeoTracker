package com.psyjg14.coursework2.model;

import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

public class GeofenceHelper {
    public GeofenceHelper() {
    }

    public Geofence createGeofence(LatLng latLng, float radius, int transitionTypes) {
        Log.d("COMP3018", "Latitude: " + latLng.latitude + ", Longitude: " + latLng.longitude + ", Radius: " + radius + ", Transition Types: " + transitionTypes);
        return new Geofence.Builder()
                .setRequestId(generateRequestId())
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(transitionTypes)
                .build();
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}
