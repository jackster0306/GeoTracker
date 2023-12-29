package com.psyjg14.coursework2.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.psyjg14.coursework2.database.entities.GeofenceEntity;

import java.util.List;

@Dao
public interface GeofenceDao {
    @Query("SELECT * FROM geofenceentity")
    List<GeofenceEntity> getAllGeofences();

    @Query("SELECT * FROM geofenceentity WHERE name = :name")
    GeofenceEntity getGeofenceByName(String name);

    @Insert
    void insertGeofence(GeofenceEntity geofenceEntity);

    @Delete
    void deleteGeofence(GeofenceEntity geofenceEntity);
}
