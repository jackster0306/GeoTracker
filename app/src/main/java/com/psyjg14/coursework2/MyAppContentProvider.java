package com.psyjg14.coursework2;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import com.psyjg14.coursework2.database.AppDatabase;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.database.entities.MovementEntity;

import java.util.Objects;

public class MyAppContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.psyjg14.coursework2";
    private static final String GEOFENCES_PATH = "geofence";
    private static final String MOVEMENTS_PATH = "movement";
    private static final int GEOFENCES_TABLE = 1;
    private static final int MOVEMENTS_TABLE = 2;


    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        uriMatcher.addURI(AUTHORITY, GEOFENCES_PATH, GEOFENCES_TABLE);
        uriMatcher.addURI(AUTHORITY, MOVEMENTS_PATH, MOVEMENTS_TABLE);
    }

    private AppDatabase appDatabase;
    @Override
    public boolean onCreate() {
        appDatabase = Room.databaseBuilder(Objects.requireNonNull(getContext()), AppDatabase.class, "MDPDatabase").build();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        if(uriMatcher.match(uri) == GEOFENCES_TABLE) {
            cursor = appDatabase.geofenceDao().getAllGeofencesAsCursor();
        }else if(uriMatcher.match(uri) == MOVEMENTS_TABLE){
            cursor = appDatabase.movementDao().getAllMovementsAsCursor();
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
        long id;
        if(uriMatcher.match(uri) == GEOFENCES_TABLE) {
            id = appDatabase.geofenceDao().insertGeofenceAndGetId(GeofenceEntity.fromContentValues(values));
        }else if(uriMatcher.match(uri) == MOVEMENTS_TABLE){
            id = appDatabase.movementDao().insertMovementAndGetId(MovementEntity.fromContentValues(values));
        } else{
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deleted;
        if(uriMatcher.match(uri) == GEOFENCES_TABLE) {
            deleted = appDatabase.geofenceDao().deleteGeofenceByName(uri.getLastPathSegment());
        }else if(uriMatcher.match(uri) == MOVEMENTS_TABLE){
            MovementEntity movementEntity = new MovementEntity();
            movementEntity.movementName = Objects.requireNonNull(uri.getLastPathSegment());
            deleted = appDatabase.movementDao().deleteMovement(movementEntity);
        }
        else{
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if(deleted > 0){
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int updated;
        if(uriMatcher.match(uri) == GEOFENCES_TABLE) {
            updated = appDatabase.geofenceDao().updateGeofence(GeofenceEntity.fromContentValues(values));
        }else if(uriMatcher.match(uri) == MOVEMENTS_TABLE){
            updated = appDatabase.movementDao().updateMovement(MovementEntity.fromContentValues(values));
        }
        else{
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return updated;
    }
}
