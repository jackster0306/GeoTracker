package com.psyjg14.coursework2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.psyjg14.coursework2.MyTypeConverters;
import com.psyjg14.coursework2.database.dao.GeofenceDao;
import com.psyjg14.coursework2.database.dao.MovementDao;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.database.entities.MovementEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {GeofenceEntity.class, MovementEntity.class}, version = 1, exportSchema = false)
@TypeConverters(MyTypeConverters.class)
public abstract class AppDatabase extends RoomDatabase {
    private static final int threadCount = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(threadCount);

    private static volatile AppDatabase instance;

    public static AppDatabase getDatabaseInstance(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "MDPDatabase")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract GeofenceDao geofenceDao();
    public abstract MovementDao movementDao();


}