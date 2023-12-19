package com.psyjg14.coursework2.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.psyjg14.coursework2.database.entities.MovementEntity;

import java.util.List;

@Dao
public interface MovementDao {
    @Query("SELECT * FROM movemententity")
    List<MovementEntity> getAllMovements();

    @Query("SELECT * FROM movemententity WHERE movementName = :movementName")
    MovementEntity getMovementById(String movementName);

    @Query("SELECT * FROM movemententity WHERE movementType = :movementType AND timestamp >= :startTime AND timestamp <= :endTime")
    List<MovementEntity> getMovementEntitiesByTimeAndType(String movementType, long startTime, long endTime);

    @Query("SELECT * FROM movemententity WHERE timestamp >= :startTime AND timestamp <= :endTime")
    List<MovementEntity> getMovementEntitiesByTime(long startTime, long endTime);


    @Query("SELECT * FROM movemententity ORDER BY timestamp DESC LIMIT 1")
    MovementEntity getLastEntity();


    @Insert
    void insertMovement(MovementEntity movement);
}
