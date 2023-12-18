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

    @Insert
    void insertMovement(MovementEntity movement);
}
