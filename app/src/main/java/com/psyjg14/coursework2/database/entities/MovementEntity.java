package com.psyjg14.coursework2.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

@Entity(tableName = "movemententity")
public class MovementEntity {
    @NonNull
    @PrimaryKey
    public String movementName;

    public List<LatLng> path;

    public float distanceTravelled;

    public double timeTaken;
}
