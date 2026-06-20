package com.example.fittrack.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.theme.BackgroundDark
import com.example.fittrack.presentation.theme.TextWhite
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.fittrack.data.local.entity.WorkoutEntity
import androidx.compose.foundation.clickable

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
            },

            label = {
                Text(
                    text = "Workout Name"
                )
            }
        )


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        OutlinedTextField(
            value = workoutDuration,

            onValueChange = {
                workoutDuration = it
            },

            label = {
                Text(
                    text = "Duration"
                )
            }
        )


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        Button(
            onClick = {
                viewModel.addWorkout(
                    WorkoutEntity(
                        name = workoutName,
                        duration = workoutDuration.toInt(),
                        date = System.currentTimeMillis()
                    )
                )

                workoutName = ""
                workoutDuration = ""
            }
        ) {

            Text(
                text = "Add Workout"
            )

        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )

        workouts.forEach { workout ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(
                            "workout_detail/${workout.id}"
                        )
                    }
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = workout.name
                    )

                    Text(
                        text = "${workout.duration} minutes"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
    }
} 