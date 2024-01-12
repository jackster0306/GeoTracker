package com.psyjg14.coursework2.view;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.databinding.ActivitySpecificTravelBinding;

import com.psyjg14.coursework2.viewmodel.SpecificTravelViewModel;

import java.util.List;
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

        specificTravelViewModel.getMovementById(MyTypeConverters.nameToDatabaseName(Objects.requireNonNull(specificTravelViewModel.getName().getValue()))).observe(this, movementEntity -> {
            if(movementEntity != null){
                specificTravelViewModel.setDistance(movementEntity.distanceTravelled);
                specificTravelViewModel.setTimeTaken(movementEntity.timeTaken);
                specificTravelViewModel.setTravelTypeText(movementEntity.movementType);
                specificTravelViewModel.setCompletionTime(movementEntity.timeStamp);
                specificTravelViewModel.setPath(movementEntity.path);
                specificTravelViewModel.setTravelType(movementEntity.movementType);
            }
        });

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

    public void onDeleteJourneyPressed(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Journey");
        builder.setMessage("Are you sure you want to delete this journey?");

        builder.setPositiveButton("Delete", (dialog, which) ->
                specificTravelViewModel.getMovementById(MyTypeConverters.nameToDatabaseName(Objects.requireNonNull(specificTravelViewModel.getName().getValue()))).observe(this, movementEntity -> {
                    if(movementEntity != null){
                        specificTravelViewModel.deleteMovement(movementEntity);
                        Toast.makeText(this, "Journey Deleted", Toast.LENGTH_SHORT).show();
                        onBackArrowPressed(null);
                    }
                }));

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void onEditJourneyPressed(View v){
        Log.d("COMP3018", "isWalk: " + specificTravelViewModel.getIsWalk().getValue() + ", isRun: " + specificTravelViewModel.getIsRun().getValue() + ", isCycle: " + specificTravelViewModel.getIsCycle().getValue());
        AlertDialog.Builder builder = new AlertDialog.Builder(SpecificTravel.this);
        builder.setTitle("Edit Journey");

        View dialogView = getLayoutInflater().inflate(R.layout.edit_journey_dialog, null);
        builder.setView(dialogView);


        if(specificTravelViewModel.getIsWalk().getValue()){
            ((RadioButton) dialogView.findViewById(R.id.walkRadioButton)).setChecked(true);
        } else if (specificTravelViewModel.getIsRun().getValue()) {
            ((RadioButton) dialogView.findViewById(R.id.runRadioButton)).setChecked(true);
        } else if (specificTravelViewModel.getIsCycle().getValue()){
            ((RadioButton) dialogView.findViewById(R.id.cycleRadioButton)).setChecked(true);
        }

        EditText nameInput = dialogView.findViewById(R.id.editTextName);
        nameInput.setText(specificTravelViewModel.getName().getValue());

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> specificTravelViewModel.onRadioButtonPressed(checkedId));


        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = nameInput.getText().toString();
            //int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            //String selectedRadioButtonText = ((RadioButton) dialogView.findViewById(selectedRadioButtonId)).getText().toString();
            if(newName.equals("")){
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                    if(newName.equals(specificTravelViewModel.getName().getValue())){
                        specificTravelViewModel.getMovementById(MyTypeConverters.nameToDatabaseName(Objects.requireNonNull(specificTravelViewModel.getName().getValue()))).observe(this, movementEntity -> {
                            if(movementEntity != null){
                                movementEntity.movementType = specificTravelViewModel.getTravelType().toLowerCase();
                                specificTravelViewModel.updateMovement(movementEntity);
                                Toast.makeText(this, "Journey Edited", Toast.LENGTH_SHORT).show();
                                specificTravelViewModel.setTravelTypeText(specificTravelViewModel.getTravelType());
                            }
                        });
                    } else{
                        List<MovementEntity> movements = specificTravelViewModel.getAllMovements().getValue();
                        boolean nameExists = false;
                        for(MovementEntity movement : movements){
                            if(movement.movementName.equals(newName)){
                                nameExists = true;
                            }
                        }
                        if(nameExists){
                            Toast.makeText(this, "Name already exists, cancelling edit", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        } else{
                            specificTravelViewModel.getMovementById(MyTypeConverters.nameToDatabaseName(Objects.requireNonNull(specificTravelViewModel.getName().getValue()))).observe(this, movementEntity -> {
                                if(movementEntity != null){
                                    specificTravelViewModel.deleteMovement(movementEntity);
                                    movementEntity.movementName = MyTypeConverters.nameToDatabaseName(newName);
                                    movementEntity.movementType = specificTravelViewModel.getTravelType().toLowerCase();
                                    specificTravelViewModel.insertMovement(movementEntity);
                                    Toast.makeText(this, "Journey Edited", Toast.LENGTH_SHORT).show();
                                    specificTravelViewModel.setName(newName);
                                    specificTravelViewModel.setTravelTypeText(specificTravelViewModel.getTravelType());
                                }
                            });
                        }
                    }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

}