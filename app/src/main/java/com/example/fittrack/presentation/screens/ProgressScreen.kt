package com.example.fittrack.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.theme.BackgroundDark
import com.example.fittrack.presentation.theme.TextWhite
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel
import androidx.compose.material3.LinearProgressIndicator



@Composable
fun ProgressScreen(
    viewModel: WorkoutViewModel
) {


    val workouts by viewModel
        .workouts
        .collectAsState()

    val exercises by viewModel
        .allExercises
        .collectAsState()

    val totalVolume =
        exercises.sumOf {

            it.sets *
                    it.reps *
                    it.weight

        }
    val progress =

        (totalVolume / 10000f)
            .coerceAtMost(1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(20.dp)
    ) {


        Text(
            text = "Progress",
            color = TextWhite,
            fontSize = 28.sp
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Text(
            text = "Total Workouts: ${workouts.size}",
            color = TextWhite
        )

        Text(
            text = "Total Exercises: ${exercises.size}"
        )


        Text(
            text = "Total Volume: $totalVolume kg"
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Text(
            text = "Strength Progress"
        )


        LinearProgressIndicator(
            progress = {
                progress
            },

            modifier = Modifier
                .fillMaxWidth()
        )

    }
}