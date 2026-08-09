package com.example.fittrack.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun ProgressScreen(
    viewModel: WorkoutViewModel
) {

    val workouts by
    viewModel.workouts.collectAsState()

    val exercises by
    viewModel.allExercises.collectAsState()

    val totalVolume =
        exercises.sumOf {
            it.sets *
                    it.reps *
                    it.weight
        }

    val totalSets =
        exercises.sumOf {
            it.sets
        }

    val totalReps =
        exercises.sumOf {
            it.sets * it.reps
        }

    val completedWorkouts =
        workouts.count {
            it.completed
        }

    val progress =
        (totalVolume / 10000f)
            .coerceAtMost(1f)

    GlowingBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Text(
                text = "Progress",
                color = TextWhite,
                fontSize = 30.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Your training statistics",
                color = TextGray
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
                    value =
                        completedWorkouts.toString(),
                    modifier =
                        Modifier.weight(1f)
                )

                StatCard(
                    title = "Exercises",
                    value =
                        exercises.size.toString(),
                    modifier =
                        Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                StatCard(
                    title = "Sets",
                    value =
                        totalSets.toString(),
                    modifier =
                        Modifier.weight(1f)
                )

                StatCard(
                    title = "Reps",
                    value =
                        totalReps.toString(),
                    modifier =
                        Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Total Volume",
                color = TextWhite,
                fontSize = 20.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "$totalVolume kg",
                color = PrimaryGreen,
                fontSize = 30.sp
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            LinearProgressIndicator(
                progress = {
                    progress
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "10,000 kg goal",
                color = TextGray
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "Training Overview",
                color = TextWhite,
                fontSize = 21.sp
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    if (completedWorkouts == 0)
                        "Complete your first workout to start building your progress."
                    else
                        "You've completed $completedWorkouts workouts. Keep training consistently!",
                color = TextGray,
                fontSize = 16.sp
            )
        }
    }
}