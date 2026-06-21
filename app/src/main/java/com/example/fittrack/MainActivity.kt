package com.example.fittrack


import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittrack.data.local.database.FitTrackDatabase
import com.example.fittrack.data.repository.WorkoutRepository
import com.example.fittrack.data.sensor.StepCounterManager
import com.example.fittrack.navigation.AppNavigation
import com.example.fittrack.presentation.theme.FitTrackTheme
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel
import com.example.fittrack.presentation.viewmodel.WorkoutViewModelFactory


class MainActivity : ComponentActivity() {


    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {

            startApp()

        }


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            permissionLauncher.launch(
                Manifest.permission.ACTIVITY_RECOGNITION
            )

        } else {

            startApp()

        }

    }


    private fun startApp() {


        val database =
            FitTrackDatabase.getDatabase(
                this
            )


        val stepCounterManager =
            StepCounterManager(
                this
            )


        val repository =
            WorkoutRepository(
                database.workoutDao()
            )


        val factory =
            WorkoutViewModelFactory(
                repository,
                stepCounterManager
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