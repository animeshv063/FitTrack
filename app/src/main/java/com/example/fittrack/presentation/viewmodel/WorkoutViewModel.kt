package com.example.fittrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrack.data.local.entity.WorkoutEntity
import com.example.fittrack.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.fittrack.data.local.entity.ExerciseEntity


class WorkoutViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {


    val workouts = repository
        .getWorkouts()
        .stateIn(
            scope = viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    fun addWorkout(
        workout: WorkoutEntity
    ) {

        viewModelScope.launch {

            repository.insertWorkout(
                workout
            )

        }
    }
    fun deleteWorkout(
        workout: WorkoutEntity
    ){
        viewModelScope.launch {
            repository.deleteWorkout(
                workout.id
            )
        }
    }
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


    fun deleteExercise(
        exercise: ExerciseEntity
    ) {

        viewModelScope.launch {

            repository.deleteExercise(
                exercise
            )

        }

    }
}