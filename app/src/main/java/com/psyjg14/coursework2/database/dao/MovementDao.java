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

@Dao
public interface MovementDao {
    @Query("SELECT * FROM movemententity")
    LiveData<List<MovementEntity>> getAllMovements();

    @Query("SELECT * FROM movemententity WHERE movementName = :movementName")
    LiveData<MovementEntity> getMovementById(String movementName);

    @Query("SELECT * FROM movemententity WHERE movementType = :movementType AND timestamp >= :startTime AND timestamp <= :endTime")
    LiveData<List<MovementEntity>> getMovementEntitiesByTimeAndType(String movementType, long startTime, long endTime);

    @Query("SELECT * FROM movemententity WHERE timestamp >= :startTime AND timestamp <= :endTime")
    LiveData<List<MovementEntity>> getMovementEntitiesByTime(long startTime, long endTime);


    @Query("SELECT * FROM movemententity ORDER BY timestamp DESC LIMIT 1")
    LiveData<MovementEntity> getLastEntity();


    @Insert
    void insertMovement(MovementEntity movement);

    @Delete
    int deleteMovement(MovementEntity movement);

    @Query("SELECT * FROM movemententity")
    Cursor getAllMovementsAsCursor();

    @Update
    int updateMovement(MovementEntity movementEntity);


}
