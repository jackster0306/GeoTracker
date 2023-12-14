package com.psyjg14.coursework2;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private boolean  requestingLocationUpdates = false;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap map;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    private LocationCallback locationCallback;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        createLocationRequest();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("LOCATION", "ONLOCATION RESULT");
                if (locationResult == null) {
                    Log.d("LOCATION", "locationResult is null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    Log.d("LOCATION", "In For Callback Location: " + location);
                    currentLocation = location;
                    LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    map.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                    map.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                }
            }
        };
    }

    private void getLastLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d("LOCATION", "Location found: " + location);
                        currentLocation = location;

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(MainActivity.this);
                    } else {
                        Log.d("LOCATION", "Location is null");
                        // Check emulator settings, permissions, etc.
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("LOCATION", "Error getting location", e);
                    // Log the exception for more information.
                }
            });
        } else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("MAP", "Map is ready");
        map = googleMap;

        /*
                // Add a marker in Nottingham and move the camera
        LatLng sydney = new LatLng(52.95, -1.15);
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        MarkerOptions options = new MarkerOptions().position(sydney).title("Marker in Nottingham");
        //Change colour of marker
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        map.addMarker(options);

         */
        //getLastLocation();
        Log.d("LOCATION", "Location: " + currentLocation);
        if(currentLocation != null){
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            map.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
            map.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        } else{
            LatLng sydney = new LatLng(52.95, -1.15);
            map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            MarkerOptions options = new MarkerOptions().position(sydney).title("Marker in Nottingham");
            //Change colour of marker
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            map.addMarker(options);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
                requestingLocationUpdates = true;
            }else{
                Toast.makeText(this, "Permission denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }


    protected void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(4000)
                .setIntervalMillis(10000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...

                requestingLocationUpdates = true;
                Log.d("LOCATION", "Location settings satisfied");
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOCATION", "Location settings not satisfied");
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            Log.d("LOCATION", "Requesting location updates");
            startLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        Log.d("LOCATION", "Starting location updates");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    protected void onPause() {
        Log.d("LOCATION", "Pausing location updates");
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        Log.d("LOCATION", "Stopping location updates");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }





}

