package com.psyjg14.coursework2.model;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;

import java.util.List;
import java.util.UUID;

/**
 * The GeofenceHelper class provides methods to interact with geofences.
 * It uses the GeofencingClient to manage geofences and the GeofenceBroadcastReceiver to handle geofence events.
 */
public class GeofenceHelper {
    private final Context context;
    private final GeofencingClient geofencingClient;
    private final Activity activity;

    private DatabaseRepository databaseRepository;

    private List<GeofenceEntity> geofences;

    /**
     * Constructs a new GeofenceHelper instance.
     *
     * @param context  The application context.
     * @param activity The activity associated with the GeofenceHelper.
     */
    public GeofenceHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        geofencingClient = com.google.android.gms.location.LocationServices.getGeofencingClient(context);
        databaseRepository = new DatabaseRepository((Application) context.getApplicationContext());
        databaseRepository.getAllGeofences().observe((LifecycleOwner) activity, geofenceEntities -> {
            geofences = geofenceEntities;
        });
    }

    /**
     * Creates a new Geofence instance with the specified parameters.
     *
     * @param geofenceID      The unique identifier for the geofence.
     * @param latLng          The geographical coordinates (latitude and longitude) of the geofence center.
     * @param radius          The radius of the geofence, in meters.
     * @param transitionTypes The types of geofence transitions to monitor (enter, exit, or both).
     * @return A Geofence instance.
     */
    private Geofence createGeofence(String geofenceID, LatLng latLng, float radius, int transitionTypes) {
        return new Geofence.Builder()
                .setRequestId(geofenceID)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(transitionTypes)
                .build();
    }

    /**
     * Generates a unique request ID for a new geofence.
     *
     * @return A unique geofence request ID.
     */
    private String generateRequestId() {
        String id = UUID.randomUUID().toString();
        for (GeofenceEntity geofence : geofences) {
            if (geofence.geofenceID.equals(id)) {
                generateRequestId();
            }
        }
        return UUID.randomUUID().toString();
    }

    /**
     * Adds a new geofence with the specified parameters to the GeofencingClient and the local database.
     *
     * @param name             The name of the geofence.
     * @param classification   The classification of the geofence.
     * @param latLng           The geographical coordinates (latitude and longitude) of the geofence center.
     * @param radius           The radius of the geofence, in meters.
     * @param geofenceNote     The note associated with the geofence.
     */
    public void addNewGeofence(String name, String classification, LatLng latLng, float radius, String geofenceNote) {
        String geofenceID = generateRequestId();
        Geofence geofence = createGeofence(geofenceID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .build();
        PendingIntent pendingIntent = getGeofencePendingIntent();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                        .addOnSuccessListener(activity, aVoid -> {
                            GeofenceEntity geofenceEntity = new GeofenceEntity();
                            geofenceEntity.geofenceID = geofenceID;
                            geofenceEntity.latitude = latLng.latitude;
                            geofenceEntity.longitude = latLng.longitude;
                            geofenceEntity.radius = radius;
                            geofenceEntity.reminderMessage = "Reminder Message";
                            geofenceEntity.transitionType = Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT;
                            geofenceEntity.name = name;
                            geofenceEntity.classification = classification;
                            geofenceEntity.geofenceNote = geofenceNote;

                            databaseRepository.insertGeofence(geofenceEntity);
                        })
                        .addOnFailureListener(activity, e -> Log.d("COMP3018", "onFailure: " + e.getMessage()));
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
            }
        }
    }

    /**
     * Adds a geofence with the specified parameters to the GeofencingClient.
     *
     * @param geofenceID The unique identifier for the geofence.
     * @param latLng     The geographical coordinates (latitude and longitude) of the geofence center.
     * @param radius     The radius of the geofence, in meters.
     */
    public void addGeofence(String geofenceID, LatLng latLng, float radius) {
        Geofence geofence = createGeofence(geofenceID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .build();
        PendingIntent pendingIntent = getGeofencePendingIntent();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                        .addOnSuccessListener(activity, aVoid -> {
                        })
                        .addOnFailureListener(activity, e -> Log.d("COMP3018", "onFailure: " + e.getMessage()));
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
            }
        }
    }

    /**
     * Retrieves the PendingIntent for geofence events.
     *
     * @return A PendingIntent for geofence events.
     */
    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);
        return pendingIntent;
    }

    /**
     * Removes the specified geofence from the database.
     *
     * @param geofence The geofence to be removed.
     */
    public void removeGeofence(GeofenceEntity geofence) {
        DatabaseRepository databaseRepository = new DatabaseRepository((Application) context.getApplicationContext());
        databaseRepository.deleteGeofence(geofence);
    }
}
