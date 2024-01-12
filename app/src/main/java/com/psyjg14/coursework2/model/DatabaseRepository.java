package com.psyjg14.coursework2.model;

import android.app.Application;
import android.database.Cursor;
import androidx.lifecycle.LiveData; // Import LiveData

import com.psyjg14.coursework2.database.AppDatabase;
import com.psyjg14.coursework2.database.dao.GeofenceDao;
import com.psyjg14.coursework2.database.dao.MovementDao;
import com.psyjg14.coursework2.database.entities.GeofenceEntity;
import com.psyjg14.coursework2.database.entities.MovementEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseRepository {
    private final GeofenceDao geofenceDao;
    private final MovementDao movementDao;
    private final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public DatabaseRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabaseInstance(application);
        geofenceDao = db.geofenceDao();
        movementDao = db.movementDao();
    }

    // GeofenceEntity methods

    public LiveData<List<GeofenceEntity>> getAllGeofences() {
        return geofenceDao.getAllGeofences();
    }

    public LiveData<GeofenceEntity> getGeofenceByName(String name) {
        return geofenceDao.getGeofenceByName(name);
    }

    public LiveData<GeofenceEntity> getGeofenceByID(String geofenceID) {
        return geofenceDao.getGeofenceByID(geofenceID);
    }

    public void deleteGeofenceByName(String geofenceName) {
        databaseWriteExecutor.execute(() -> geofenceDao.deleteGeofenceByName(geofenceName));
    }

    public void insertGeofence(GeofenceEntity geofenceEntity) {
        databaseWriteExecutor.execute(() -> geofenceDao.insertGeofence(geofenceEntity));
    }

    public void deleteGeofence(GeofenceEntity geofenceEntity) {
        databaseWriteExecutor.execute(() -> geofenceDao.deleteGeofence(geofenceEntity));
    }

    public Cursor getAllGeofencesAsCursor() {
        return geofenceDao.getAllGeofencesAsCursor();
    }

    public void updateGeofence(GeofenceEntity geofenceEntity) {
        databaseWriteExecutor.execute(() -> geofenceDao.updateGeofence(geofenceEntity));
    }

    // MovementEntity methods

    public LiveData<List<MovementEntity>> getAllMovements() {
        return movementDao.getAllMovements();
    }

    public LiveData<MovementEntity> getMovementById(String movementName) {
        return movementDao.getMovementById(movementName);
    }

    public LiveData<List<MovementEntity>> getMovementEntitiesByTimeAndType(String movementType, long startTime, long endTime) {
        return movementDao.getMovementEntitiesByTimeAndType(movementType, startTime, endTime);
    }

    public LiveData<List<MovementEntity>> getMovementEntitiesByTime(long startTime, long endTime) {
        return movementDao.getMovementEntitiesByTime(startTime, endTime);
    }

    public LiveData<MovementEntity> getLastMovementEntity() {
        return movementDao.getLastEntity();
    }

    public void insertMovement(MovementEntity movementEntity) {
        databaseWriteExecutor.execute(() -> movementDao.insertMovement(movementEntity));
    }

    public void deleteMovement(MovementEntity movementEntity) {
        databaseWriteExecutor.execute(() -> movementDao.deleteMovement(movementEntity));
    }

    public Cursor getAllMovementsAsCursor() {
        return movementDao.getAllMovementsAsCursor();
    }

    public void updateMovement(MovementEntity movementEntity) {
        databaseWriteExecutor.execute(() -> movementDao.updateMovement(movementEntity));
    }
}
