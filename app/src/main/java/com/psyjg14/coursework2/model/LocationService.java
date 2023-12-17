package com.psyjg14.coursework2.model;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationService extends Service {
    private final IBinder binder = new LocalBinder();
    private static final String TAG = "COMP3018";

    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MyLocationCallback myLocationCallback;

    private boolean isUpdating = false;

    public interface MyLocationCallback {
        void onLocationChanged(Location location);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }


    public void setCallback(MyLocationCallback callback) {
        myLocationCallback = callback;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: LocationService");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        initLocationCallback();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startLocationUpdates(createLocationRequest());


        return START_STICKY;
    }

    private void initLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() != null) {
                    Log.d(TAG, "Location Updated: " + locationResult.getLastLocation());
                    myLocationCallback.onLocationChanged(locationResult.getLastLocation());
                }
            }
        };
    }

    public void startLocationUpdates(LocationRequest locationRequest) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            Log.e(TAG, "Location permission not granted");
        }
    }

    public void stopLocationUpdates() {
        isUpdating = false;
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void getLastLocation() {
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
        return new LocationRequest.Builder(0)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateDistanceMeters(50)
                .build();
    }

    public Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return fusedLocationProviderClient.getLastLocation().getResult();
        }
        else{
            return null;
        }
    }

}
