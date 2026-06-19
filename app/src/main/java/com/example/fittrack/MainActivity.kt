package com.example.fittrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittrack.data.local.database.FitTrackDatabase
import com.example.fittrack.data.repository.WorkoutRepository
import com.example.fittrack.navigation.AppNavigation
import com.example.fittrack.presentation.theme.FitTrackTheme
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel
import com.example.fittrack.presentation.viewmodel.WorkoutViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        val database = FitTrackDatabase.getDatabase(
            this
        )

        val repository = WorkoutRepository(
            database.workoutDao()
        )

        val factory = WorkoutViewModelFactory(
            repository
        )


        setContent {

            FitTrackTheme {

                val workoutViewModel: WorkoutViewModel =
                    viewModel(
                        factory = factory
                    )

                AppNavigation(
                    workoutViewModel = workoutViewModel
                )

            }
        }
    }
}