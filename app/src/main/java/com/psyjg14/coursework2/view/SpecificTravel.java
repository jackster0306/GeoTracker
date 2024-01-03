package com.psyjg14.coursework2.view;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import android.app.Activity;
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
import com.psyjg14.coursework2.MyTypeConverters;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.AppDatabase;
import com.psyjg14.coursework2.database.dao.MovementDao;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.databinding.ActivitySpecificTravelBinding;

import com.psyjg14.coursework2.viewmodel.SpecificTravelViewModel;

import java.util.Objects;

public class SpecificTravel extends AppCompatActivity implements OnMapReadyCallback {
    private SpecificTravelViewModel specificTravelViewModel;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivitySpecificTravelBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_specific_travel);
        specificTravelViewModel = new ViewModelProvider(this).get(SpecificTravelViewModel.class);
        binding.setViewModel(specificTravelViewModel);
        binding.setLifecycleOwner(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.specificMapFragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Log.d("COMP3018", "************************8Name Before Thread: " + getIntent().getStringExtra("name"));
        if(getIntent().getStringExtra("name") != null){
            specificTravelViewModel.setName(MyTypeConverters.nameFromDatabaseName(Objects.requireNonNull(getIntent().getStringExtra("name"))));
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String distanceUnit = preferences.getString(getString(R.string.pref_unit_system_key), "");
        specificTravelViewModel.setDistanceUnit(distanceUnit);

        new Thread(() -> {
            MovementDao movementDao = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "MDPDatabase").build().movementDao();
            Log.d("COMP3018", "************************8Name: " + specificTravelViewModel.getName().getValue());
            MovementEntity movementEntity = movementDao.getMovementById(MyTypeConverters.nameToDatabaseName(Objects.requireNonNull(specificTravelViewModel.getName().getValue())));
            Log.d("COMP3018", "************************8Distance: " + movementEntity.distanceTravelled + ", Time: " + movementEntity.timeTaken + ", Type: " + movementEntity.movementType + ", TimeStamp: " + movementEntity.timeStamp + ", Path: " + movementEntity.path);
            specificTravelViewModel.setDistance(movementEntity.distanceTravelled);
            specificTravelViewModel.setTimeTaken(movementEntity.timeTaken);
            specificTravelViewModel.setTravelType(movementEntity.movementType);
            specificTravelViewModel.setCompletionTime(movementEntity.timeStamp);
            specificTravelViewModel.setPath(movementEntity.path);
        }).start();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        specificTravelViewModel.getPath().observe(this, path -> {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(path)
                    .color(Color.CYAN)
                    .width(10);
            map.addPolyline(polylineOptions);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(LatLng latLng : path){
                builder.include(latLng);
            }
            LatLng startLatLng = path.get(0);
            LatLng endLatLng = path.get(path.size() - 1);
            map.addMarker(new MarkerOptions().position(startLatLng).title("Start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            map.addMarker(new MarkerOptions().position(endLatLng).title("End"));
            LatLngBounds pathBounds = builder.build();
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(pathBounds, 100));
        });
    }

    public void onBackArrowPressed(View v){
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
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

        outState.putString("name", specificTravelViewModel.getName().getValue());
    }

    /**
     * Called when the activity is restored.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        specificTravelViewModel.setName(savedInstanceState.getString("name"));
    }

}