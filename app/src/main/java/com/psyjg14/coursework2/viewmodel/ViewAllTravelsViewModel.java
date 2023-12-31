package com.psyjg14.coursework2.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.psyjg14.coursework2.model.TravelDataItem;

import java.util.ArrayList;
import java.util.List;

public class ViewAllTravelsViewModel extends ViewModel {
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
}

