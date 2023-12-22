package com.psyjg14.coursework2.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
class LocationDataHolder {
    private static LocationDataHolder instance;

    private Location lastLocation;
    private float distanceTravelled;
    private long startTime;
    private long endTime;
    private List<LatLng> path;

    private LocationDataHolder() {
        path = new ArrayList<>();
    }

    public static synchronized LocationDataHolder getInstance() {
        if (instance == null) {
            instance = new LocationDataHolder();
        }
        return instance;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public float getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(float distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public List<LatLng> getPath() {
        return path;
    }

    public void setPath(List<LatLng> path) {
        this.path = path;
    }
    // Add getters and setters for other variables (startTime, endTime, path)
}


public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATES = "com.psyjg14.coursework2.PROCESS_UPDATES";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null && intent.getAction() != null){
                if (intent.getAction().equals(ACTION_PROCESS_UPDATES)) {
                    LocationResult result = LocationResult.extractResult(intent);
                    if (result != null) {
                        List<Location> locations = result.getLocations();
                        if (!locations.isEmpty()) {
                            // Handle the received location updates
                            Log.d("COMP3018", "******Location Update Received in BroadcastReceiver: *********, Location: " + locations);
                            handleLocationUpdate(locations.get(0));
                        }
                    }
                }
        }
    }


    private void handleLocationUpdate(Location newLocation) {
        LocationDataHolder dataHolder = LocationDataHolder.getInstance();
        List<LatLng> path = dataHolder.getPath();
        float distanceTravelled = dataHolder.getDistanceTravelled();
        Log.e("COMP3018", "******Location Update Received in BroadcastReceiver: *********, Location: " + newLocation);
        // Implement your logic to handle the received location update
        // You can update UI, calculate distance, or perform other actions
        if(path.size() == 1){
            //startTime = newLocation.getTime();
            path.add(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
            path.add(new LatLng(21, 21));
        } else if(path.size() > 1){
            path.add(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
            int pathSize = path.size() -1;
            float[] results = new float[1];
            Location.distanceBetween(path.get(pathSize - 1).latitude, path.get(pathSize - 1).longitude, path.get(pathSize).latitude, path.get(pathSize).longitude, results);
            distanceTravelled += results[0];
            //endTime = newLocation.getTime();
        } else{
            path.add(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
        }
        Log.d("COMP3018", "Path Size: " + path.size());
        Log.d("COMP3018", "Path: " + path);
        Log.d("COMP3018", "Distance Travelled: " + distanceTravelled);
        dataHolder.setLastLocation(newLocation);
        dataHolder.setDistanceTravelled(distanceTravelled);
        dataHolder.setPath(path);
    }
}
