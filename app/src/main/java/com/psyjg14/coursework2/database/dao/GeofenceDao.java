package com.psyjg14.coursework2.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.psyjg14.coursework2.database.entities.GeofenceEntity;

import java.util.List;

@Dao
public interface GeofenceDao {
    @Query("SELECT * FROM geofenceentity")
    List<GeofenceEntity> getAllGeofences();

    @Insert
    void insertGeofence(GeofenceEntity geofenceEntity);
}
