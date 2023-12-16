package com.psyjg14.coursework2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.View;
import android.widget.Toast;


import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.psyjg14.coursework2.databinding.ActivityMainBinding;
import com.psyjg14.coursework2.viewmodels.MainActivityViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private static final String TAG = "COMP3018";

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private final int FINE_PERMISSION_CODE = 1;
    private SearchView mapSearchView;

    private MainActivityViewModel mainActivityViewModel;

    private GoogleMap map;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    LocationRequest locationRequest;
    private GeofencingClient geofencingClient;

    private GeofenceHelper geofenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Data binding initialization
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        binding.setViewModel(mainActivityViewModel);
        binding.setLifecycleOwner(this);

        mapSearchView = binding.searchView;


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
                    if(map != null){
                        map.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                        //map.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                        Log.d("LOCATION", "Marker Set");
                    }
                }
            }
        };


        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper();


        // Observe the LiveData in the ViewModel
        mainActivityViewModel.getAddingGeofence().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean addingGeofence) {
                if(mainActivityViewModel.getAddingGeofenceAsBool()){
                    Toast.makeText(MainActivity.this, "Click on the map to add a geofence", Toast.LENGTH_SHORT).show();
                } else{
                    if(mainActivityViewModel.getGeofenceMarker() != null){
                        Toast.makeText(MainActivity.this, "Geofence added", Toast.LENGTH_SHORT).show();
                        addGeofence(mainActivityViewModel.getGeofenceMarker().getPosition(), 100);
                        CircleOptions circleOptions = new CircleOptions()
                                .center(mainActivityViewModel.getGeofenceMarker().getPosition())
                                .radius(100) // Set the geofence radius
                                .strokeColor(Color.RED) // Set the stroke color. Make it changable based on good/bad location reminders
                                .fillColor(Color.argb(70, 255, 0, 0)); // Set the fill color with alpha
                        map.addCircle(circleOptions);
                        Log.d("COMP3018", "setting geofence marker to null");

                    } else{
                        Log.d("COMP3018", "getFirstPressed: " + mainActivityViewModel.getGeofenceFirstPressed());
                        if(!mainActivityViewModel.getGeofenceFirstPressed()){
                            Toast.makeText(MainActivity.this, "No geofence added", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


    }

    /**************************************************************************
     *  Map code
     **************************************************************************/

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("MAP", "Map is ready");
        map = googleMap;

        Log.d("LOCATION", "Location: " + currentLocation);
        if (currentLocation != null) {
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            map.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
            map.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            addGeofence(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 100);
        }

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null) {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    map.addMarker(new MarkerOptions().position(latLng).title(location));
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SEARCH", "Query: " + newText);
                return false;
            }
        });

        map.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "Map clicked");
        if (mainActivityViewModel.getAddingGeofenceAsBool()) {
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            MarkerOptions options = new MarkerOptions().position(latLng).title("Geofence Location");
            //Change colour of marker
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            if(mainActivityViewModel.getGeofenceMarker() != null){
                mainActivityViewModel.removeGeofenceMarker();
            }
            mainActivityViewModel.setGeofenceMarker(map.addMarker(options));
        }
    }

    /**************************************************************************
     *  Location code
     **************************************************************************/


    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
                mainActivityViewModel.setRequestingLocationUpdates(true);
            } else {
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

                mainActivityViewModel.setRequestingLocationUpdates(true);
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
        if (mainActivityViewModel.getRequestingLocationUpdates()) {
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


    /**************************************************************************
     *  Geofence code
     **************************************************************************/

    private void addGeofence(LatLng latLng, float radius) {
        Geofence geofence = geofenceHelper.createGeofence(latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .build();
        PendingIntent pendingIntent = getGeofencePendingIntent();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
                geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                        .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("COMP3018", "onSuccess: Geofence Added...");
                                //if(mainActivityViewModel.getGeofenceMarker() != null){
                                    Log.d("COMP3018", "onSuccess: Adding circle");

                                    //mainActivityViewModel.setGeofenceMarker(null);
                               // }
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("COMP3018", "onFailure: " + e.getMessage());
                            }
                        });
            } else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, FINE_PERMISSION_CODE);
            }

        }

    }

    private PendingIntent getGeofencePendingIntent() {
        Log.d("COMP3018", "getGeofencePendingIntent: ");
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        return pendingIntent;
    }


    /**************************************************************************
     *  Button Press Codes
     **************************************************************************/
}

