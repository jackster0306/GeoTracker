package com.psyjg14.coursework2.model;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Type converters for Room Database.
 */
public class MyTypeConverters {

    /**
     * Converts a string representation of a List of LatLng to an actual List of LatLng.
     * Used to store a List of LatLng in the database.
     *
     * @param value The string representation of the List of LatLng.
     * @return A List of LatLng.
     */
    @TypeConverter
    public static List<LatLng> latLngFromString(String value) {
        Type listType = new TypeToken<List<LatLng>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    /**
     * Converts a List of LatLng to its string representation.
     * Used to retrieve a List of LatLng from the database.
     *
     * @param list The List of LatLng.
     * @return The string representation of the List of LatLng.
     */
    @TypeConverter
    public static String stringFromLatLng(List<LatLng> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    /**
     * Converts a name to a database-friendly format by replacing spaces with underscores.
     *
     * @param name The name to be converted.
     * @return The database-friendly name.
     */
    public static String nameToDatabaseName(String name) {
        return name.replace(" ", "_");
    }

    /**
     * Converts a database-friendly name to a regular format by replacing underscores with spaces.
     *
     * @param name The database-friendly name to be converted.
     * @return The regular format name.
     */
    public static String nameFromDatabaseName(String name) {
        return name.replace("_", " ");
    }
}
