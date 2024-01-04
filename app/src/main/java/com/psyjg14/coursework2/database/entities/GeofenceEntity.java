package com.psyjg14.coursework2.database.entities;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    public static GeofenceEntity fromContentValues(ContentValues values){
        GeofenceEntity geofenceEntity = new GeofenceEntity();
        if(values.containsKey("geofenceID")){
            geofenceEntity.geofenceID = values.getAsString("geofenceID");
        }
        if(values.containsKey("latitude")){
            geofenceEntity.latitude = values.getAsDouble("latitude");
        }
        if(values.containsKey("longitude")){
            geofenceEntity.longitude = values.getAsDouble("longitude");
        }
        if(values.containsKey("radius")){
            geofenceEntity.radius = values.getAsFloat("radius");
        }
        if(values.containsKey("reminderMessage")){
            geofenceEntity.reminderMessage = values.getAsString("reminderMessage");
        }
        if(values.containsKey("transitionType")){
            geofenceEntity.transitionType = values.getAsInteger("transitionType");
        }
        if(values.containsKey("name")){
            geofenceEntity.name = values.getAsString("name");
        }
        if(values.containsKey("classification")){
            geofenceEntity.classification = values.getAsString("classification");
        }
        if(values.containsKey("geofenceNote")){
            geofenceEntity.geofenceNote = values.getAsString("geofenceNote");
        }
        return geofenceEntity;
    }
}
