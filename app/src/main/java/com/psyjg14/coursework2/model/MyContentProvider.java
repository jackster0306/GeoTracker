package com.psyjg14.coursework2.model;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Objects;

/**
 * The MyContentProvider class provides methods to interact with the database.
 */
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

    /**
     * Called when the content provider is created. Initializes the DatabaseRepository.
     *
     * @return True if the provider was successfully loaded, false otherwise.
     */
    @Override
    public boolean onCreate() {
        databaseRepository = new DatabaseRepository((Application) getContext().getApplicationContext());
        return true;
    }

    /**
     * Queries the specified table based on the given URI and returns the result as a Cursor.
     * All parameters except the URI will be null when called.
     *
     * @param uri           The URI to query.
     * @param projection    The list of columns to put into the cursor. If null, all columns are included.
     * @param selection     A selection criteria to apply when filtering rows. If null, all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs.
     * @param sortOrder     How the rows in the cursor should be sorted.
     * @return A Cursor containing the result of the query.
     * @throws IllegalArgumentException if the URI is unknown.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        if (uriMatcher.match(uri) == GEOFENCES_TABLE) {
            cursor = databaseRepository.getAllGeofencesAsCursor();
        } else if (uriMatcher.match(uri) == MOVEMENTS_TABLE) {
            cursor = databaseRepository.getAllMovementsAsCursor();
        } else {
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