package com.psyjg14.coursework2.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.psyjg14.coursework2.database.dao.GeofenceDao;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;

@Database(entities = {GeofenceEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GeofenceDao geofenceDao();
}