package com.psyjg14.coursework2.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.psyjg14.coursework2.database.AppDatabase;
import com.psyjg14.coursework2.database.dao.GeofenceDao;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;

import java.util.List;
import java.util.UUID;

public class GeofenceHelper {
    private Context context;
    private GeofencingClient geofencingClient;
    private Activity activity;
    public GeofenceHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        geofencingClient = com.google.android.gms.location.LocationServices.getGeofencingClient(context);
    }

    private Geofence createGeofence(String name, String classification, LatLng latLng, float radius, int transitionTypes) {
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

    /**************************************************************************
     *  Geofence code
     **************************************************************************/

    public void addGeofence(String name, String classification, LatLng latLng, float radius, onGeofenceAddedCallback callback) {
        Geofence geofence = createGeofence(name, classification,latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .build();
        PendingIntent pendingIntent = getGeofencePendingIntent();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
                geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                        .addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                            @SuppressLint("StaticFieldLeak")
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("COMP3018", "onSuccess: Geofence Added...");
                                Log.d("COMP3018", "onSuccess: Adding circle");
                                GeofenceEntity geofenceEntity = new GeofenceEntity();
                                geofenceEntity.latitude = latLng.latitude;
                                geofenceEntity.longitude = latLng.longitude;
                                geofenceEntity.radius = radius;
                                geofenceEntity.reminderMessage = "Reminder Message";
                                geofenceEntity.transitionType = Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT;
                                geofenceEntity.name = name;
                                geofenceEntity.classification = classification;
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        AppDatabase db = Room.databaseBuilder(context.getApplicationContext(),
                                                AppDatabase.class, "MDPDatabase").build();

                                        GeofenceDao geofenceDao = db.geofenceDao();
                                        geofenceDao.insertGeofence(geofenceEntity);
                                        if (callback != null) {
                                            callback.onGeofenceAdded();
                                        }
                                        return null;
                                    }
                                }.execute();
                            }
                        })
                        .addOnFailureListener(activity, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("COMP3018", "onFailure: " + e.getMessage());
                            }
                        });
            } else{
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
            }

        }

    }

    private PendingIntent getGeofencePendingIntent() {
        Log.d("COMP3018", "getGeofencePendingIntent: ");
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);
        return pendingIntent;
    }

    @SuppressLint("StaticFieldLeak")
    public void getGeofences(GetGeofenceCallback callback) {
        new AsyncTask<Void, Void, List<GeofenceEntity>>() {
            @Override
            protected List<GeofenceEntity> doInBackground(Void... voids) {
                AppDatabase db = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "MDPDatabase").build();

                GeofenceDao geofenceDao = db.geofenceDao();
                return geofenceDao.getAllGeofences();
            }

            @Override
            protected void onPostExecute(List<GeofenceEntity> geofences) {
                super.onPostExecute(geofences);
                callback.GeofenceCallback(geofences);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void removeGeofence(GeofenceEntity geofence, onGeofenceRemovedCallback callback){
        new AsyncTask<Void, Void, List<GeofenceEntity>>() {
            @Override
            protected List<GeofenceEntity> doInBackground(Void... voids) {
                AppDatabase db = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "MDPDatabase").build();

                GeofenceDao geofenceDao = db.geofenceDao();
                geofenceDao.deleteGeofence(geofence);
                return geofenceDao.getAllGeofences();
            }

            @Override
            protected void onPostExecute(List<GeofenceEntity> geofences) {
                super.onPostExecute(geofences);
                callback.onGeofenceRemoved(geofences);
            }
        }.execute();
    }


    public interface GetGeofenceCallback{
        void GeofenceCallback(List<GeofenceEntity> geofences);
    }

    public interface onGeofenceAddedCallback{
        void onGeofenceAdded();
    }

    public interface onGeofenceRemovedCallback{
        void onGeofenceRemoved(List<GeofenceEntity> geofences);
    }
}
