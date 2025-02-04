package com.psyjg14.coursework2.database.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class representing a geofence in the database.
 */
@Entity(tableName = "geofenceentity")
public class GeofenceEntity {
    @PrimaryKey
    @NonNull
    public String geofenceID;

    public double latitude;
    public double longitude;
    public float radius;
    public String reminderMessage;
    public int transitionType;
    public String name;
    public String classification;

    public String geofenceNote;
}
