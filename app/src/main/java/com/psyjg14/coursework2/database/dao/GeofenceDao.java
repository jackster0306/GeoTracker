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

/**
 * Data Access Object (DAO) for interacting with the GeofenceEntity table in the database.
 */
@Dao
public interface GeofenceDao {

    /**
     * Retrieve all geofences from the database as LiveData.
     *
     * @return LiveData containing a list of all geofences.
     */
    @Query("SELECT * FROM geofenceentity")
    LiveData<List<GeofenceEntity>> getAllGeofences();

    /**
     * Retrieve a geofence by its name.
     *
     * @param name The name of the geofence.
     * @return LiveData containing the geofence with the specified name.
     */
    @Query("SELECT * FROM geofenceentity WHERE name = :name")
    LiveData<GeofenceEntity> getGeofenceByName(String name);

    /**
     * Retrieve a geofence by its ID.
     *
     * @param geofenceID The ID of the geofence.
     * @return LiveData containing the geofence with the specified ID.
     */
    @Query("SELECT * FROM geofenceentity WHERE geofenceID = :geofenceID")
    LiveData<GeofenceEntity> getGeofenceByID(String geofenceID);

    /**
     * Delete a geofence by its name.
     *
     * @param geofenceName The name of the geofence to be deleted.
     * @return The number of geofences deleted (should be 1 if successful).
     */
    @Query("DELETE FROM geofenceentity WHERE name = :geofenceName")
    int deleteGeofenceByName(String geofenceName);

    /**
     * Insert a new geofence into the database.
     *
     * @param geofenceEntity The geofence entity to be inserted.
     */
    @Insert
    void insertGeofence(GeofenceEntity geofenceEntity);

    /**
     * Delete a geofence from the database.
     *
     * @param geofenceEntity The geofence entity to be deleted.
     */
    @Delete
    void deleteGeofence(GeofenceEntity geofenceEntity);

    /**
     * Retrieve all geofences from the database as a Cursor.
     *
     * @return A Cursor containing all geofences.
     */
    @Query("SELECT * FROM geofenceentity")
    Cursor getAllGeofencesAsCursor();

    /**
     * Update an existing geofence in the database.
     *
     * @param geofenceEntity The geofence entity to be updated.
     * @return The number of geofences updated (should be 1 if successful).
     */
    @Update
    int updateGeofence(GeofenceEntity geofenceEntity);
}
