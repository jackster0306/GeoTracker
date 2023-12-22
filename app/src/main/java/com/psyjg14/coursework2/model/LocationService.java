package com.psyjg14.coursework2.model;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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

    private Location lastLocation;  // Track the last location
    private float distanceTravelled = 0;  // Track the total distance traveled
    private long startTime;  // Track the start time
    private long endTime;  // Track the end time
    private String name;
    private String type;
    private List<LatLng> path = new ArrayList<>();

    private Notification notification;

    public interface MyLocationCallback {
        void onLocationChanged(Location location);

        void onStoppedJourney(float distanceTravelled, long startTime, long endTime, String name, String type, List<LatLng> path);
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

        //PendingIntent pendingIntent = getLocationPendingIntent();

//        new Thread(()-> {
            startLocationUpdates(createLocationRequest());
//        }).start();




//        while(isUpdating){
//            try {
//                Log.d(TAG, "*************************IN WHILE*************************");
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }


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
            //fusedLocationProviderClient.requestLocationUpdates(locationRequest, pendingIntent);
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
            myLocationCallback.onStoppedJourney(distanceTravelled, startTime, endTime, name, type, path);
        }
    }

    public void getLastLocation() {
        Log.d(TAG, "getLastLocation: LocationService");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Log.d(TAG, "Last Location: " + location);
                    myLocationCallback.onLocationChanged(location);
                } else {
                    Log.d(TAG, "Last Location: null");
                }
            });
        } else {
            Log.e(TAG, "Location permission not granted");
        }
    }

    public LocationRequest createLocationRequest() {
        Log.d(TAG, "createLocationRequest: LocationService");
        return new LocationRequest.Builder(0)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateDistanceMeters(50)
                .build();
    }

    public Location getCurrentLocation() {
        Log.d(TAG, "getCurrentLocation: LocationService");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return fusedLocationProviderClient.getLastLocation().getResult();
        }
        else{
            return null;
        }

    }

    public void startTracking(String type){
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Player Service")
                .setContentText("Playing selected music")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();
        startForeground(NOTIFICATION_ID, notification);
        Log.d(TAG, "startTracking: LocationService");
        startTime = System.currentTimeMillis();
        isUpdating = true;
        this.type = type;
        //startTime = System.currentTimeMillis();
    }

    private void updateData(Location newLocation) {
        Log.d(TAG, "updateData: LocationService");
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
        lastLocation = newLocation;
    }

    public boolean getIsUpdating(){
        Log.d(TAG, "getIsUpdating: LocationService");
        return isUpdating;
    }


//    private PendingIntent getLocationPendingIntent() {
//        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
//        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
//        intent.putExtra("name", name);
//        intent.putExtra("type", type);
//        intent.putExtra("startTime", startTime);
//        intent.putExtra("endTime", endTime);
//        intent.putExtra("distanceTravelled", distanceTravelled);
//        intent.putExtra("path", (ArrayList<LatLng>) path);
//
//
//
//        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);
//    }
}
