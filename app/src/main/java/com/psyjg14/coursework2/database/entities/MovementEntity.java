package com.psyjg14.coursework2.database.entities;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.psyjg14.coursework2.MyTypeConverters;

import java.util.List;

@Entity(tableName = "movemententity")
public class MovementEntity {
    @NonNull
    @PrimaryKey
    public String movementName;

    public List<LatLng> path;

    public float distanceTravelled;

    public double timeTaken;

    public String movementType;

    public long timeStamp;

    public static MovementEntity fromContentValues(ContentValues values){
        MovementEntity movementEntity = new MovementEntity();
        if(values.containsKey("movementName")){
            movementEntity.movementName = values.getAsString("movementName");
        }
        if(values.containsKey("path")){
            if(values.getAsString("path") != null){
                movementEntity.path = MyTypeConverters.latLngFromString(values.getAsString("path"));
            }
        }
        if(values.containsKey("distanceTravelled")){
            movementEntity.distanceTravelled = values.getAsFloat("distanceTravelled");
        }
        if(values.containsKey("timeTaken")){
            movementEntity.timeTaken = values.getAsDouble("timeTaken");
        }
        if(values.containsKey("movementType")){
            movementEntity.movementType = values.getAsString("movementType");
        }
        if(values.containsKey("timeStamp")){
            movementEntity.timeStamp = values.getAsLong("timeStamp");
        }
        return movementEntity;
    }
}
