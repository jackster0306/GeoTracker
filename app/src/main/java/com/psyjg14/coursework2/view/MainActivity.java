package com.psyjg14.coursework2.view;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.psyjg14.coursework2.database.AppDatabase;
import com.psyjg14.coursework2.database.dao.GeofenceDao;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.databinding.GeofenceDetailsLayoutBinding;
import com.psyjg14.coursework2.model.GeofenceHelper;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.databinding.ActivityMainBinding;
import com.psyjg14.coursework2.viewmodel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private static final String TAG = "COMP3018";
    private final int FINE_PERMISSION_CODE = 1;
    private SearchView mapSearchView;

    private MainActivityViewModel mainActivityViewModel;

    private GoogleMap map;

    private GeofenceHelper geofenceHelper;

    private static final int GEOFENCE_RADIUS = 100;



    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Data binding initialization
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        binding.setViewModel(mainActivityViewModel);
        binding.setLifecycleOwner(this);

        BottomNavigationView navBar;

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

        geofenceHelper = new GeofenceHelper(this, this);


        mainActivityViewModel.getAddingGeofence().observe(this, addingGeofence -> {
            Marker geofenceMarker = mainActivityViewModel.getGeofenceMarker();
            if(mainActivityViewModel.getGeofenceMarker() != null){
                mainActivityViewModel.removeGeofenceMarker();
            }

            if (mainActivityViewModel.getAddingGeofenceAsBool()) {
                Toast.makeText(MainActivity.this, "Click on the map to add a geofence", Toast.LENGTH_SHORT).show();
            } else {
                if (geofenceMarker != null) {
                    showGeofenceDialog(geofenceMarker);
                } else {
                    Log.d("COMP3018", "getFirstPressed: " + mainActivityViewModel.getGeofenceFirstPressed());
                    if (!mainActivityViewModel.getGeofenceFirstPressed()) {
                        Toast.makeText(MainActivity.this, "No geofence added", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
        } else {
            mainActivityViewModel.setRequestingLocationUpdates(true);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieve the values of preferences using the keys
        String locationAccuracy = preferences.getString(getString(R.string.pref_location_accuracy_key), "");
        String unitSystem = preferences.getString(getString(R.string.pref_unit_system_key), "");
        String updatePeriods = preferences.getString(getString(R.string.pref_update_periods_key), "");

        // Now you have the preference values, you can use them as needed
        // ...

        // Example: Log the retrieved values
        Log.d("COMP3018", "Location Accuracy: " + locationAccuracy);
        Log.d("COMP3018", "Unit System: " + unitSystem);
        Log.d("COMP3018", "Update Periods: " + updatePeriods);

    }


    /**************************************************************************
     *  Map code
     **************************************************************************/

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("COMP3018", "Map is ready");
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

                Geocoder geocoder = new Geocoder(MainActivity.this);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                assert addressList != null;
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                map.addMarker(new MarkerOptions().position(latLng).title(location));
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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
            String geofenceName = Objects.requireNonNull(circle.getTag()).toString();
            //SETUP DIALOG THAT SHOWS, DISPLAYING NAME AND CLASSIFICATION WITH A DELETE BUTTON
            geofenceHelper.getGeofences(geofences -> {
                for (GeofenceEntity geofence : geofences) {
                    if (geofence.name.equals(geofenceName)) {
                        showGeofenceDetails(geofence);
                    }
                }
            });
        });
    }

    private void updateGeofencesOnMap(){
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

    private void showGeofenceDetails(GeofenceEntity geofence){

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


            GeofenceDetailsLayoutBinding detailsBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.geofence_details_layout, null, false);
            detailsBinding.setGeofence(geofence);
            builder.setView(detailsBinding.getRoot());



            AlertDialog dialog = builder.create();
            dialog.show();

        detailsBinding.deleteGeofenceButton.setOnClickListener(v -> geofenceHelper.removeGeofence(geofence, geofences -> {
            mainActivityViewModel.setGeofences(geofences);
            updateGeofencesOnMap();
            dialog.cancel();
        }));

            detailsBinding.closeDialogButton.setOnClickListener(view -> dialog.cancel());
    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) {
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
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
                }
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

    private void showGeofenceDialog(Marker geofenceMarker) {
        Log.d("COMP3018", "showGeofenceDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Geofence");

        View dialogView = getLayoutInflater().inflate(R.layout.geofence_dialog_layout, null);
        builder.setView(dialogView);

        TextView nameInput = dialogView.findViewById(R.id.geofenceNameEditText);
        Spinner spinner = dialogView.findViewById(R.id.geofenceClassificationSpinner);
        TextView noteInput = dialogView.findViewById(R.id.geofenceNoteEditText);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String geofenceName = nameInput.getText().toString();
            String geofenceClassification = spinner.getSelectedItem().toString();
            String geofenceNote = noteInput.getText().toString();
            boolean isDuplicate = false;
            Log.d("COMP3018", "geofenceName: " + geofenceName);
            for(GeofenceEntity geofence : mainActivityViewModel.getGeofences()){
                Log.d("COMP3018", "Geofence Name from Current Geofences: " + geofence.name + " geofenceName to add: " + geofenceName);
                if(geofence.name.equals(geofenceName)){
                    isDuplicate = true;
                }
            }
            if (geofenceName.equals("")) {
                Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
            } else if(isDuplicate){
                Toast.makeText(MainActivity.this, "Geofence name already exists", Toast.LENGTH_SHORT).show();
                mainActivityViewModel.onGeofenceButtonPressed();
                showGeofenceDialog(geofenceMarker);
            }
            else {
                Toast.makeText(MainActivity.this, "Geofence added", Toast.LENGTH_SHORT).show();
                geofenceHelper.addGeofence(geofenceName, geofenceClassification, geofenceMarker.getPosition(), GEOFENCE_RADIUS, geofenceNote, () -> geofenceHelper.getGeofences(geofences -> {
                    Log.d("COMP3018", "GeofenceCallback: " + geofences.size());
                    mainActivityViewModel.setGeofences(geofences);
                    updateGeofencesOnMap();
                }));

            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void onBack(){
        finish();
    }


    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of addingGeofence
        outState.putBoolean("addingGeofence", mainActivityViewModel.getAddingGeofenceAsBool());
        // Save the state of geofenceFirstPressed
        outState.putBoolean("geofenceFirstPressed", mainActivityViewModel.getGeofenceFirstPressed());
        // You can save other relevant data here if needed

        if(mainActivityViewModel.getAddingGeofenceAsBool()){
            //put marker position
        }
    }

    /**
     * Called when the activity is restored.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            boolean addingGeofence = savedInstanceState.getBoolean("addingGeofence");
            mainActivityViewModel.setAddingGeofence(addingGeofence);

            // Restore the state of geofenceFirstPressed
            boolean geofenceFirstPressed = savedInstanceState.getBoolean("geofenceFirstPressed");
            mainActivityViewModel.setGeofenceFirstPressed(geofenceFirstPressed);
        }
    }

}





