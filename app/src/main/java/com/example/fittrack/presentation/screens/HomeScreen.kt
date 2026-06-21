package com.example.fittrack.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fittrack.navigation.Routes
import com.example.fittrack.presentation.components.*
import com.example.fittrack.presentation.theme.*
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

    val latestWorkout =
        workouts.lastOrNull()


    var visible by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(Unit) {

        visible = true

        viewModel.startStepCounter()

    }


    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            Text(
                text = "Welcome Back 👋",
                color = TextWhite,
                fontSize = 28.sp
            )

            Text(
                text = "Ready for today's workout?",
                color = TextGray
            )


            Spacer(
                Modifier.height(30.dp)
            )


            Row {

                StatCard(
                    title = "Workouts",
                    value = workouts.size.toString(),
                    modifier = Modifier.weight(1f)
                )


                Spacer(
                    Modifier.width(16.dp)
                )


                StatCard(
                    title = "Calories",
                    value = calories.toString(),
                    modifier = Modifier.weight(1f)
                )
            }


            Spacer(
                Modifier.height(24.dp)
            )


            ActivityCard(
                steps = steps,
                calories = calories
            )


            Spacer(
                Modifier.height(24.dp)
            )


            Text(
                text = "Today's Workout",
                color = TextWhite,
                fontSize = 20.sp
            )


            Spacer(
                Modifier.height(12.dp)
            )


            WorkoutCard(
                name =
                    latestWorkout?.name
                        ?: "No Workout",

                duration =
                    latestWorkout?.duration
                        ?: 0
            )


            Spacer(
                Modifier.height(24.dp)
            )


            PrimaryButton(
                text = "Start Workout",
                onClick = {

                    navController.navigate(
                        Routes.Workout.route
                    )

                }
            )

        }
    }
}