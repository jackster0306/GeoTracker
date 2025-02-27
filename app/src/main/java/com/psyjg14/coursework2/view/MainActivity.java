package com.psyjg14.coursework2.view;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.view.View;
import android.widget.EditText;
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
import com.psyjg14.coursework2.model.MyContentProvider;
import com.psyjg14.coursework2.model.NavBarManager;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.databinding.GeofenceDetailsLayoutBinding;
import com.psyjg14.coursework2.model.GeofenceHelper;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.databinding.ActivityMainBinding;
import com.psyjg14.coursework2.viewmodel.MainActivityViewModel;

import java.util.List;
import java.util.Objects;

/**
 * The main activity of the application responsible for displaying the map and managing geofences.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private static final String TAG = "COMP3018";
    private final int FINE_PERMISSION_CODE = 1;
    private SearchView mapSearchView;

    private MainActivityViewModel mainActivityViewModel;

    private GoogleMap map;

    private GeofenceHelper geofenceHelper;

    private static final int GEOFENCE_RADIUS = 100;



    /**
     * Called when the activity is created. Initializes the UI components and sets up the map.
     *
     * @param savedInstanceState The saved state of the activity.
     */
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
            NavBarManager instance = NavBarManager.getInstance();
            int itemId = item.getItemId();
            return instance.navBarItemPressed(MainActivity.this, itemId, R.id.mapMenu);
        });


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

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        mainActivityViewModel.getAllGeofences().observe(this, geofences -> {
            for (GeofenceEntity geofence : geofences) {
                geofenceHelper.addGeofence(geofence.geofenceID, new LatLng(geofence.latitude, geofence.longitude), geofence.radius);
            }
        });


        new Thread(() ->{
            ContentResolver myAppContentProvider = getContentResolver();
            Cursor cursor =  myAppContentProvider.query(MyContentProvider.GEOFENCES_URI, null, null, null, null);
            if(cursor != null){
                try{
                    while(cursor.moveToNext()){
                        for (int i = 0; i < cursor.getColumnCount(); i++) {
                            Log.d("COMP3018", "Column: " + cursor.getColumnName(i) + " Data: " + cursor.getString(i));
                        }
                    }
                }finally {
                    Log.d("COMP3018", "Geofence Cursor closed\n");
                    cursor.close();
                }
            }
            Cursor cursor2 =  myAppContentProvider.query(MyContentProvider.MOVEMENTS_URI, null, null, null, null);
            if(cursor2 != null){
                try{
                    while(cursor2.moveToNext()){
                        for (int i = 0; i < cursor2.getColumnCount(); i++) {
                            Log.d("COMP3018", "Column: " + cursor2.getColumnName(i) + " Data: " + cursor2.getString(i));
                        }
                    }
                }finally {
                    Log.d("COMP3018", "Movement Cursor closed\n");
                    cursor2.close();
                }
            }
        }).start();



    }

    /**
     * Initializes the map and sets up listeners for user interactions.
     *
     * @param googleMap The GoogleMap instance representing the map.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
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
                return false;
            }
        });

        map.setOnMapClickListener(this);

        mainActivityViewModel.getAllGeofences().observe(this, this::updateGeofencesOnMap);

        map.setOnCircleClickListener(circle -> {
            String geofenceName = Objects.requireNonNull(circle.getTag()).toString();
            mainActivityViewModel.getAllGeofences().observe(this, geofences -> {
                for (GeofenceEntity geofence : geofences) {
                    if (geofence.name.equals(geofenceName)) {
                        showGeofenceDetails(geofence);
                    }
                }
            });
        });
    }

    /**
     * Updates the geofences on the map based on the provided list of geofences.
     *
     * @param geofences The list of geofences to be displayed on the map.
     */
    private void updateGeofencesOnMap(List<GeofenceEntity> geofences){
                    map.clear();
                    for (GeofenceEntity geofence : geofences) {
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

    /**
     * Shows the details of a geofence as a popup.
     *
     * @param geofence The geofence entity to display details for.
     */
    private void showGeofenceDetails(GeofenceEntity geofence){

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


            GeofenceDetailsLayoutBinding detailsBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.geofence_details_layout, null, false);
            detailsBinding.setGeofence(geofence);
            builder.setView(detailsBinding.getRoot());



            AlertDialog dialog = builder.create();
            dialog.show();

            detailsBinding.deleteGeofenceButton.setOnClickListener(v -> geofenceHelper.removeGeofence(geofence));

            detailsBinding.closeDialogButton.setOnClickListener(view -> dialog.cancel());

            detailsBinding.editGeofenceButton.setOnClickListener(view -> {
            EditGeofence(geofence);
            dialog.cancel();
            }

            );
    }

    /**
     * Displays a popup for editing a geofence.
     *
     * @param geofence The geofence entity to be edited.
     */
    private void EditGeofence(GeofenceEntity geofence){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Edit Journey");

        View dialogView = getLayoutInflater().inflate(R.layout.edit_geofence_dialog, null);
        builder.setView(dialogView);

        EditText nameInput = dialogView.findViewById(R.id.geofenceNameEditText);
        nameInput.setText(geofence.name);

        EditText noteInput = dialogView.findViewById(R.id.geofenceNoteEditText);
        noteInput.setText(geofence.geofenceNote);

        Spinner spinner = dialogView.findViewById(R.id.geofenceClassificationSpinner);
        int index = 0;
        if(geofence.classification.equals("Neutral")){
            index = 0;
        } else if(geofence.classification.equals("Good")){
            index = 1;
        } else if(geofence.classification.equals("Bad")){
            index = 2;
        }

        spinner.setSelection(index);


        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = nameInput.getText().toString();
            String newNote = noteInput.getText().toString();
            if(newName.equals("")){
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            } else if (newNote.equals("")) {
                Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                geofence.name = newName;
                geofence.geofenceNote = newNote;
                geofence.classification = spinner.getSelectedItem().toString();
                mainActivityViewModel.updateGeofence(geofence);
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();


    }


    /**
     * Called when the map is clicked.
     *
     * @param latLng The LatLng coordinates of the clicked location on the map.
     */
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (mainActivityViewModel.getAddingGeofenceAsBool()) {
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            MarkerOptions options = new MarkerOptions().position(latLng).title("Geofence Location");

            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            if (mainActivityViewModel.getGeofenceMarker() != null) {
                mainActivityViewModel.removeGeofenceMarker();
            }
            mainActivityViewModel.setGeofenceMarker(map.addMarker(options));
        }
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
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        super.onStart();
    }

    /**
     * Called when the activity is stopped.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Displays a popup for adding a new geofence.
     *
     * @param geofenceMarker The marker representing the geofence on the map.
     */
    private void showGeofenceDialog(Marker geofenceMarker) {
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
            for(GeofenceEntity geofence : Objects.requireNonNull(mainActivityViewModel.getAllGeofences().getValue())){
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
                geofenceHelper.addNewGeofence(geofenceName, geofenceClassification, geofenceMarker.getPosition(), GEOFENCE_RADIUS, geofenceNote);
            }
            dialog.cancel();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
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
        outState.putBoolean("addingGeofence", mainActivityViewModel.getAddingGeofenceAsBool());
        outState.putBoolean("geofenceFirstPressed", mainActivityViewModel.getGeofenceFirstPressed());

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

            boolean geofenceFirstPressed = savedInstanceState.getBoolean("geofenceFirstPressed");
            mainActivityViewModel.setGeofenceFirstPressed(geofenceFirstPressed);
        }
    }

}





