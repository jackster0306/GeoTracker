package com.psyjg14.coursework2.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

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
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.TravelDataItemAdapter;
import com.psyjg14.coursework2.database.AppDatabase;
import com.psyjg14.coursework2.database.dao.MovementDao;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.databinding.ActivityViewAllTravelsBinding;
import com.psyjg14.coursework2.model.TravelDataItem;
import com.psyjg14.coursework2.viewmodel.ViewAllTravelsViewModel;

import java.util.List;

public class ViewAllTravelsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ViewAllTravelsViewModel viewAllTravelsViewModel;

    private GoogleMap map;

    ActivityViewAllTravelsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_all_travels);
        viewAllTravelsViewModel = new ViewModelProvider(this).get(ViewAllTravelsViewModel.class);
        binding.setViewModel(viewAllTravelsViewModel);
        binding.setLifecycleOwner(this);

        RecyclerView recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        TravelDataItemAdapter adapter = new TravelDataItemAdapter(viewAllTravelsViewModel.getMovementList(), this);
        recyclerView.setAdapter(adapter);

        new Thread(() -> {
            MovementDao movementDao = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "MDPDatabase").build().movementDao();
            List<MovementEntity> movementEntities = movementDao.getAllMovements();
            for(MovementEntity entity : movementEntities){
                viewAllTravelsViewModel.addToMovementList(new TravelDataItem(entity.movementName, entity.distanceTravelled, entity.timeTaken, entity.movementType, entity.timeStamp));
            }
        }).start();


    }

    public void onShowMapPressed(View view) {
        Log.d("COMP3018", "onShowMapPressed");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.travelsMap);
        mapFragment.getMapAsync(this);
        String name = view.getTag().toString();

        new Thread(() -> {
            MovementDao movementDao = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "MDPDatabase").build().movementDao();
            MovementEntity movementEntity = movementDao.getMovementById(name);
            viewAllTravelsViewModel.setPath(movementEntity.path);
        }).start();
        binding.mapFrameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        viewAllTravelsViewModel.getPath().observe(this, path -> {
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
}