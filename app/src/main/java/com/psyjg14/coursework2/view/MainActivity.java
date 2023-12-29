package com.psyjg14.coursework2.view;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.text.InputType;
import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import com.psyjg14.coursework2.databinding.GeofenceDetailsLayoutBinding;
import com.psyjg14.coursework2.model.GeofenceBroadcastReceiver;
import com.psyjg14.coursework2.model.GeofenceHelper;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.databinding.ActivityMainBinding;
import com.psyjg14.coursework2.model.LocationService;
import com.psyjg14.coursework2.viewmodel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
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
            if (itemId == R.id.mapMenu) {
                return true;
            } else if (itemId == R.id.statsMenu) {
                Intent intent = new Intent(MainActivity.this, ViewDataActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.settingsMenu) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.manageMenu) {
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

        geofenceHelper = new GeofenceHelper(this, this);


        mainActivityViewModel.getAddingGeofence().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean addingGeofence) {
                Marker geofenceMarker = mainActivityViewModel.getGeofenceMarker();
                if(mainActivityViewModel.getGeofenceMarker() != null){
                    mainActivityViewModel.removeGeofenceMarker();
                }

                if (mainActivityViewModel.getAddingGeofenceAsBool()) {
                    Toast.makeText(MainActivity.this, "Click on the map to add a geofence", Toast.LENGTH_SHORT).show();
                } else {
                    if (geofenceMarker != null) {
                        Log.d("COMP3018", "showGeofenceDialog");
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Add Geofence");

                        View dialogView = getLayoutInflater().inflate(R.layout.geofence_dialog_layout, null);
                        builder.setView(dialogView);

                        EditText nameInput = dialogView.findViewById(R.id.geofenceNameEditText);
                        Spinner spinner = dialogView.findViewById(R.id.geofenceClassificationSpinner);

                        builder.setPositiveButton("OK", (dialog, which) -> {
                            String geofenceName = nameInput.getText().toString();
                            String geofenceClassification = spinner.getSelectedItem().toString();
                            Log.d("COMP3018", "geofenceName: " + geofenceName);
                            if (geofenceName.equals("")) {
                                Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Geofence added", Toast.LENGTH_SHORT).show();
                                geofenceHelper.addGeofence(geofenceName, geofenceClassification, geofenceMarker.getPosition(), GEOFENCE_RADIUS, new GeofenceHelper.onGeofenceAddedCallback() {
                                    @Override
                                    public void onGeofenceAdded() {
                                        geofenceHelper.getGeofences(new GeofenceHelper.GetGeofenceCallback() {
                                            @Override
                                            public void GeofenceCallback(List<GeofenceEntity> geofences) {
                                                Log.d("COMP3018", "GeofenceCallback: " + geofences.size());
                                                mainActivityViewModel.setGeofences(geofences);
                                                updateGeofencesOnMap();
                                            }
                                        });
                                    }
                                });

                            }
                        });

                        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                        builder.show();
                    } else {
                        Log.d("COMP3018", "getFirstPressed: " + mainActivityViewModel.getGeofenceFirstPressed());
                        if (!mainActivityViewModel.getGeofenceFirstPressed()) {
                            Toast.makeText(MainActivity.this, "No geofence added", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        updateGeofencesOnMap();
        map.setOnCircleClickListener(circle -> {
            String geofenceName = circle.getTag().toString();
            //SETUP DIALOG THAT SHOWS, DISPLAYING NAME AND CLASSIFICATION WITH A DELETE BUTTON
            new Thread(() -> {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "MDPDatabase").build();

                GeofenceDao geofenceDao = db.geofenceDao();
                GeofenceEntity geofence = geofenceDao.getGeofenceByName(geofenceName);
                showGeofenceDetails(geofence);
            }).start();
        });
    }

    private void updateGeofencesOnMap(){
        runOnUiThread(
                () -> {
                    map.clear();
                    Log.d("COMP3018", "updateGeofencesOnMap");
                    Log.d("COMP3018", "geofences size: " + mainActivityViewModel.getGeofences().size());
                    for (GeofenceEntity geofence : mainActivityViewModel.getGeofences()) {
                        LatLng latLng = new LatLng(geofence.latitude, geofence.longitude);
                        int geofenceColour;
                        if(geofence.classification.equals("Good")) {
                            geofenceColour = Color.GREEN;
                        } else if(geofence.classification.equals("Bad")) {
                            geofenceColour = Color.RED;
                        } else {
                            geofenceColour = Color.YELLOW;
                        }
                        CircleOptions circleOptions = new CircleOptions()
                                .center(latLng)
                                .radius(geofence.radius) // Set the geofence radius
                                .strokeColor(Color.BLACK)
                                .fillColor(Color.argb(70, Color.red(geofenceColour), Color.green(geofenceColour), Color.blue(geofenceColour)));
                        Circle circle = map.addCircle(circleOptions);

                        circle.setTag(geofence.name);

                        circle.setClickable(true);
                    }
                }
        );

    }

    private void showGeofenceDetails(GeofenceEntity geofence){
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


            GeofenceDetailsLayoutBinding detailsBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.geofence_details_layout, null, false);
            detailsBinding.setGeofence(geofence);
            builder.setView(detailsBinding.getRoot());


            detailsBinding.deleteGeofenceButton.setOnClickListener(v ->{
                new Thread(() -> {
                    AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "MDPDatabase").build();

                    GeofenceDao geofenceDao = db.geofenceDao();
                    geofenceDao.deleteGeofence(geofence);
                    mainActivityViewModel.setGeofences(geofenceDao.getAllGeofences());
                    Log.d("COMP3018", "geofence deleted");
                    updateGeofencesOnMap();
                }).start();
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            detailsBinding.closeDialogButton.setOnClickListener(view -> dialog.cancel());
        });
    }


    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "Map clicked");
        if (mainActivityViewModel.getAddingGeofenceAsBool()) {
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            MarkerOptions options = new MarkerOptions().position(latLng).title("Geofence Location");
            //Change colour of marker
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            if (mainActivityViewModel.getGeofenceMarker() != null) {
                mainActivityViewModel.removeGeofenceMarker();
            }
            mainActivityViewModel.setGeofenceMarker(map.addMarker(options));
            Log.d("COMP3018", "Map Clicked, Geofence Marker: " + mainActivityViewModel.getGeofenceMarker());
        }
    }

    /**************************************************************************
     *  Requests code
     **************************************************************************/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //locationService.getLastLocation();
                mainActivityViewModel.setRequestingLocationUpdates(true);
            } else {
                Toast.makeText(this, "Permission denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Called when the activity is started.
     */
    @Override
    protected void onStart() {
        Log.d(TAG, "OnStart called");
        super.onStart();
    }

    /**
     * Called when the activity is stopped.
     */
    @Override
    protected void onStop() {
        Log.d(TAG, "OnStop called");
        super.onStop();
    }

    private void showGeofenceDialog(){

    }

}





