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
import com.example.fittrack.presentation.components.ActivityCard
import com.example.fittrack.presentation.components.PrimaryButton
import com.example.fittrack.presentation.components.StatCard
import com.example.fittrack.presentation.components.WorkoutCard
import com.example.fittrack.presentation.theme.BackgroundDark
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextWhite

@Composable
fun HomeScreen() {

    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        visible = true
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
                color = TextGray,
                fontSize = 16.sp
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Row {

                StatCard(
                    title = "Workouts",
                    value = "12",
                    modifier = Modifier.weight(1f)
                )

                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                StatCard(
                    title = "Calories",
                    value = "950",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            ActivityCard()

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Today's Workout",
                color = TextWhite,
                fontSize = 20.sp
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            WorkoutCard()

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            PrimaryButton(
                text = "Start Workout",
                onClick = {

                }
            )
        }
    }
}