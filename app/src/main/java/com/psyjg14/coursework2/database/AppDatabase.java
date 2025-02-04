package com.psyjg14.coursework2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.psyjg14.coursework2.model.MyTypeConverters;
import com.psyjg14.coursework2.database.dao.GeofenceDao;
import com.psyjg14.coursework2.database.dao.MovementDao;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.database.entities.MovementEntity;

/**
 * Room database class that provides access to the DAOs and creates a singleton database instance.
 */
@Database(entities = {GeofenceEntity.class, MovementEntity.class}, version = 1, exportSchema = false)
@TypeConverters(MyTypeConverters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    /**
     * Get the singleton instance of the AppDatabase.
     *
     * @param context Application context.
     * @return The singleton instance of AppDatabase.
     */
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

    /**
     * Get the GeofenceDao for database operations related to GeofenceEntity.
     *
     * @return GeofenceDao instance.
     */
    public abstract GeofenceDao geofenceDao();

    /**
     * Get the MovementDao for database operations related to MovementEntity.
     *
     * @return MovementDao instance.
     */
    public abstract MovementDao movementDao();
}
