package com.example.fittrack.data.repository

import com.example.fittrack.data.local.dao.WorkoutDao
import com.example.fittrack.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow
import com.example.fittrack.data.local.entity.ExerciseEntity

class WorkoutRepository(
    private val workoutDao: WorkoutDao
) {

    fun getWorkouts(): Flow<List<WorkoutEntity>> {
        return workoutDao.getWorkouts()
    }


    suspend fun insertWorkout(
        workout: WorkoutEntity
    ) {
        workoutDao.insertWorkout(workout)
    }


    suspend fun deleteWorkout(
        id: Int
    ) {
        workoutDao.deleteWorkout(id)
    }

    fun getExercises(
        workoutId: Int
    ): Flow<List<ExerciseEntity>> {

        return workoutDao.getExercises(
            workoutId
        )

    }


    suspend fun insertExercise(
        exercise: ExerciseEntity
    ) {

        workoutDao.insertExercise(
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

    fun getAllExercises() =
        workoutDao.getAllExercises()

}

