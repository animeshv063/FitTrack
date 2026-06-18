package com.example.fittrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fittrack.domain.model.Exercise
import com.example.fittrack.domain.model.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WorkoutViewModel : ViewModel() {

    private val _workouts = MutableStateFlow(
        listOf(
            Workout(
                id = 1,
                name = "Push Day",
                duration = 45,
                exercises = listOf(
                    Exercise(
                        name = "Bench Press",
                        sets = 3,
                        reps = 10,
                        weight = 60
                    )
                )
            )
        )
    )

    val workouts: StateFlow<List<Workout>> = _workouts

}

