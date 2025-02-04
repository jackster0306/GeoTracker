package com.psyjg14.coursework2.model;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.view.ManageCurrentJourney;

import java.util.ArrayList;
import java.util.List;

/**
 * LocationService is responsible for tracking the user's location and managing location updates.
 * It provides functionality for starting, stopping, and updating location tracking, as well as handling
 * tasks when the app is closed or removed from the recent apps list.
 */
public class LocationService extends Service {
    private static final String CHANNEL_ID = "PlayingChannel";

    private static final int NOTIFICATION_ID = 1;

    private final IBinder binder = new LocalBinder();
    private static final String TAG = "COMP3018";

    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MyLocationCallback myLocationCallback;

    private boolean isUpdating = false;

    private float distanceTravelled = 0;
    private long startTime;
    private long endTime;

    private String type;
    private List<LatLng> path = new ArrayList<>();

    private Notification notification;

    private int locationPriority = Priority.PRIORITY_HIGH_ACCURACY;

    private int updateDistance = 50;

    private boolean trackWhenClosed = true;

    private DatabaseRepository databaseRepository;

    private String name;

    /**
     * Callback interface for location-related events.
     */
    public interface MyLocationCallback {
        /**
         * Called when the device's location is changed.
         *
         * @param location The updated location.
         */
        void onLocationChanged(Location location);

        /**
         * Called when a journey is stopped, providing journey details.
         *
         * @param distanceTravelled Total distance traveled during the journey.
         * @param startTime         Start time of the journey.
         * @param endTime           End time of the journey.
         * @param type              Type of the journey.
         * @param path              List of LatLng points representing the journey path.
         */
        void onStoppedJourney(float distanceTravelled, long startTime, long endTime, String type, List<LatLng> path);
    }


    /**
     * Called when binding to the service. Returns the binder object.
     *
     * @param intent The intent used to bind.
     * @return The binder instance.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Binder class for providing access to the service.
     */
    public class LocalBinder extends Binder {
        /**
         * Gets the instance of the LocationService.
         *
         * @return The LocationService instance.
         */
        public LocationService getService() {
            return LocationService.this;
        }
    }

    /**
     * Sets the callback for location-related events.
     *
     * @param callback The callback instance.
     */
    public void setCallback(MyLocationCallback callback) {
        myLocationCallback = callback;
    }

