package com.psyjg14.coursework2.view;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.psyjg14.coursework2.model.MyTypeConverters;
import com.psyjg14.coursework2.model.NavBarManager;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.databinding.ActivityManageCurrentJourneyBinding;
import com.psyjg14.coursework2.model.DatabaseRepository;
import com.psyjg14.coursework2.model.LocationService;
import com.psyjg14.coursework2.viewmodel.ManageCurrentJourneyViewModel;

import java.util.List;
import java.util.Objects;

/**
 * Activity responsible for managing the current journey, including starting and stopping tracking.
 */
public class ManageCurrentJourney extends AppCompatActivity {
    private ManageCurrentJourneyViewModel manageCurrentJourneyViewModel;

    LocationService locationService;

    private ActivityManageCurrentJourneyBinding binding;

    /**
     * Called when the activity is created. Initializes the UI components and sets up the ViewModel.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_current_journey);
        manageCurrentJourneyViewModel = new ViewModelProvider(this).get(ManageCurrentJourneyViewModel.class);
        binding.setViewModel(manageCurrentJourneyViewModel);
        binding.setLifecycleOwner(this);

        BottomNavigationView navBar;
        navBar = binding.manageNavBar;

        navBar.setSelectedItemId(R.id.manageMenu);

        navBar.setOnItemSelectedListener(item -> {
            NavBarManager instance = NavBarManager.getInstance();
            int itemId = item.getItemId();
            return instance.navBarItemPressed(ManageCurrentJourney.this, itemId, R.id.manageMenu);
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        manageCurrentJourneyViewModel.getMovementEntities().observe(this, movements -> {
            manageCurrentJourneyViewModel.setDefaultName(createNewName(movements));
        });
    }

    /**
     * Creates a new name for a journey based on the existing movements.
     *
     * @param movements The list of existing movement entities.
     * @return A new name for the journey.
     */
    private String createNewName(List<MovementEntity> movements){
        String newName;
        if(movements.size() > 0){
            newName = "Journey " + (movements.size()+1);
        }
        else{
            newName = "Journey 1";
        }
        for(MovementEntity movement : movements){
            if(movement.movementName.equals(newName)){
                createNewName(movements);
            }
        }
        return newName;
    }

    /**
     * Handles the result of permission requests.
     *
     * @param requestCode  The request code passed to requestPermissions.
     * @param permissions  The requested permissions.
     * @param grantResults The results of the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Called when the "Start" button is pressed. Initiates journey tracking if permissions are granted.
     *
     * @param v The view that triggered the method.
     */
    public void onStartPressed(View v){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
        } else{
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

            String locationPriority = preferences.getString(getString(R.string.pref_location_accuracy_key), "");
            String updatePeriods = preferences.getString(getString(R.string.pref_update_periods_key), "");
            String trackingType = preferences.getString(getString(R.string.pref_tracking_key), "");
            locationService.setName(manageCurrentJourneyViewModel.getDefaultName());
            locationService.setupLocation(locationPriority, updatePeriods, trackingType);
            manageCurrentJourneyViewModel.setIsTracking(true);
            locationService.startTracking(manageCurrentJourneyViewModel.getType());
            startService(new Intent(this, LocationService.class));
        }
    }

    /**
     * Called when the "Stop" button is pressed. Saves the journey details only if a valid name is provided.
     * Stops journey tracking.
     *
     * @param v The view that triggered the method.
     */
    public void onStopPressed(View v){
        if(binding.nameEditText.getText().toString().equals("")){
            Toast.makeText(ManageCurrentJourney.this, "Please enter a name for your journey", Toast.LENGTH_SHORT).show();
        } else {
            LiveData<List<MovementEntity>> movementEntities = manageCurrentJourneyViewModel.getMovementEntities();
            movementEntities.observe(this, movements -> {
                if(movements != null){
                    boolean nameExists = false;
                    for(MovementEntity movement : movements){
                        if(movement.movementName.equals(binding.nameEditText.getText().toString())){
                            nameExists = true;
                        }
                    }

                    if(nameExists){
                        Toast.makeText(ManageCurrentJourney.this, "Name already exists, please choose another name", Toast.LENGTH_SHORT).show();
                        binding.nameEditText.setText("");
                        //onStopPressed(v);
                        movementEntities.removeObservers(this);

                    } else{
                        manageCurrentJourneyViewModel.setName(binding.nameEditText.getText().toString());
                        manageCurrentJourneyViewModel.setNote(binding.noteEditText.getText().toString());
                        manageCurrentJourneyViewModel.setIsTracking(false);
                        locationService.stopLocationUpdates();
                        Toast.makeText(ManageCurrentJourney.this, "Journey Saved", Toast.LENGTH_SHORT).show();
                        binding.nameEditText.setText("");
                        binding.noteEditText.setText("");
                        movementEntities.removeObservers(this);
                    }
                }
            });

        }
    }



    /**
     * Handles the connection to the LocationService.
     */
    private final ServiceConnection connection = new ServiceConnection() {

        // Called when a client binds to the service
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            locationService = binder.getService();
            manageCurrentJourneyViewModel.setIsBound(true);
            manageCurrentJourneyViewModel.setIsTracking(locationService.getIsUpdating());


            locationService.setCallback(new LocationService.MyLocationCallback() {
                @Override
                public void onLocationChanged(Location location) {
                }

                @Override
                public void onStoppedJourney(float distanceTravelled, long startTime, long endTime, String type, List<LatLng> path) {
                    MovementEntity movementEntity = new MovementEntity();
                    movementEntity.movementName = MyTypeConverters.nameToDatabaseName(manageCurrentJourneyViewModel.getName());
                    movementEntity.movementType = type.toLowerCase();
                    movementEntity.distanceTravelled = distanceTravelled;
                    movementEntity.timeStamp = endTime;
                    movementEntity.timeTaken = endTime - startTime;
                    movementEntity.path = path;
                    movementEntity.note = manageCurrentJourneyViewModel.getNote();
                    locationService.stopSelf();
                    DatabaseRepository databaseRepository = new DatabaseRepository(getApplication());
                    databaseRepository.insertMovement(movementEntity);
                }
            });
        }

        // Called when a client unbinds from the service
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            manageCurrentJourneyViewModel.setIsBound(false);
        }
    };


    /**
     * Called when the activity is started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Called when the activity is stopped.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (manageCurrentJourneyViewModel.getIsBound()) {
            unbindService(connection);
            manageCurrentJourneyViewModel.setIsBound(false);
        }
    }

    /**
     * Handles the back button press.
     */
    public void onBack(){
        NavBarManager.getInstance().onBackPressed(this, R.id.statsMenu);
        finish();
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(manageCurrentJourneyViewModel.getIsTracking().getValue()){
            outState.putBoolean("isTracking", true);
            outState.putString("name", manageCurrentJourneyViewModel.getName());
            outState.putString("type", manageCurrentJourneyViewModel.getType());
            outState.putString("note", manageCurrentJourneyViewModel.getNote());
        } else{
            outState.putBoolean("isTracking", false);
        }
    }

    /**
     * Called when the activity is restored.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getBoolean("isTracking")){
            manageCurrentJourneyViewModel.setIsTracking(true);
            manageCurrentJourneyViewModel.setName(savedInstanceState.getString("name"));
            manageCurrentJourneyViewModel.setType(Objects.requireNonNull(savedInstanceState.getString("type")));
            manageCurrentJourneyViewModel.setNote(savedInstanceState.getString("note"));
        } else{
            manageCurrentJourneyViewModel.setIsTracking(false);
        }
    }

}