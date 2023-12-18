package com.psyjg14.coursework2;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MyTypeConverters {
    @TypeConverter
    public static List<LatLng> latLngFromString(String value) {
        Type listType = new TypeToken<List<LatLng>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringFromLatLng(List<LatLng> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