    /**
     * Called when the service is created. Initializes necessary components.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        databaseRepository = new DatabaseRepository(getApplication());
        initLocationCallback();
        createNotificationChannel();
    }

    /**
     * Creates a notification channel for the service.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Player Service";
            String description = "Used for playing music";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Called when the service is started. Handles the "stop" action if provided in the intent.
     *
     * @param intent  The intent passed to the service.
     * @param flags   Additional data about this start request.
     * @param startId A unique integer representing this specific request to start.
     * @return The service's behavior upon startup.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            if(intent.getAction() != null){
                if(intent.getAction().equals("stop")){
                    isUpdating = false;
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    endTime = System.currentTimeMillis();
                    MovementEntity movementEntity = new MovementEntity();
                    movementEntity.movementName = MyTypeConverters.nameToDatabaseName(name);
                    movementEntity.movementType = type.toLowerCase();
                    movementEntity.distanceTravelled = distanceTravelled;
                    movementEntity.timeStamp = endTime;
                    movementEntity.timeTaken = endTime - startTime;
                    movementEntity.path = path;
                    movementEntity.note = "Journey was stopped through a notification";
                    databaseRepository.insertMovement(movementEntity);
                    stopForeground(true);
                    stopSelf();
                }
            }
        }

        startLocationUpdates(createLocationRequest());

        return START_STICKY;
    }

    /**
     * Initializes the location callback for receiving location updates.
     */
    private void initLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() != null) {
                    myLocationCallback.onLocationChanged(locationResult.getLastLocation());
                    if(isUpdating){
                        updateData(locationResult.getLastLocation());
                    }
                }
            }
        };
    }

    /**
     * Starts location updates with the provided location request.
     *
     * @param locationRequest The location request.
     */
    public void startLocationUpdates(LocationRequest locationRequest) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            Log.e(TAG, "Location permission not granted");
        }
    }

    /**
     * Stops location updates and notifies the callback with journey details.
     */
    public void stopLocationUpdates() {
        isUpdating = false;
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        endTime = System.currentTimeMillis();
        stopForeground(true);
        if (myLocationCallback != null) {
            myLocationCallback.onStoppedJourney(distanceTravelled, startTime, endTime, type, path);
        }
    }

    /**
     * Creates a location request with priority and update distance specified in settings.
     *
     * @return The created location request.
     */
    public LocationRequest createLocationRequest() {
        return new LocationRequest.Builder(0)
                .setPriority(locationPriority)
                .setMinUpdateDistanceMeters(updateDistance)
                .build();
    }

    /**
     * Starts tracking a journey with the specified type.
     *
     * @param type The type of the journey.
     */
    public void startTracking(String type){
        Intent notificationIntent = new Intent(this, ManageCurrentJourney.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Tracking Journey")
                .setContentText("Tracking your current " + type)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_background, "Stop Tracking", getStopServicePendingIntent())
                .build();
        startForeground(NOTIFICATION_ID, notification);
        startTime = System.currentTimeMillis();
        isUpdating = true;
        this.type = type;
        //startTime = System.currentTimeMillis();
    }

    /**
     * Gets the pending intent for stopping the service.
     *
     * @return The pending intent.
     */
    private PendingIntent getStopServicePendingIntent(){
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction("stop");
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_MUTABLE);
    }

    /**
     * Updates the journey data with the new location.
     *
     * @param newLocation The new location.
     */
    private void updateData(Location newLocation) {
        if(path.size() == 1){
            startTime = newLocation.getTime();
            path.add(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
        } else if(path.size() > 1){
            path.add(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
            int pathSize = path.size() -1;
            float[] results = new float[1];
            Location.distanceBetween(path.get(pathSize - 1).latitude, path.get(pathSize - 1).longitude, path.get(pathSize).latitude, path.get(pathSize).longitude, results);
            distanceTravelled += results[0];
            endTime = newLocation.getTime();
        } else{
            path.add(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
        }
    }

    /**
     * Checks if the service is currently updating the location.
     *
     * @return True if updating, false otherwise.
     */
    public boolean getIsUpdating(){
        return isUpdating;
    }

    /**
     * Sets up location parameters based on the provided settings.
     *
     * @param locationPriorityString The location priority setting.
     * @param updateDistanceString   The update distance setting.
     * @param trackWhenClosedString  The track when closed setting.
     */
    public void setupLocation(String locationPriorityString, String updateDistanceString, String trackWhenClosedString){
        if (locationPriorityString.equals("high")){
            locationPriority = Priority.PRIORITY_HIGH_ACCURACY;
        } else if(locationPriorityString.equals("balanced")){
            locationPriority = Priority.PRIORITY_BALANCED_POWER_ACCURACY;
        } else{
            locationPriority = Priority.PRIORITY_LOW_POWER;
        }

        if(updateDistanceString.equals("low")){
            updateDistance = 10;
        } else if(updateDistanceString.equals("medium")){
            updateDistance = 25;
        } else{
            updateDistance = 50;
        }

        if(trackWhenClosedString.equals("track")){
            trackWhenClosed = true;
        } else{
            trackWhenClosed = false;
        }

    }

    /**
     * Called when the service is removed from recent tasks (fully closed).
     *
     * @param rootIntent The original root intent that was used to launch the task.
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if(!trackWhenClosed){
            MovementEntity movementEntity = new MovementEntity();
            movementEntity.movementName = MyTypeConverters.nameToDatabaseName(name);
            movementEntity.movementType = type.toLowerCase();
            movementEntity.distanceTravelled = distanceTravelled;
            movementEntity.timeStamp = endTime;
            movementEntity.timeTaken = endTime - startTime;
            movementEntity.path = path;
            movementEntity.note = "Journey was stopped when the app was closed";
            databaseRepository.insertMovement(movementEntity);
            stopLocationUpdates();
            stopSelf();
            super.onTaskRemoved(rootIntent);
        }
    }

    /**
     * Sets the name for the current journey.
     *
     * @param newName The new name for the journey.
     */
    public void setName(String newName){
        name = newName;
    }
}
