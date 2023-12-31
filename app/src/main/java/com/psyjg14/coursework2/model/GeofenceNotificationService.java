package com.psyjg14.coursework2.model;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class GeofenceNotificationService extends Service {
    private static final String TAG = "COMP3018";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: GeofenceNotificationService");
        if(intent != null && intent.hasExtra("notification")){
            Log.d(TAG, "onStartCommand: GeofenceNotificationService HAS INTENT");
            Notification notification = intent.getParcelableExtra("notification");
            startForeground(2, notification);
        }
        return START_NOT_STICKY;
    }


}