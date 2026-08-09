package com.example.fittrack.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.components.GlowingBackground
import com.example.fittrack.presentation.components.StatCard
import com.example.fittrack.presentation.theme.PrimaryGreen
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextWhite
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel

@Composable
fun ProfileScreen(
    viewModel: WorkoutViewModel
) {

    val steps by
    viewModel.steps.collectAsState()

    val workouts by
    viewModel.workouts.collectAsState()

    val exercises by
    viewModel.allExercises.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startStepCounter()
    }

    val completed =
        workouts.count {
            it.completed
        }

    val totalVolume =
        exercises.sumOf {
            it.sets *
                    it.reps *
                    it.weight
        }

    val stepProgress =
        (steps / 10000f)
            .coerceAtMost(1f)

    GlowingBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Text(
                text = "Profile",
                color = TextWhite,
                fontSize = 30.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Your fitness journey",
                color = TextGray
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "👤",
                fontSize = 54.sp
            )

            Text(
                text = "FitTrack Athlete",
                color = TextWhite,
                fontSize = 23.sp
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                StatCard(
                    title = "Workouts",
                    value = completed.toString(),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Volume",
                    value = "$totalVolume kg",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "Daily Step Goal",
                color = TextWhite,
                fontSize = 21.sp
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "$steps / 10,000 steps",
                color = PrimaryGreen,
                fontSize = 20.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            LinearProgressIndicator(
                progress = {
                    stepProgress
                }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    if (steps >= 10000)
                        "🎉 Daily goal completed!"
                    else
                        "${10000 - steps} steps remaining",
                color = TextGray
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "Keep Going 💪",
                color = TextWhite,
                fontSize = 22.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    "Consistency beats intensity. Keep showing up and your progress will follow.",
                color = TextGray,
                fontSize = 16.sp
            )
        }
    }
}