package com.psyjg14.coursework2.view;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.psyjg14.coursework2.NavBarManager;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.databinding.ActivityViewDataBinding;
import com.psyjg14.coursework2.viewmodel.ViewDataViewModel;

import java.util.Calendar;
import java.util.List;


public class ViewDataActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ViewDataViewModel viewDataViewModel;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        ActivityViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_view_data);
        viewDataViewModel = new ViewModelProvider(this).get(ViewDataViewModel.class);
        binding.setViewModel(viewDataViewModel);
        binding.setLifecycleOwner(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String distanceUnit = preferences.getString(getString(R.string.pref_unit_system_key), "");
        viewDataViewModel.setDistanceUnit(distanceUnit);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.dataMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        viewDataViewModel.updateDistanceAndTime();

        BottomNavigationView navBar;

        navBar = binding.dataNavBar;


        navBar.setSelectedItemId(R.id.statsMenu);



        navBar.setOnItemSelectedListener(item -> {
            NavBarManager instance = NavBarManager.getInstance();
            int itemId = item.getItemId();
            return instance.navBarItemPressed(ViewDataActivity.this, itemId, R.id.statsMenu);
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        viewDataViewModel.getTimeSelected().observe(this, timeSelected -> {
            timeOrTypeUpdated(timeSelected, viewDataViewModel.getTypeSelected().getValue());
        });

        viewDataViewModel.getTypeSelected().observe(this, typeSelected -> {
            timeOrTypeUpdated(viewDataViewModel.getTimeSelected().getValue(), typeSelected);
        });

        viewDataViewModel.getLastMovement().observe(this, movementEntity -> {
            if(movementEntity != null) {
                viewDataViewModel.setPath(movementEntity.path);
            }
        });

    }

    public void timeOrTypeUpdated(int timeSelected, int typeSelected){
        Calendar calendar = Calendar.getInstance();
        long endTime = System.currentTimeMillis();
        long startTime;
        if(timeSelected == R.id.dayButton){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            startTime = calendar.getTimeInMillis();
        } else if(timeSelected == R.id.weekButton){
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            startTime = calendar.getTimeInMillis();
        } else if(timeSelected == R.id.monthButton){
            calendar.add(Calendar.MONTH, -1);
            startTime = calendar.getTimeInMillis();
        } else if(timeSelected == R.id.yearButton){
            calendar.add(Calendar.YEAR, -1);
            startTime = calendar.getTimeInMillis();
        } else{
            startTime = 0;
        }

        if(typeSelected == R.id.allButton){
            viewDataViewModel.getMovementsByTime(startTime, endTime).observe(this, movements -> {
                float distance = 0;
                long time = 0;
                if (movements.size() > 0) {
                    for (MovementEntity movement : movements) {
                        distance += movement.distanceTravelled;
                        time += movement.timeTaken;
                    }
                }
                if (viewDataViewModel.getDistanceUnit().equals("metric")) {
                    viewDataViewModel.setDistanceTravelled("Distance Travelled: " + distance / 1000 + " kilometres");
                } else {
                    viewDataViewModel.setDistanceTravelled("Distance Travelled: " + distance * 0.000621371 + " miles");
                }
                long hours = time / 3600;
                long remainingSeconds = time % 3600;
                long minutes = remainingSeconds / 60;
                long seconds = remainingSeconds % 60;
                viewDataViewModel.setTimeTaken(String.format("Time Taken: %02d:%02d:%02d", hours, minutes, seconds));
                viewDataViewModel.setNumOfSessions("Number of Sessions: " + movements.size());

            });
        }else{
            viewDataViewModel.getMovementsByTimeAndType(viewDataViewModel.getType(typeSelected), startTime, endTime).observe(this, movements -> {
                float distance = 0;
                long time = 0;
                if (movements.size() > 0) {
                    for (MovementEntity movement : movements) {
                        distance += movement.distanceTravelled;
                        time += movement.timeTaken;
                    }
                }
                if (viewDataViewModel.getDistanceUnit().equals("metric")) {
                    viewDataViewModel.setDistanceTravelled("Distance Travelled: " + distance / 1000 + " kilometres");
                } else {
                    viewDataViewModel.setDistanceTravelled("Distance Travelled: " + distance * 0.000621371 + " miles");
                }
                long hours = time / 3600;
                long remainingSeconds = time % 3600;
                long minutes = remainingSeconds / 60;
                long seconds = remainingSeconds % 60;
                viewDataViewModel.setTimeTaken(String.format("Time Taken: %02d:%02d:%02d", hours, minutes, seconds));
                viewDataViewModel.setNumOfSessions("Number of Sessions: " + movements.size());

            });
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        viewDataViewModel.getPath().observe(this, path -> {
            if(path.size()>1){
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(path)
                        .color(Color.CYAN)
                        .width(10);
                map.addPolyline(polylineOptions);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for(LatLng latLng : path){
                    builder.include(latLng);
                }
                if(path.size() == 0){
                    return;
                }
                LatLngBounds pathBounds = builder.build();

                map.setOnMapLoadedCallback(() -> {
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(pathBounds, 100));
                });

                LatLng startLatLng = path.get(0);
                LatLng endLatLng = path.get(path.size() - 1);
                map.addMarker(new MarkerOptions().position(startLatLng).title("Start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                map.addMarker(new MarkerOptions().position(endLatLng).title("End"));
            }

        });
    }

    public void viewAllTravels(View v){
        Intent intent = new Intent(this, ViewAllTravelsActivity.class);
        startActivity(intent);
    }

    public void onBack(){
        NavBarManager.getInstance().onBackPressed(this, R.id.statsMenu);
        finish();
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the current background color, playback speed
        super.onSaveInstanceState(outState);
    }

    /**
     * Called when the activity is restored.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}