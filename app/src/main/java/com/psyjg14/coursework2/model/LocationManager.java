package com.psyjg14.coursework2.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.security.Permission;

public class LocationManager {
    private static final String TAG = "COMP3018";

    private Context context;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationManagerCallback locationManagerCallback;

    public interface LocationManagerCallback {
        void onLocationChanged(Location location);
    }

    public LocationManager(Context context, LocationManagerCallback callback) {
        this.context = context;
        locationManagerCallback = callback;
        initLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    private void initLocationCallback() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() != null) {
                    Log.d(TAG, "Location Updated: " + locationResult.getLastLocation());
                    locationManagerCallback.onLocationChanged(locationResult.getLastLocation());
                }
            }
        };
    }

    public void requestLocationUpdates(LocationRequest locationRequest){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            Log.e(TAG, "Location permission not granted");
        }
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void getLastLocation(){
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Log.d(TAG, "Last Location: " + location);
                    locationManagerCallback.onLocationChanged(location);
                }else{
                    Log.d(TAG, "Last Location: null");
                }
            });
        } else {
            Log.e(TAG, "Location permission not granted");
        }
    }

    public LocationRequest createLocationRequest(){
        return new LocationRequest.Builder(4000)
                .setIntervalMillis(10000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();
    }

}
