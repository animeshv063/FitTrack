package com.example.fittrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fittrack.data.repository.WorkoutRepository
import com.example.fittrack.data.sensor.StepCounterManager

class WorkoutViewModelFactory(
    private val repository: WorkoutRepository,
    private val stepCounterManager: StepCounterManager
) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        return WorkoutViewModel(
            repository,
            stepCounterManager
        ) as T

    }
}

