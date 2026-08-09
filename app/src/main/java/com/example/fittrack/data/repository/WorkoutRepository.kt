package com.example.fittrack.data.repository

import com.example.fittrack.data.local.dao.WorkoutDao
import com.example.fittrack.data.local.entity.ExerciseEntity
import com.example.fittrack.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val workoutDao: WorkoutDao
) {

    // -------------------------
    // WORKOUTS
    // -------------------------

    fun getWorkouts(): Flow<List<WorkoutEntity>> {
        return workoutDao.getWorkouts()
    }

    suspend fun getWorkout(
        id: Int
    ): WorkoutEntity? {
        return workoutDao.getWorkout(id)
    }

    suspend fun insertWorkout(
        workout: WorkoutEntity
    ) {
        workoutDao.insertWorkout(workout)
    }

    suspend fun updateWorkout(
        workout: WorkoutEntity
    ) {
        workoutDao.updateWorkout(workout)
    }

    suspend fun deleteWorkout(
        id: Int
    ) {
        workoutDao.deleteWorkout(id)
    }

    suspend fun setWorkoutCompleted(
        id: Int,
        completed: Boolean
    ) {
        workoutDao.setWorkoutCompleted(
            id,
            completed
        )
    }

    suspend fun deleteAllWorkouts() {
        workoutDao.deleteAllWorkouts()
    }


    // -------------------------
    // EXERCISES
    // -------------------------

    fun getExercises(
        workoutId: Int
    ): Flow<List<ExerciseEntity>> {

        return workoutDao.getExercises(
            workoutId
        )
    }

    fun getAllExercises(): Flow<List<ExerciseEntity>> {
        return workoutDao.getAllExercises()
    }

    suspend fun getExercise(
        id: Int
    ): ExerciseEntity? {

        return workoutDao.getExercise(id)
    }

    suspend fun insertExercise(
        exercise: ExerciseEntity
    ) {

        workoutDao.insertExercise(
            exercise
        )
    }

    suspend fun updateExercise(
        exercise: ExerciseEntity
    ) {

        workoutDao.updateExercise(
            exercise
        )
    }

    suspend fun deleteExercise(
        exercise: ExerciseEntity
    ) {

        workoutDao.deleteExercise(
            exercise
        )
    }

    suspend fun updateCompletedSets(
        exerciseId: Int,
        completedSets: Int
    ) {

        workoutDao.updateCompletedSets(
            exerciseId,
            completedSets
        )
    }

    suspend fun deleteExercisesForWorkout(
        workoutId: Int
    ) {

        workoutDao.deleteExercisesForWorkout(
            workoutId
        )
    }
}