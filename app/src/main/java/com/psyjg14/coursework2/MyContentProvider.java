package com.psyjg14.coursework2;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.psyjg14.coursework2.model.DatabaseRepository;

import java.util.Objects;

public class MyContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.psyjg14.coursework2.provider";
    private static final String GEOFENCES_PATH = "geofence_table";
    private static final String MOVEMENTS_PATH = "movement_table";
    private static final int GEOFENCES_TABLE = 1;
    private static final int MOVEMENTS_TABLE = 2;

    public static final Uri GEOFENCES_URI = Uri.parse("content://" + AUTHORITY + "/" + GEOFENCES_PATH);
    public static final Uri MOVEMENTS_URI = Uri.parse("content://" + AUTHORITY + "/" + MOVEMENTS_PATH);

    private DatabaseRepository databaseRepository;


    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        uriMatcher.addURI(AUTHORITY, GEOFENCES_PATH, 1);
        uriMatcher.addURI(AUTHORITY, MOVEMENTS_PATH, 2);
    }

    @Override
    public boolean onCreate() {
        Log.d("COMP3018", "**************************************88CONTENT PROVIDER CREATED");
        databaseRepository = new DatabaseRepository((Application) getContext().getApplicationContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        if(uriMatcher.match(uri) == GEOFENCES_TABLE) {
            cursor = databaseRepository.getAllGeofencesAsCursor();
        }else if(uriMatcher.match(uri) == MOVEMENTS_TABLE){
            cursor = databaseRepository.getAllMovementsAsCursor();
        }else{
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}