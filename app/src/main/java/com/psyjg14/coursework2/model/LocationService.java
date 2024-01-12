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
import androidx.lifecycle.LiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.psyjg14.coursework2.MyTypeConverters;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.view.MainActivity;
import com.psyjg14.coursework2.view.ManageCurrentJourney;

import java.util.ArrayList;
import java.util.List;

// DOES NOT UPDATE IN BACKGROUND NEED TO FIX THIS - ISSUES WITH PATH AND DISTANCE TRAVELLED. I BROKE EVERYTHING
public class LocationService extends Service {
    private static final String CHANNEL_ID = "PlayingChannel";

    // Notification ID
    private static final int NOTIFICATION_ID = 1;


    private final IBinder binder = new LocalBinder();
    private static final String TAG = "COMP3018";

    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MyLocationCallback myLocationCallback;

    private boolean isUpdating = false;

    // Track the last location
    private float distanceTravelled = 0;  // Track the total distance traveled
    private long startTime;  // Track the start time
    private long endTime;  // Track the end time

    private String type;
    private List<LatLng> path = new ArrayList<>();

    private Notification notification;

    private int locationPriority = Priority.PRIORITY_HIGH_ACCURACY;

    private int updateDistance = 50;

    private boolean trackWhenClosed = true;

    private DatabaseRepository databaseRepository;

    private String name;

    public interface MyLocationCallback {
        void onLocationChanged(Location location);

        void onStoppedJourney(float distanceTravelled, long startTime, long endTime, String type, List<LatLng> path);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: LocationService");
        return binder;
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }


    public void setCallback(MyLocationCallback callback) {
        Log.d(TAG, "setCallback: LocationService");
        myLocationCallback = callback;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: LocationService");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        databaseRepository = new DatabaseRepository(getApplication());
        initLocationCallback();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        Log.d(TAG, "Creating notification channel.");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Player Service";
            String description = "Used for playing music";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel creation complete.");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "*************************onStartCommand: LocationService*************************");
        if(intent != null){
            if(intent.getAction() != null){
                if(intent.getAction().equals("stop")){
                    Log.d(TAG, "onStartCommand: STOP, name: "+name);
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

    private void initLocationCallback() {
        Log.d(TAG, "initLocationCallback: LocationService");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() != null) {
                    Log.e(TAG, "******Location Callback Sent: *********, Location Updated: " + locationResult.getLastLocation());
                    myLocationCallback.onLocationChanged(locationResult.getLastLocation());
                    if(isUpdating){
                        Log.e(TAG, "*******UPDATING DATA**********");
                        updateData(locationResult.getLastLocation());
                    }
                }
            }
        };
    }

    public void startLocationUpdates(LocationRequest locationRequest) {
        Log.d(TAG, "startLocationUpdates: LocationService");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            Log.e(TAG, "Location permission not granted");
        }
    }

    public void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates: LocationService");
        isUpdating = false;
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        endTime = System.currentTimeMillis();
        stopForeground(true);
        if (myLocationCallback != null) {
            myLocationCallback.onStoppedJourney(distanceTravelled, startTime, endTime, type, path);
        }
    }

    public LocationRequest createLocationRequest() {
        Log.d(TAG, "createLocationRequest: LocationService");
        Log.d(TAG, "Location Priority: " + locationPriority + " Update Distance: " + updateDistance);
        return new LocationRequest.Builder(0)
                .setPriority(locationPriority)
                .setMinUpdateDistanceMeters(updateDistance)
                .build();
    }

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
        Log.d(TAG, "startTracking: LocationService");
        startTime = System.currentTimeMillis();
        isUpdating = true;
        this.type = type;
        //startTime = System.currentTimeMillis();
    }

    private PendingIntent getStopServicePendingIntent(){
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction("stop");
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_MUTABLE);
    }

    private void updateData(Location newLocation) {
        Log.d(TAG, "updateData: LocationService");
        if(path.size() == 1){
            startTime = newLocation.getTime();
            Log.d(TAG, "Start Time: ");
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

    public boolean getIsUpdating(){
        Log.d(TAG, "getIsUpdating: LocationService");
        return isUpdating;
    }

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

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved: LocationService");
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

    public void setName(String newName){
        name = newName;
    }
}
