package com.psyjg14.coursework2.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.AppDatabase;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.databinding.ActivityMainBinding;
import com.psyjg14.coursework2.databinding.ActivityManageCurrentJourneyBinding;
import com.psyjg14.coursework2.model.LocationService;
import com.psyjg14.coursework2.viewmodel.MainActivityViewModel;
import com.psyjg14.coursework2.viewmodel.ManageCurrentJourneyViewModel;

import java.util.List;

public class ManageCurrentJourney extends AppCompatActivity {
    private ManageCurrentJourneyViewModel manageCurrentJourneyViewModel;

    private BottomNavigationView navBar;

    LocationService locationService;

    private ActivityManageCurrentJourneyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_current_journey);
        manageCurrentJourneyViewModel = new ViewModelProvider(this).get(ManageCurrentJourneyViewModel.class);
        binding.setViewModel(manageCurrentJourneyViewModel);
        binding.setLifecycleOwner(this);
        navBar = binding.manageNavBar;

        navBar.setSelectedItemId(R.id.manageMenu);

        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.mapMenu){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if(itemId == R.id.statsMenu){
                Intent intent = new Intent(this, ViewDataActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if(itemId == R.id.settingsMenu){
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.manageMenu){
                return true;
            }
            return false;
        });
    }


    public void onStartPressed(View v){
        manageCurrentJourneyViewModel.setIsTracking(true);
        locationService.startTracking(manageCurrentJourneyViewModel.getType());
        startService(new Intent(this, LocationService.class));
    }

    public void onStopPressed(View v){
        binding.nameEditText.getText().toString();

        if(binding.nameEditText.getText().toString().equals("")){
            manageCurrentJourneyViewModel.setName("UnnamedTesting");
        } else {
            manageCurrentJourneyViewModel.setName(binding.nameEditText.getText().toString());
        }
        manageCurrentJourneyViewModel.setIsTracking(false);
        locationService.stopLocationUpdates();
    }



    private ServiceConnection connection = new ServiceConnection() {

        // Called when a client binds to the service
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("COMP3018", "onServiceConnected");
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            locationService = binder.getService();
            manageCurrentJourneyViewModel.setIsBound(true);
            Log.d("COMP3018", "*********************onServiceConnected: " + locationService.getIsUpdating());
            manageCurrentJourneyViewModel.setIsTracking(locationService.getIsUpdating());


            locationService.setCallback(new LocationService.MyLocationCallback() {
                @Override
                public void onLocationChanged(Location location) {
                    return;
                }

                @Override
                public void onStoppedJourney(float distanceTravelled, long startTime, long endTime, String name, String type, List<LatLng> path) {
                    Log.d("COMP3018", "NAME: "+ manageCurrentJourneyViewModel.getName());
                    MovementEntity movementEntity = new MovementEntity();
                    movementEntity.movementName = manageCurrentJourneyViewModel.getName();
                    movementEntity.movementType = type;
                    movementEntity.distanceTravelled = distanceTravelled;
                    movementEntity.timeStamp = endTime;
                    movementEntity.timeTaken = endTime - startTime;
                    movementEntity.path = path;
                    locationService.stopSelf();
                    new Thread(() -> {
                        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                                AppDatabase.class, "MDPDatabase").build();

                        db.movementDao().insertMovement(movementEntity);
                    }).start();
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
        Log.d("COMP3018", "OnStart called");
        super.onStart();
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Called when the activity is stopped.
     */
    @Override
    protected void onStop() {
        Log.d("COMP3018", "OnStop called");
        super.onStop();
        if (manageCurrentJourneyViewModel.getIsBound()) {
            unbindService(connection);
            manageCurrentJourneyViewModel.setIsBound(false);
        }
    }
}