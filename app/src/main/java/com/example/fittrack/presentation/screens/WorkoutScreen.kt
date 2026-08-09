package com.example.fittrack.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fittrack.data.local.entity.WorkoutEntity
import com.example.fittrack.presentation.theme.BackgroundDark
import com.example.fittrack.presentation.theme.PrimaryGreen
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextWhite
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel

@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel,
    navController: NavController
) {

    val workouts by viewModel
        .workouts
        .collectAsState()

    var workoutName by remember {
        mutableStateOf("")
    }

    var workoutDuration by remember {
        mutableStateOf("")
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var workoutToDelete by remember {
        mutableStateOf<WorkoutEntity?>(null)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(20.dp)
    ) {

        Text(
            text = "Workouts",
            color = TextWhite,
            fontSize = 28.sp
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = workoutName,
            onValueChange = {
                workoutName = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Workout Name")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedTextField(
            value = workoutDuration,
            onValueChange = {
                workoutDuration = it.filter { char ->
                    char.isDigit()
                }
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Duration (minutes)")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (errorMessage.isNotEmpty()) {

            Text(
                text = errorMessage,
                color = androidx.compose.ui.graphics.Color.Red
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        Button(
            onClick = {

                val duration =
                    workoutDuration.toIntOrNull()

                when {
                    workoutName.isBlank() -> {
                        errorMessage =
                            "Enter a workout name"
                    }

                    duration == null ||
                            duration <= 0 -> {
                        errorMessage =
                            "Enter a valid duration"
                    }

                    else -> {

                        viewModel.addWorkout(
                            WorkoutEntity(
                                name = workoutName.trim(),
                                duration = duration,
                                date = System.currentTimeMillis()
                            )
                        )

                        workoutName = ""
                        workoutDuration = ""
                        errorMessage = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Add Workout")
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (workouts.isEmpty()) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "💪",
                    fontSize = 48.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "No workouts yet",
                    color = TextWhite,
                    fontSize = 22.sp
                )

                Text(
                    text = "Create your first workout to start tracking your progress.",
                    color = TextGray
                )
            }

        } else {

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = workouts,
                    key = {
                        it.id
                    }
                ) { workout ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = workout.name,
                                    color = TextWhite,
                                    fontSize = 20.sp
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text =
                                        "${workout.duration} minutes",
                                    color = PrimaryGreen
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text =
                                        if (workout.completed)
                                            "✓ Completed"
                                        else
                                            "Not completed",
                                    color =
                                        if (workout.completed)
                                            PrimaryGreen
                                        else
                                            TextGray
                                )
                            }

                            Row {

                                IconButton(
                                    onClick = {

                                        workoutToDelete =
                                            workout

                                        showDeleteDialog =
                                            true
                                    }
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.Delete,
                                        contentDescription =
                                            "Delete workout"
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {

                                navController.navigate(
                                    "workout_detail/${workout.id}"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 16.dp
                                )
                        ) {

                            Text("Open Workout")
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Delete Workout?")
            },
            text = {
                Text(
                    "This will delete the workout and all its exercises."
                )
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        workoutToDelete?.let {
                            viewModel.deleteWorkout(it)
                        }

                        workoutToDelete = null
                        showDeleteDialog = false
                    }
                ) {

                    Text(
                        text = "Delete",
                        color = androidx.compose.ui.graphics.Color.Red
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }
}