package com.psyjg14.coursework2.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.psyjg14.coursework2.database.entities.MovementEntity;
import com.psyjg14.coursework2.model.TravelDataItem;

import java.util.ArrayList;
import java.util.List;

public class ViewAllTravelsViewModel extends ViewModel {
    private MutableLiveData<List<LatLng>> path = new MutableLiveData<>();

    private MutableLiveData<List<TravelDataItem>> movementList = new MutableLiveData<>();

    public ViewAllTravelsViewModel() {
        movementList.setValue(new ArrayList<>());
    }


    public MutableLiveData<List<TravelDataItem>> getMovementList() {
        return movementList;
    }

    public void addToMovementList(TravelDataItem dataItem){
        List<TravelDataItem> list = movementList.getValue();
        list.add(dataItem);
        movementList.postValue(list);
    }

//    public MutableLiveData<String> getName() {
//        return name;
//    }
//    public MutableLiveData<String> getDistanceText() {
//        return distanceText;
//    }
//
//    public MutableLiveData<String> getTimeTakenText() {
//        return timeTakenText;
//    }
//
//    public MutableLiveData<String> getTravelType() {
//        return travelType;
//    }
//
//    public MutableLiveData<String> getCompletionTimeText() {
//        return completionTimeText;
//    }

    public void setPath(List<LatLng> newPath){
        path.postValue(newPath);
    }

    public MutableLiveData<List<LatLng>> getPath() {
        return path;
    }




}

