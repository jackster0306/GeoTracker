package com.psyjg14.coursework2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.psyjg14.coursework2.view.MainActivity;
import com.psyjg14.coursework2.view.ManageCurrentJourney;
import com.psyjg14.coursework2.view.SettingsActivity;
import com.psyjg14.coursework2.view.ViewDataActivity;

public class NavBarManager {
    private static NavBarManager managerInstance;

    private int selectedItemId;

    private int previousActivityItemId;


    private NavBarManager() {

    }

    public static NavBarManager getInstance() {
        if (managerInstance == null) {
            managerInstance = new NavBarManager();
        }
        return managerInstance;
    }

    public boolean navBarItemPressed(Context context, int itemId, int previousId){
        if(itemId != previousId){
            selectedItemId = itemId;
            previousActivityItemId = previousId;
            Class<?> classToStart = null;
            if (itemId == R.id.mapMenu) {
                classToStart = MainActivity.class;
            } else if (itemId == R.id.statsMenu) {
                classToStart = ViewDataActivity.class;
            } else if (itemId == R.id.settingsMenu) {
                classToStart = SettingsActivity.class;
            } else if (itemId == R.id.manageMenu) {
                classToStart = ManageCurrentJourney.class;
            }
            Intent intent = new Intent(context, classToStart);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(0, 0);
            return true;
        }
        return false;
    }

    public void onBackPressed(Context context, int newPreviousId){
        selectedItemId = previousActivityItemId;
        previousActivityItemId = newPreviousId;
        Intent intent = new Intent(context, getActivityFromId(selectedItemId));
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    public Class<?> getActivityFromId(int itemId){
        Class<?> classToStart = null;
        if (itemId == R.id.mapMenu) {
            classToStart = MainActivity.class;
        } else if (itemId == R.id.statsMenu) {
            classToStart = ViewDataActivity.class;
        } else if (itemId == R.id.settingsMenu) {
            classToStart = SettingsActivity.class;
        } else if (itemId == R.id.manageMenu) {
            classToStart = ManageCurrentJourney.class;
        }
        return classToStart;
    }
}

