package com.psyjg14.coursework2.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;

import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.TravelDataItemAdapter;
import com.psyjg14.coursework2.database.AppDatabase;
import com.psyjg14.coursework2.database.dao.MovementDao;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.databinding.ActivityViewAllTravelsBinding;
import com.psyjg14.coursework2.databinding.ActivityViewDataBinding;
import com.psyjg14.coursework2.model.TravelDataItem;
import com.psyjg14.coursework2.viewmodel.ViewAllTravelsViewModel;
import com.psyjg14.coursework2.viewmodel.ViewDataViewModel;

import java.util.List;

public class ViewAllTravelsActivity extends AppCompatActivity {
    private ViewAllTravelsViewModel viewAllTravelsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        ActivityViewAllTravelsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_view_all_travels);
        viewAllTravelsViewModel = new ViewModelProvider(this).get(ViewAllTravelsViewModel.class);
        binding.setViewModel(viewAllTravelsViewModel);
        binding.setLifecycleOwner(this);

        new Thread(() -> {
            MovementDao movementDao = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "MDPDatabase").build().movementDao();
            List<MovementEntity> movementEntities = movementDao.getAllMovements();
            for(MovementEntity entity : movementEntities){
                Log.d("COMP3018", "**************************ENTITY - MovementName: " + entity.movementName + " Distance: " + entity.distanceTravelled + " Time: " + entity.timeTaken + " Type: " + entity.movementType + " TimeStamp: " + entity.timeStamp);
                viewAllTravelsViewModel.addToMovementList(new TravelDataItem(entity.movementName, entity.distanceTravelled, entity.timeTaken, entity.movementType, entity.timeStamp));
            }
        }).start();

        RecyclerView recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        TravelDataItemAdapter adapter = new TravelDataItemAdapter(viewAllTravelsViewModel.getMovementList().getValue());
        recyclerView.setAdapter(adapter);
        viewAllTravelsViewModel.getMovementList().observe(this, travelDataItems -> {
            adapter.setTravelDataItemList(travelDataItems);
        });
    }
}