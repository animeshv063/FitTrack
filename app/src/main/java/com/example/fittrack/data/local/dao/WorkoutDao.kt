package com.example.fittrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fittrack.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert
    suspend fun insertWorkout(
        workout: WorkoutEntity
    )


    @Query(
        "SELECT * FROM workouts"
    )
    fun getWorkouts(): Flow<List<WorkoutEntity>>


    @Query(
        "DELETE FROM workouts WHERE id = :id"
    )
    suspend fun deleteWorkout(
        id: Int
    )
}

