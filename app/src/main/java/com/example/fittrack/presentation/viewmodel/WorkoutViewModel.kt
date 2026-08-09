package com.example.fittrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrack.data.local.entity.ExerciseEntity
import com.example.fittrack.data.local.entity.WorkoutEntity
import com.example.fittrack.data.repository.WorkoutRepository
import com.example.fittrack.data.sensor.StepCounterManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val repository: WorkoutRepository,
    private val stepCounterManager: StepCounterManager
) : ViewModel() {

    // -------------------------
    // STEPS
    // -------------------------

    val steps =
        stepCounterManager.steps


    // -------------------------
    // WORKOUTS
    // -------------------------

    val workouts =
        repository
            .getWorkouts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )


    // -------------------------
    // ALL EXERCISES
    // -------------------------

    val allExercises =
        repository
            .getAllExercises()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )


    // -------------------------
    // WORKOUT ACTIONS
    // -------------------------

    fun addWorkout(
        workout: WorkoutEntity
    ) {

        viewModelScope.launch {

            repository.insertWorkout(
                workout
            )
        }
    }


    fun updateWorkout(
        workout: WorkoutEntity
    ) {

        viewModelScope.launch {

            repository.updateWorkout(
                workout
            )
        }
    }


    fun deleteWorkout(
        workout: WorkoutEntity
    ) {

        viewModelScope.launch {

            repository.deleteWorkout(
                workout.id
            )
        }
    }


    fun completeWorkout(
        workoutId: Int
    ) {

        viewModelScope.launch {

            repository.setWorkoutCompleted(
                workoutId,
                true
            )
        }
    }


    fun deleteAllWorkouts() {

        viewModelScope.launch {

            repository.deleteAllWorkouts()
        }
    }


    // -------------------------
    // EXERCISES
    // -------------------------

    fun getExercises(
        workoutId: Int
    ) =
        repository.getExercises(
            workoutId
        )


    fun addExercise(
        exercise: ExerciseEntity
    ) {

        viewModelScope.launch {

            repository.insertExercise(
                exercise
            )
        }
    }


    fun updateExercise(
        exercise: ExerciseEntity
    ) {

        viewModelScope.launch {

            repository.updateExercise(
                exercise
            )
        }
    }


    fun deleteExercise(
        exercise: ExerciseEntity
    ) {

        viewModelScope.launch {

            repository.deleteExercise(
                exercise
            )
        }
    }


    fun updateCompletedSets(
        exerciseId: Int,
        completedSets: Int
    ) {

        viewModelScope.launch {

            repository.updateCompletedSets(
                exerciseId,
                completedSets
            )
        }
    }


    // -------------------------
    // SENSOR
    // -------------------------

    fun startStepCounter() {

        stepCounterManager.start()
    }
}