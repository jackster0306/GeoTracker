package com.psyjg14.coursework2.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.model.DatabaseRepository;
import com.psyjg14.coursework2.model.TravelDataItem;

import java.util.ArrayList;
import java.util.List;

public class ViewAllTravelsViewModel extends AndroidViewModel {
    private MutableLiveData<List<TravelDataItem>> movementList = new MutableLiveData<>();

    private DatabaseRepository databaseRepository;


    public ViewAllTravelsViewModel(Application application) {
        super(application);
        movementList.setValue(new ArrayList<>());
        databaseRepository = new DatabaseRepository(application);
    }

    public LiveData<List<MovementEntity>> getAllMovements() {
        return databaseRepository.getAllMovements();
    }


    public MutableLiveData<List<TravelDataItem>> getMovementList() {
        return movementList;
    }

}

