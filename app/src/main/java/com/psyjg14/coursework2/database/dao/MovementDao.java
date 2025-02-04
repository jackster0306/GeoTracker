package com.psyjg14.coursework2.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.psyjg14.coursework2.database.entities.MovementEntity;

import java.util.List;

/**
 * Data Access Object (DAO) for interacting with the MovementEntity table in the database.
 */
@Dao
public interface MovementDao {

    /**
     * Retrieve all movements from the database.
     *
     * @return LiveData containing a list of all movements.
     */
    @Query("SELECT * FROM movemententity")
    LiveData<List<MovementEntity>> getAllMovements();

    /**
     * Retrieve a movement by its name.
     *
     * @param movementName The name of the movement.
     * @return LiveData containing the movement with the specified name.
     */
    @Query("SELECT * FROM movemententity WHERE movementName = :movementName")
    LiveData<MovementEntity> getMovementById(String movementName);

    /**
     * Retrieve movements by type and within a specified time range.
     *
     * @param movementType The type of movement.
     * @param startTime    The start timestamp of the time range.
     * @param endTime      The end timestamp of the time range.
     * @return LiveData containing a list of movements within the specified time range and type.
     */
    @Query("SELECT * FROM movemententity WHERE movementType = :movementType AND timestamp >= :startTime AND timestamp <= :endTime")
    LiveData<List<MovementEntity>> getMovementEntitiesByTimeAndType(String movementType, long startTime, long endTime);

    /**
     * Retrieve movements within a specified time range.
     *
     * @param startTime The start timestamp of the time range.
     * @param endTime   The end timestamp of the time range.
     * @return LiveData containing a list of movements within the specified time range.
     */
    @Query("SELECT * FROM movemententity WHERE timestamp >= :startTime AND timestamp <= :endTime")
    LiveData<List<MovementEntity>> getMovementEntitiesByTime(long startTime, long endTime);

    /**
     * Retrieve the last movement entity in the database.
     *
     * @return LiveData containing the last movement entity added.
     */
    @Query("SELECT * FROM movemententity ORDER BY timestamp DESC LIMIT 1")
    LiveData<MovementEntity> getLastEntity();

    /**
     * Insert a new movement entity into the database.
     *
     * @param movement The movement entity to be inserted.
     */
    @Insert
    void insertMovement(MovementEntity movement);

    /**
     * Delete a movement entity from the database.
     *
     * @param movement The movement entity to be deleted.
     * @return The number of movements deleted (should be 1 if successful).
     */
    @Delete
    int deleteMovement(MovementEntity movement);

    /**
     * Retrieve all movements from the database as a Cursor.
     * Used by the ContentProvider.
     *
     * @return A Cursor containing all movements.
     */
    @Query("SELECT * FROM movemententity")
    Cursor getAllMovementsAsCursor();

    /**
     * Update an existing movement entity in the database.
     *
     * @param movementEntity The movement entity to be updated.
     * @return The number of movements updated (should be 1 if successful).
     */
    @Update
    int updateMovement(MovementEntity movementEntity);
}
