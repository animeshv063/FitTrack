package com.example.fittrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.fittrack.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow
import com.example.fittrack.data.local.entity.ExerciseEntity

@Dao
interface WorkoutDao {

    @Insert
    suspend fun insertWorkout(
        workout: WorkoutEntity
    )

    @Insert
    suspend fun insertExercise(
        exercise: ExerciseEntity
    )

    @Query(
        "SELECT * FROM exercises WHERE workoutId = :workoutId"
    )
    fun getExercises(
        workoutId: Int
    ): Flow<List<ExerciseEntity>>
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

    @Delete
    suspend fun deleteExercise(
        exercise: ExerciseEntity
    )
}

