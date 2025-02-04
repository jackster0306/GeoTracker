package com.psyjg14.coursework2.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.view.MainActivity;
import com.psyjg14.coursework2.view.ManageCurrentJourney;
import com.psyjg14.coursework2.view.SettingsActivity;
import com.psyjg14.coursework2.view.ViewDataActivity;

/**
 * The NavBarManager class is a singleton that manages navigation between different activities
 * Using a Bottom Navigation Bar. It keeps track of the selected item and the previous item in the navigation bar.
 */
public class NavBarManager {
    private static NavBarManager managerInstance;

    private int selectedItemId;

    private int previousActivityItemId;


    private NavBarManager() {

    }

    /**
     * Returns the singleton instance of NavBarManager.
     *
     * @return The singleton instance of NavBarManager.
     */
    public static NavBarManager getInstance() {
        if (managerInstance == null) {
            managerInstance = new NavBarManager();
        }
        return managerInstance;
    }

    /**
     * Handles the selection of a navigation item.
     *
     * @param context      The context of the application.
     * @param itemId       The ID of the selected item.
     * @param previousId   The ID of the previously selected item.
     * @return true if the selection is different from the previous one; otherwise, false.
     */
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

    /**
     * Handles the back press event, navigating to the previous selected item.
     *
     * @param context         The context of the application.
     * @param newPreviousId   The ID of the item to set as the new previous item.
     */
    public void onBackPressed(Context context, int newPreviousId){
        selectedItemId = previousActivityItemId;
        previousActivityItemId = newPreviousId;
        Intent intent = new Intent(context, getActivityFromId(selectedItemId));
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    /**
     * Gets the activity class associated with the given item ID.
     *
     * @param itemId The ID of the navigation item.
     * @return The activity class associated with the given item ID.
     */
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

