package com.psyjg14.coursework2.model;

import android.app.Application;
import android.database.Cursor;
import androidx.lifecycle.LiveData;

import com.psyjg14.coursework2.database.AppDatabase;
import com.psyjg14.coursework2.database.dao.GeofenceDao;
import com.psyjg14.coursework2.database.dao.MovementDao;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.database.entities.MovementEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The DatabaseRepository class is an intermediary between the ViewModels and the Room database.
 * It provides methods for performing various database operations related to Entities in the Database.
 */
public class DatabaseRepository {

    private final GeofenceDao geofenceDao;
    private final MovementDao movementDao;
    private final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    /**
     * Constructor for DatabaseRepository.
     *
     * @param application The Application instance used to get the AppDatabase instance.
     */
    public DatabaseRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabaseInstance(application);
        geofenceDao = db.geofenceDao();
        movementDao = db.movementDao();
    }

    // GeofenceEntity methods

    /**
     * Get a list of all GeofenceEntities.
     *
     * @return LiveData<List<GeofenceEntity>> representing all GeofenceEntities.
     */
    public LiveData<List<GeofenceEntity>> getAllGeofences() {
        return geofenceDao.getAllGeofences();
    }


    /**
     * Get a GeofenceEntity with the specified geofence ID.
     *
     * @param geofenceID The geofence ID of the GeofenceEntity.
     * @return LiveData<GeofenceEntity> representing the GeofenceEntity with the specified geofence ID.
     */
    public LiveData<GeofenceEntity> getGeofenceByID(String geofenceID) {
        return geofenceDao.getGeofenceByID(geofenceID);
    }

    /**
     * Insert a new GeofenceEntity into the database.
     *
     * @param geofenceEntity The GeofenceEntity to be inserted.
     */
    public void insertGeofence(GeofenceEntity geofenceEntity) {
        databaseWriteExecutor.execute(() -> geofenceDao.insertGeofence(geofenceEntity));
    }

    /**
     * Delete a GeofenceEntity from the database.
     *
     * @param geofenceEntity The GeofenceEntity to be deleted.
     */
    public void deleteGeofence(GeofenceEntity geofenceEntity) {
        databaseWriteExecutor.execute(() -> geofenceDao.deleteGeofence(geofenceEntity));
    }

    /**
     * Get all GeofenceEntities as a Cursor.
     * Used by the ContentProvider.
     *
     * @return Cursor containing all GeofenceEntities.
     */
    public Cursor getAllGeofencesAsCursor() {
        return geofenceDao.getAllGeofencesAsCursor();
    }

    /**
     * Update an existing GeofenceEntity in the database.
     *
     * @param geofenceEntity The GeofenceEntity to be updated.
     */
    public void updateGeofence(GeofenceEntity geofenceEntity) {
        databaseWriteExecutor.execute(() -> geofenceDao.updateGeofence(geofenceEntity));
    }



    // MovementEntity methods

    /**
     * Get a list of all MovementEntities.
     *
     * @return LiveData<List<MovementEntity>> representing all MovementEntities.
     */
    public LiveData<List<MovementEntity>> getAllMovements() {
        return movementDao.getAllMovements();
    }

    /**
     * Get a MovementEntity with the specified movement name.
     *
     * @param movementName The name of the MovementEntity.
     * @return LiveData<MovementEntity> representing the MovementEntity with the specified movement name.
     */
    public LiveData<MovementEntity> getMovementById(String movementName) {
        return movementDao.getMovementById(movementName);
    }

    /**
     * Get a list of MovementEntities filtered by type and time range.
     *
     * @param movementType The type of MovementEntity.
     * @param startTime    The start time of the time range.
     * @param endTime      The end time of the time range.
     * @return LiveData<List<MovementEntity>> representing the filtered MovementEntities.
     */
    public LiveData<List<MovementEntity>> getMovementEntitiesByTimeAndType(String movementType, long startTime, long endTime) {
        return movementDao.getMovementEntitiesByTimeAndType(movementType, startTime, endTime);
    }

    /**
     * Get a list of MovementEntities filtered by time range.
     *
     * @param startTime The start time of the time range.
     * @param endTime   The end time of the time range.
     * @return LiveData<List<MovementEntity>> representing the filtered MovementEntities.
     */
    public LiveData<List<MovementEntity>> getMovementEntitiesByTime(long startTime, long endTime) {
        return movementDao.getMovementEntitiesByTime(startTime, endTime);
    }

    /**
     * Get the last MovementEntity in the database.
     *
     * @return LiveData<MovementEntity> representing the last MovementEntity.
     */
    public LiveData<MovementEntity> getLastMovementEntity() {
        return movementDao.getLastEntity();
    }

    /**
     * Insert a new MovementEntity into the database.
     *
     * @param movementEntity The MovementEntity to be inserted.
     */
    public void insertMovement(MovementEntity movementEntity) {
        databaseWriteExecutor.execute(() -> movementDao.insertMovement(movementEntity));
    }

    /**
     * Delete a MovementEntity from the database.
     *
     * @param movementEntity The MovementEntity to be deleted.
     */
    public void deleteMovement(MovementEntity movementEntity) {
        databaseWriteExecutor.execute(() -> movementDao.deleteMovement(movementEntity));
    }

    /**
     * Get all MovementEntities as a Cursor.
     * Used by the ContentProvider.
     *
     * @return Cursor containing all MovementEntities.
     */
    public Cursor getAllMovementsAsCursor() {
        return movementDao.getAllMovementsAsCursor();
    }

    /**
     * Update an existing MovementEntity in the database.
     *
     * @param movementEntity The MovementEntity to be updated.
     */
    public void updateMovement(MovementEntity movementEntity) {
        databaseWriteExecutor.execute(() -> movementDao.updateMovement(movementEntity));
    }
}