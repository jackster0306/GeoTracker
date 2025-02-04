package com.psyjg14.coursework2.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.model.DatabaseRepository;
import com.psyjg14.coursework2.model.TravelDataItem;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for managing all travels, containing a list of all movements and travels.
 */
public class ViewAllTravelsViewModel extends AndroidViewModel {
    private MutableLiveData<List<TravelDataItem>> movementList = new MutableLiveData<>();
    private DatabaseRepository databaseRepository;

    /**
     * Constructor for the ViewAllTravelsViewModel.
     *
     * @param application The application context.
     */
    public ViewAllTravelsViewModel(Application application) {
        super(application);
        movementList.setValue(new ArrayList<>());
        databaseRepository = new DatabaseRepository(application);
    }

    /**
     * Gets a list of all movement entities.
     *
     * @return LiveData containing a list of all movement entities.
     */
    public LiveData<List<MovementEntity>> getAllMovements() {
        return databaseRepository.getAllMovements();
    }

    /**
     * Gets the list of TravelDataItems.
     *
     * @return MutableLiveData containing the list of TravelDataItems.
     */
    public MutableLiveData<List<TravelDataItem>> getMovementList() {
        return movementList;
    }
}
