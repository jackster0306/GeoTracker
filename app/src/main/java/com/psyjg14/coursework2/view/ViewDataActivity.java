package com.psyjg14.coursework2.view;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
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
import com.psyjg14.coursework2.databinding.ActivityViewDataBinding;
import com.psyjg14.coursework2.viewmodel.ViewDataViewModel;


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