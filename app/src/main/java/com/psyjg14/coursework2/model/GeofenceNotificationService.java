package com.psyjg14.coursework2.model;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * The GeofenceNotificationService class is a service responsible for displaying a notification
 * when a geofence transition event occurs.
 */
public class GeofenceNotificationService extends Service {

    /**
     * Called when the service is created.
     *
     * @return Null because this service is not bound.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called when the service is started. It handles the onStartCommand event, including displaying a notification
     * if the intent contains a notification extra.
     *
     * @param intent  The intent passed to the service.
     * @param flags   Additional data about this start request.
     * @param startId A unique identifier for the current start request.
     * @return START_NOT_STICKY, indicating that if the service is killed, it should not be restarted.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("notification")) {
            Notification notification = intent.getParcelableExtra("notification");
            startForeground(2, notification);
        }
        return START_NOT_STICKY;
    }
}
