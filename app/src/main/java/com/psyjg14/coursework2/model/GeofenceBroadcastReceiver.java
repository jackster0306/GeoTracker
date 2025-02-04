package com.psyjg14.coursework2.model;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.view.MainActivity;

import java.util.Objects;

/**
 * The GeofenceBroadcastReceiver class handles geofence transition events.
 * It receives geofencing events and responds accordingly by showing notifications.
 * The notifications are displayed when the device enters a geofence and are removed when it exits.
 */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "COMP3018";
    private static final String CHANNEL_ID = "Geofence Channel";

    /**
     * This method is called when a geofence transition event is received.
     * It handles entering and exiting geofences by showing notifications by starting/stopping the GeofenceNotificationService.
     *
     * @param context The application context.
     * @param intent  The intent containing the geofence transition event.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

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
            DatabaseRepository databaseRepository = new DatabaseRepository((Application) context.getApplicationContext());
            LiveData<GeofenceEntity> liveData = databaseRepository.getGeofenceByID(Objects.requireNonNull(Objects.requireNonNull(geofencingEvent.getTriggeringGeofences()).get(0)).getRequestId());
            liveData.observeForever(new Observer<GeofenceEntity>() {
                @Override
                public void onChanged(GeofenceEntity geofenceEntity) {
                    if (geofenceEntity != null) {
                        String geofenceName = geofenceEntity.name;
                        String geofenceClassification = geofenceEntity.classification;
                        String geofenceNote = geofenceEntity.geofenceNote;

                        handleStartForegroundNotification(context, geofenceName, geofenceClassification, geofenceNote);
                        new Handler(Looper.getMainLooper()).post(() -> handleStartForegroundNotification(context, geofenceName, geofenceClassification, geofenceNote));
                    }
                    liveData.removeObserver(this);
                }
            });

        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            context.stopService(new Intent(context, GeofenceNotificationService.class));
        } else {
            Log.e(TAG, "onReceive: Invalid transition type");
        }
    }

    /**
     * Helper method to handle the creation of a foreground notification.
     * It creates a notification channel, builds a notification, and starts the GeofenceNotificationService.
     *
     * @param context              The application context.
     * @param geofenceName         The name of the geofence.
     * @param geofenceClassification The classification of the geofence.
     * @param geofenceNote         The note associated with the geofence.
     */
    private void handleStartForegroundNotification(Context context, String geofenceName, String geofenceClassification, String geofenceNote) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Geofence Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Geofence Notification")
                .setContentText("You have entered " + geofenceName + " (" + geofenceClassification + ")\n Your note: " + geofenceNote)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .build();

        Intent serviceIntent = new Intent(context, GeofenceNotificationService.class);
        serviceIntent.putExtra("notification", notification);
        context.startService(serviceIntent);
    }
}
