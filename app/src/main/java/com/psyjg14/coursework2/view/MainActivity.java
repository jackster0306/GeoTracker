package com.psyjg14.coursework2.view;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Path;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.view.View;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.psyjg14.coursework2.database.AppDatabase;
import com.psyjg14.coursework2.database.dao.GeofenceDao;
import com.psyjg14.coursework2.database.dao.MovementDao;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.model.GeofenceBroadcastReceiver;
import com.psyjg14.coursework2.model.GeofenceHelper;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.databinding.ActivityMainBinding;
import com.psyjg14.coursework2.model.LocationService;
import com.psyjg14.coursework2.viewmodel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener{
    private static final String TAG = "COMP3018";
    private final int FINE_PERMISSION_CODE = 1;
    private SearchView mapSearchView;
    private BottomNavigationView navBar;

    private MainActivityViewModel mainActivityViewModel;

    private GoogleMap map;
    private GeofencingClient geofencingClient;

    private GeofenceHelper geofenceHelper;

    private static final int GEOFENCE_RADIUS = 100;

    private LocationService locationService;


    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Data binding initialization
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        binding.setViewModel(mainActivityViewModel);
        binding.setLifecycleOwner(this);

        mapSearchView = binding.searchView;
        navBar = binding.bottomNavigation;

        navBar.setSelectedItemId(R.id.mapMenu);

        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.mapMenu){
                return true;
            } else if(itemId == R.id.statsMenu){
                Intent intent = new Intent(MainActivity.this, ViewDataActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if(itemId == R.id.settingsMenu){
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.manageMenu){
                Intent intent = new Intent(MainActivity.this, ManageCurrentJourney.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "MDPDatabase").build();

                GeofenceDao userDao = db.geofenceDao();
                mainActivityViewModel.setGeofences(userDao.getAllGeofences());
                return null;
            }
        }.execute();



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(!mainActivityViewModel.getIsBound()){
            Intent intent = new Intent(MainActivity.this, LocationService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        Log.d(TAG, "Starting service");
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        startService(intent);


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
                        addGeofence(mainActivityViewModel.getGeofenceMarker().getPosition(), GEOFENCE_RADIUS);
                        CircleOptions circleOptions = new CircleOptions()
                                .center(mainActivityViewModel.getGeofenceMarker().getPosition())
                                .radius(GEOFENCE_RADIUS) // Set the geofence radius
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

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, FINE_PERMISSION_CODE);
        }
    }


    /**************************************************************************
     *  Map code
     **************************************************************************/

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("MAP", "Map is ready");
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
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
        for(GeofenceEntity geofence: mainActivityViewModel.getGeofences()){
            LatLng latLng = new LatLng(geofence.latitude, geofence.longitude);
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(geofence.radius) // Set the geofence radius
                    .strokeColor(Color.RED) // Set the stroke color. Make it changable based on good/bad location reminders
                    .fillColor(Color.argb(70, 255, 0, 0)); // Set the fill color with alpha
            map.addCircle(circleOptions);
        }

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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationService.getLastLocation();
                mainActivityViewModel.setRequestingLocationUpdates(true);
            } else {
                Toast.makeText(this, "Permission denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
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
                            @SuppressLint("StaticFieldLeak")
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("COMP3018", "onSuccess: Geofence Added...");
                                //if(mainActivityViewModel.getGeofenceMarker() != null){
                                    Log.d("COMP3018", "onSuccess: Adding circle");
                                GeofenceEntity geofenceEntity = new GeofenceEntity();
                                geofenceEntity.latitude = latLng.latitude;
                                geofenceEntity.longitude = latLng.longitude;
                                geofenceEntity.radius = radius;
                                geofenceEntity.reminderMessage = "Reminder Message";
                                geofenceEntity.transitionType = Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT;
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                                                AppDatabase.class, "MDPDatabase").build();

                                        GeofenceDao geofenceDao = db.geofenceDao();
                                        geofenceDao.insertGeofence(geofenceEntity);
                                        return null;
                                    }
                                }.execute();

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
     *  Location Service code
     **************************************************************************/



    private ServiceConnection connection = new ServiceConnection() {

        // Called when a client binds to the service
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            locationService = binder.getService();
            Log.d(TAG, "isBound set to true");
            mainActivityViewModel.setIsBound(true);

            locationService.setCallback(new LocationService.MyLocationCallback() {
                @Override
                public void onLocationChanged(Location location) {
                    if(map != null && location != null){
                        Log.d("COMP3018", "Main Activity - Location changed: " + location);
                        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
//                        mainActivityViewModel.addLatLngToPath(myLocation);
//                        PolylineOptions polylineOptions = new PolylineOptions()
//                                .addAll(mainActivityViewModel.getPath())
//                                .color(Color.GREEN)
//                                .width(10);
//
//                        map.addPolyline(polylineOptions);
//                        updateDistanceAndTime(location);
                    }
                }

                @Override
                public void onStoppedJourney(float distanceTravelled, long startTime, long endTime, String name, String type, List<LatLng> path) {

                }
            });
        }

        // Called when a client unbinds from the service
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mainActivityViewModel.setIsBound(false);
        }
    };


    /**
     * Called when the activity is started.
     */
    @Override
    protected void onStart() {
        Log.d(TAG, "OnStart called");
        super.onStart();
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Called when the activity is stopped.
     */
    @Override
    protected void onStop() {
        Log.d(TAG, "OnStop called");
        super.onStop();
        if (mainActivityViewModel.getIsBound()) {
            if(locationService.getIsUpdating()){
                unbindService(connection);
            } else{
                locationService.stopSelf();
            }
            mainActivityViewModel.setIsBound(false);
        }
    }

    public void onStopWalkPressed(View v){
        MovementEntity movementEntity = new MovementEntity();
        movementEntity.movementName = "Walk1";
        movementEntity.path = mainActivityViewModel.getPath();
        movementEntity.distanceTravelled = mainActivityViewModel.getTotalDistance();
        movementEntity.timeTaken = ((mainActivityViewModel.getEndTime() - mainActivityViewModel.getStartTime())/1000);
        movementEntity.movementType = "walk";
        movementEntity.timeStamp = System.currentTimeMillis();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "MDPDatabase").build();

                db.movementDao().insertMovement(movementEntity);
                return null;
            }
        }.execute();
    }

    public void viewWalk(View v){
        new AsyncTask<Void, Void, MovementEntity>() {
            @Override
            protected MovementEntity doInBackground(Void... voids) {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "MDPDatabase").build();

                MovementDao movementDao = db.movementDao();
                return movementDao.getMovementById("Test Movement");
            }

            @Override
            protected void onPostExecute(MovementEntity retrievedMovement) {
                if (retrievedMovement != null) {
                    List<LatLng> path = retrievedMovement.path;
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(path)
                            .color(Color.GREEN)
                            .width(10);

                    map.addPolyline(polylineOptions);
                } else {
                    // Handle the case where the movement is not found
                }
            }
        }.execute();

    }



    private void updateDistanceAndTime(Location location){
        if(mainActivityViewModel.getPath().size() == 1){
            mainActivityViewModel.setStartTime(location.getTime());
        } else if(mainActivityViewModel.getPath().size() > 1){
            List<LatLng> path = mainActivityViewModel.getPath();
            int pathSize = path.size() -1;
            float[] results = new float[1];
            Location.distanceBetween(path.get(pathSize - 1).latitude, path.get(pathSize - 1).longitude, path.get(pathSize).latitude, path.get(pathSize).longitude, results);
            mainActivityViewModel.setTotalDistance(mainActivityViewModel.getTotalDistance() + results[0]);
            mainActivityViewModel.setEndTime(location.getTime());
        }
    }
}





