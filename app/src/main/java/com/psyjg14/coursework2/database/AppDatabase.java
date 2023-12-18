package com.psyjg14.coursework2.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.psyjg14.coursework2.MyTypeConverters;
import com.psyjg14.coursework2.database.dao.GeofenceDao;
import com.psyjg14.coursework2.database.dao.MovementDao;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.database.entities.MovementEntity;

@Database(entities = {GeofenceEntity.class, MovementEntity.class}, version = 1)
@TypeConverters(MyTypeConverters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GeofenceDao geofenceDao();
    public abstract MovementDao movementDao();
}