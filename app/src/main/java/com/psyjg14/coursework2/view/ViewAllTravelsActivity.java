package com.psyjg14.coursework2.view;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.psyjg14.coursework2.MyTypeConverters;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.TravelDataItemAdapter;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.databinding.ActivityViewAllTravelsBinding;
import com.psyjg14.coursework2.model.TravelDataItem;
import com.psyjg14.coursework2.viewmodel.ViewAllTravelsViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewAllTravelsActivity extends AppCompatActivity {
    private ViewAllTravelsViewModel viewAllTravelsViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        ActivityViewAllTravelsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_view_all_travels);
        viewAllTravelsViewModel = new ViewModelProvider(this).get(ViewAllTravelsViewModel.class);
        binding.setViewModel(viewAllTravelsViewModel);
        binding.setLifecycleOwner(this);


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        RecyclerView recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        TravelDataItemAdapter adapter = new TravelDataItemAdapter(viewAllTravelsViewModel.getMovementList(), this);
        recyclerView.setAdapter(adapter);

        Log.d("COMP3018", "ViewAllTravelsActivity: onCreate: ");
        viewAllTravelsViewModel.getAllMovements().observe(this, movementEntities -> {
            if(movementEntities != null){
                viewAllTravelsViewModel.getMovementList().getValue().clear();
                List<TravelDataItem> list = new ArrayList<>();
                for(MovementEntity entity : movementEntities){
                    Date date = new Date(entity.timeStamp);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss", Locale.UK);
                    String dateCompleted = "Date Completed: "+ dateFormat.format(date);
                    String traveltype = entity.movementType;
                    if(entity.movementType.equals("walk")){
                        traveltype = "Travel Type: Walk";
                    } else if(entity.movementType.equals("run")){
                        traveltype = "Travel Type: Run";
                    } else if(entity.movementType.equals("cycle")){
                        traveltype = "Travel Type: Cycle";
                    }
                    TravelDataItem travelDataItem = new TravelDataItem(MyTypeConverters.nameFromDatabaseName(entity.movementName), traveltype, dateCompleted);
                    list.add(travelDataItem);
                    Log.d("COMP3018", "ViewAllTravelsActivity: onCreate: " + travelDataItem.getName() + ", " + travelDataItem.getTravelType() + ", " + travelDataItem.getDateCompleted());
                }
                Log.d("COMP3018", "ViewAllTravelsActivity: onCreate: " + list);
                viewAllTravelsViewModel.getMovementList().setValue(list);
            }
        });

        viewAllTravelsViewModel.getMovementList().observe(this, travelDataItems -> {
            if(travelDataItems != null){
                adapter.notifyDataSetChanged();
            }
        });

    }

    public void onShowMapPressed(View view) {
        String name = view.getTag().toString();
        Log.d("COMP3018", "Name: " + name);
        Intent intent = new Intent(this, SpecificTravel.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    public void onBackArrowPressed(View v){
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