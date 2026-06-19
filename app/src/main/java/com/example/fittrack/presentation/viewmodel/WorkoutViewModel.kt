package com.example.fittrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrack.data.local.entity.WorkoutEntity
import com.example.fittrack.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
}