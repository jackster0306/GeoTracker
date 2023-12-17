package com.psyjg14.coursework2.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "geofenceentity")
public class GeofenceEntity {
    @PrimaryKey(autoGenerate = true)
    public long geofenceID;

    public double latitude;
    public double longitude;
    public float radius;
    public String reminderMessage;
    public int transitionType;
}
