package com.example.fittrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fittrack.data.local.entity.ExerciseEntity
import com.example.fittrack.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // -------------------------
    // WORKOUTS
    // -------------------------

    @Insert
    suspend fun insertWorkout(
        workout: WorkoutEntity
    )

    @Update
    suspend fun updateWorkout(
        workout: WorkoutEntity
    )

    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getWorkouts(): Flow<List<WorkoutEntity>>

    @Query(
        "SELECT * FROM workouts WHERE id = :id LIMIT 1"
    )
    suspend fun getWorkout(
        id: Int
    ): WorkoutEntity?

    @Query(
        """
        UPDATE workouts
        SET completed = :completed
        WHERE id = :id
        """
    )
    suspend fun setWorkoutCompleted(
        id: Int,
        completed: Boolean
    )

    @Query(
        "DELETE FROM workouts WHERE id = :id"
    )
    suspend fun deleteWorkout(
        id: Int
    )

    @Query("DELETE FROM workouts")
    suspend fun deleteAllWorkouts()


    // -------------------------
    // EXERCISES
    // -------------------------

    @Insert
    suspend fun insertExercise(
        exercise: ExerciseEntity
    )

    @Update
    suspend fun updateExercise(
        exercise: ExerciseEntity
    )

    @Delete
    suspend fun deleteExercise(
        exercise: ExerciseEntity
    )

    @Query(
        """
        SELECT * FROM exercises
        WHERE workoutId = :workoutId
        ORDER BY id ASC
        """
    )
    fun getExercises(
        workoutId: Int
    ): Flow<List<ExerciseEntity>>

    @Query(
        """
        SELECT * FROM exercises
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getExercise(
        id: Int
    ): ExerciseEntity?

    @Query("SELECT * FROM exercises ORDER BY id DESC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query(
        """
        UPDATE exercises
        SET completedSets = :completedSets
        WHERE id = :exerciseId
        """
    )
    suspend fun updateCompletedSets(
        exerciseId: Int,
        completedSets: Int
    )

    @Query(
        """
        DELETE FROM exercises
        WHERE workoutId = :workoutId
        """
    )
    suspend fun deleteExercisesForWorkout(
        workoutId: Int
    )
}