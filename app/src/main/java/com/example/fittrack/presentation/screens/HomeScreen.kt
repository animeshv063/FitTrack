package com.example.fittrack.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fittrack.navigation.Routes
import com.example.fittrack.presentation.components.ActivityCard
import com.example.fittrack.presentation.components.GlowingBackground
import com.example.fittrack.presentation.components.PrimaryButton
import com.example.fittrack.presentation.components.StatCard
import com.example.fittrack.presentation.components.WorkoutCard
import com.example.fittrack.presentation.theme.PrimaryGreen
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextWhite
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: WorkoutViewModel
) {

    val workouts by viewModel.workouts.collectAsState()
    val steps by viewModel.steps.collectAsState()

    val calories =
        (steps * 0.04).toInt()

    val completedWorkouts =
        workouts.count { it.completed }

    val latestWorkout =
        workouts.firstOrNull()

    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        visible = true
        viewModel.startStepCounter()
    }

    GlowingBackground {

        AnimatedVisibility(
            visible = visible,
            enter =
                fadeIn() +
                        slideInVertically()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(20.dp)
            ) {

                Text(
                    text = "Welcome Back 👋",
                    color = TextWhite,
                    fontSize = 30.sp
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Ready to become stronger?",
                    color = TextGray
                )

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    StatCard(
                        title = "Workouts",
                        value = completedWorkouts.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Calories",
                        value = calories.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                ActivityCard(
                    steps = steps,
                    calories = calories
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = "Today's Workout",
                    color = TextWhite,
                    fontSize = 21.sp
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                WorkoutCard(
                    name =
                        latestWorkout?.name
                            ?: "No Workout Yet",

                    duration =
                        latestWorkout?.duration
                            ?: 0
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                PrimaryButton(
                    text = "Start Workout",
                    onClick = {
                        navController.navigate(
                            Routes.Workout.route
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                if (workouts.isNotEmpty()) {

                    Text(
                        text = "Recent Workouts",
                        color = TextWhite,
                        fontSize = 21.sp
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    workouts
                        .take(3)
                        .forEach { workout ->

                            WorkoutCard(
                                name = workout.name,
                                duration = workout.duration
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )
                        }
                }

                Spacer(
                    modifier = Modifier.height(80.dp)
                )
            }
        }
    }
}