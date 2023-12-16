package com.psyjg14.coursework2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "COMP3018";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: GeofenceBroadcastReceiver");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Add a null check to handle potential null geofencingEvent
        if (geofencingEvent == null) {
            Log.e(TAG, "onReceive: GeofencingEvent is null");
            return;
        }

        if (geofencingEvent.hasError()) {
            Log.e(TAG, "onReceive: Error receiving geofence event: " + geofencingEvent.getErrorCode());
            return;
        }

        int transition = geofencingEvent.getGeofenceTransition();

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d(TAG, "onReceive: ENTER");
        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d(TAG, "onReceive: EXIT");
        } else {
            Log.d(TAG, "onReceive: transition: " + transition);
        }
    }
}
