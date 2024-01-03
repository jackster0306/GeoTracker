package com.psyjg14.coursework2;

import android.content.Context;

import androidx.room.Room;

import com.psyjg14.coursework2.database.AppDatabase;

public class DatabaseSingleton {
    private static AppDatabase database;

    public static AppDatabase getDatabaseInstance(Context context){
        if(database == null){
            database = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "MDPDatabase").build();
        }
        return database;
    }
}
