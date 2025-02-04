package com.psyjg14.coursework2.database.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Entity class representing a movement in the database.
 */
@Entity(tableName = "movemententity")
public class MovementEntity {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;


    public String movementName;

    public List<LatLng> path;

    public float distanceTravelled;

    public double timeTaken;

    public String movementType;

    public long timeStamp;

    public String note;
}
