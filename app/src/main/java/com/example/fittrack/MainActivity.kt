package com.example.fittrack

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittrack.data.local.database.FitTrackDatabase
import com.example.fittrack.data.repository.WorkoutRepository
import com.example.fittrack.data.sensor.StepCounterManager
import com.example.fittrack.data.sensor.StepTrackerService
import com.example.fittrack.navigation.AppNavigation
import com.example.fittrack.presentation.theme.FitTrackTheme
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel
import com.example.fittrack.presentation.viewmodel.WorkoutViewModelFactory

class MainActivity : ComponentActivity() {

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            StepTrackerService.stopService(this)
            startApp()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Ensure background step notification service is terminated
        StepTrackerService.stopService(this)

        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            startApp()
        }
    }

    private fun startApp() {
        val database = FitTrackDatabase.getDatabase(this)
        val stepCounterManager = StepCounterManager(this)
        val repository = WorkoutRepository(database.workoutDao())
        val factory = WorkoutViewModelFactory(repository, stepCounterManager)

        setContent {
            FitTrackTheme {
                val workoutViewModel: WorkoutViewModel = viewModel(factory = factory)
                AppNavigation(workoutViewModel = workoutViewModel)
            }
        }
    }
}