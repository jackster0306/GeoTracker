package com.psyjg14.coursework2.view;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;


import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import com.psyjg14.coursework2.model.MyTypeConverters;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.databinding.ActivitySpecificTravelBinding;

import com.psyjg14.coursework2.viewmodel.SpecificTravelViewModel;

import java.util.List;
import java.util.Objects;

/**
 * Activity responsible for displaying details of a specific travel journey, including a map view.
 */
public class SpecificTravel extends AppCompatActivity implements OnMapReadyCallback {
    private SpecificTravelViewModel specificTravelViewModel;

    private GoogleMap map;

    /**
     * Called when the activity is created. Initializes the UI components, sets up the ViewModel,
     * and fetches details of the specific travel journey.
     *
     * @param savedInstanceState The saved state of the activity.
     */
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
                specificTravelViewModel.setNoteText(movementEntity.note);
                specificTravelViewModel.setNote(movementEntity.note);
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

    /**
     * Called when the map is ready to be used. Updates the map with the journey's path and markers.
     *
     * @param googleMap The GoogleMap instance representing the map.
     */
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

    /**
     * Handles the back arrow button press, finishing the activity.
     *
     * @param v The View associated with the back arrow button.
     */
    public void onBackArrowPressed(View v){
        finish();
    }

    /**
     * Handles the back button press, finishing the activity.
     */
    public void onBack(){
        finish();
    }


    /**
     * Handles the button press to delete the current journey, displaying a confirmation popup.
     *
     * @param v The View associated with the delete button.
     */
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

    /**
     * Handles the button press to edit the current journey, displaying a popup.
     *
     * @param v The View associated with the edit button.
     */
    public void onEditJourneyPressed(View v){
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

        EditText noteInput = dialogView.findViewById(R.id.editTextNote);
        noteInput.setText(specificTravelViewModel.getNote());


        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = nameInput.getText().toString();
            String newNote = noteInput.getText().toString();
            if(newName.equals("")){
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                    if(newName.equals(specificTravelViewModel.getName().getValue())){
                        LiveData<MovementEntity> movementEntityLiveData =specificTravelViewModel.getMovementById(MyTypeConverters.nameToDatabaseName(Objects.requireNonNull(specificTravelViewModel.getName().getValue())));
                        movementEntityLiveData.observe(this, movementEntity -> {
                            if(movementEntity != null){
                                movementEntity.movementType = specificTravelViewModel.getTravelType().toLowerCase();
                                movementEntity.note = newNote;
                                specificTravelViewModel.updateMovement(movementEntity);
                                Toast.makeText(SpecificTravel.this, "Journey Edited", Toast.LENGTH_SHORT).show();
                                specificTravelViewModel.setTravelTypeText(specificTravelViewModel.getTravelType());
                                specificTravelViewModel.setNoteText(newNote);
                                specificTravelViewModel.getMovementById(MyTypeConverters.nameToDatabaseName(Objects.requireNonNull(specificTravelViewModel.getName().getValue()))).removeObservers(SpecificTravel.this);
                                movementEntityLiveData.removeObservers(SpecificTravel.this);
                            }
                        });
                    } else{
                        LiveData<List<MovementEntity>> movementEntityLiveData = specificTravelViewModel.getAllMovements();
                        movementEntityLiveData.observe(this, movementEntities -> {
                            if(movementEntities != null){
                                boolean nameExists = false;
                                for(MovementEntity movement : movementEntities){
                                    if(movement.movementName.equals(newName)){
                                        nameExists = true;
                                    }
                                }
                                if(nameExists){
                                    Toast.makeText(this, "Name already exists, cancelling edit", Toast.LENGTH_SHORT).show();
                                    movementEntityLiveData.removeObservers(SpecificTravel.this);
                                    dialog.cancel();
                                } else{
                                    LiveData<MovementEntity> movementEntityLive =specificTravelViewModel.getMovementById(MyTypeConverters.nameToDatabaseName(Objects.requireNonNull(specificTravelViewModel.getName().getValue())));
                                    movementEntityLive.observe(this, movementEntity -> {
                                        if(movementEntity != null){
                                            movementEntity.movementName = MyTypeConverters.nameToDatabaseName(newName);
                                            movementEntity.movementType = specificTravelViewModel.getTravelType().toLowerCase();
                                            Toast.makeText(this, "Journey Edited", Toast.LENGTH_SHORT).show();
                                            specificTravelViewModel.setName(newName);
                                            specificTravelViewModel.setTravelTypeText(specificTravelViewModel.getTravelType());
                                            specificTravelViewModel.updateMovement(movementEntity);
                                            movementEntityLiveData.removeObservers(SpecificTravel.this);
                                        }
                                    });
                                }

                            }
                        });
                    }
                    dialog.cancel();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
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