package com.psyjg14.coursework2.model;

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
import androidx.room.Room;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.psyjg14.coursework2.database.AppDatabase;
import com.psyjg14.coursework2.database.dao.GeofenceDao;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.view.MainActivity;

import java.util.Objects;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "COMP3018";
    private static final String CHANNEL_ID = "Geofence Channel";

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
            Log.e(TAG, "onReceive: ENTER");
            new Thread(() -> {
                Log.d(TAG, "onReceive: THREAD");
                AppDatabase db = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "MDPDatabase").build();
                GeofenceDao geofenceDao = db.geofenceDao();

                Geofence geofenceTriggered = Objects.requireNonNull(geofencingEvent.getTriggeringGeofences()).get(0);
                GeofenceEntity geofenceEntity = geofenceDao.getGeofenceByID(geofenceTriggered.getRequestId());
                if(geofenceEntity != null){
                    String geofenceName = geofenceEntity.name;
                    String geofenceClassification = geofenceEntity.classification;
                    String geofenceNote = geofenceEntity.geofenceNote;

                    new Handler(Looper.getMainLooper()).post(() -> handleStartForegroundNotification(context, geofenceName, geofenceClassification, geofenceNote));
                }
                Log.d(TAG, "THREAD, geofenceEntity: " + geofenceEntity);
            }).start();
            Log.d(TAG, "onReceive: ENTER");
        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d(TAG, "onReceive: EXIT");
            context.stopService(new Intent(context, GeofenceNotificationService.class));
        } else {
            Log.d(TAG, "onReceive: transition: " + transition);
        }
    }

    private void handleStartForegroundNotification(Context context, String geofenceName, String geofenceClassification, String geofenceNote){
        Log.d(TAG, "handleStartForegroundNotification: " + geofenceName + " " + geofenceClassification + " " + geofenceNote);
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
        Log.d(TAG, "STARTING SERVICE");
        context.startService(serviceIntent);
    }
}
