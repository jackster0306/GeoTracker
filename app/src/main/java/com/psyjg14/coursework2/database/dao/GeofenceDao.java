package com.psyjg14.coursework2.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.psyjg14.coursework2.database.entities.GeofenceEntity;

import java.util.List;

@Dao
public interface GeofenceDao {
    @Query("SELECT * FROM geofenceentity")
    LiveData<List<GeofenceEntity>> getAllGeofences();

    @Query("SELECT * FROM geofenceentity WHERE name = :name")
    LiveData<GeofenceEntity> getGeofenceByName(String name);

    @Query("SELECT * FROM geofenceentity WHERE geofenceID = :geofenceID")
    LiveData<GeofenceEntity> getGeofenceByID(String geofenceID);

    @Query("DELETE FROM geofenceentity WHERE name = :geofenceName")
    int deleteGeofenceByName(String geofenceName);

    @Insert
    void insertGeofence(GeofenceEntity geofenceEntity);

    @Delete
    void deleteGeofence(GeofenceEntity geofenceEntity);

    @Query("SELECT * FROM geofenceentity")
    Cursor getAllGeofencesAsCursor();

    @Update
    int updateGeofence(GeofenceEntity geofenceEntity);
}
