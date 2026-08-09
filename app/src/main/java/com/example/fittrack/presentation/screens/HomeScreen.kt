package com.example.fittrack.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fittrack.data.local.entity.WorkoutEntity
import com.example.fittrack.navigation.Routes
import com.example.fittrack.presentation.components.ActivityCard
import com.example.fittrack.presentation.components.CalendarStreakCard
import com.example.fittrack.presentation.components.GlowingBackground
import com.example.fittrack.presentation.components.PrimaryButton
import com.example.fittrack.presentation.components.StatCard
import com.example.fittrack.presentation.components.WorkoutCard
import com.example.fittrack.presentation.theme.CardBorderActive
import com.example.fittrack.presentation.theme.CardBorderWhite
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.DangerRed
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextSilver
import com.example.fittrack.presentation.theme.TextWhite
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: WorkoutViewModel
) {
    val workouts by viewModel.workouts.collectAsState()
    val steps by viewModel.steps.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val waterLogs by viewModel.waterLogs.collectAsState()

    val calories = (steps * 0.04).toInt()
    val completedWorkouts = workouts.count { it.completed }
    val latestWorkout = workouts.firstOrNull()

    val totalWaterMl = waterLogs.sumOf { it.amountMl }
    val dynamicTitle = viewModel.getAthleteTitle(completedWorkouts)

    var visible by remember { mutableStateOf(false) }
    var workoutToDelete by remember { mutableStateOf<WorkoutEntity?>(null) }

    LaunchedEffect(Unit) {
        visible = true
        viewModel.startStepCounter()
    }

    GlowingBackground {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Header Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TRAINING DASHBOARD",
                            color = TextGray,
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = userProfile?.name ?: "Athlete",
                            color = TextWhite,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(CardDarkElevated, RoundedCornerShape(16.dp))
                            .border(1.dp, CardBorderActive, RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.EmojiEvents,
                                contentDescription = "Rank",
                                tint = TextWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = dynamicTitle,
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Grid with Vector Icons
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Workouts Done",
                        value = completedWorkouts.toString(),
                        modifier = Modifier.weight(1f),
                        vectorIcon = Icons.Rounded.FitnessCenter
                    )
                    StatCard(
                        title = "Calories Burned",
                        value = "$calories kcal",
                        modifier = Modifier.weight(1f),
                        vectorIcon = Icons.Rounded.LocalFireDepartment
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Calendar Streak Heatmap Component
                CalendarStreakCard(
                    workouts = workouts
                )



                Spacer(modifier = Modifier.height(16.dp))

                // Step Activity Card
                ActivityCard(
                    steps = steps,
                    calories = calories,
                    stepGoal = userProfile?.stepGoal ?: 10000
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Hydration Quick Logger Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(CardDarkElevated, CircleShape)
                                    .border(1.dp, CardBorderActive.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.WaterDrop,
                                    contentDescription = "Hydration",
                                    tint = TextWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Hydration Logger",
                                color = TextWhite,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "$totalWaterMl / ${userProfile?.waterGoalMl ?: 3000} ml",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(CardDarkElevated, RoundedCornerShape(16.dp))
                                .border(1.dp, CardBorderWhite, RoundedCornerShape(16.dp))
                                .clickable { viewModel.addWaterLog(250) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+250 ml", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(CardDarkElevated, RoundedCornerShape(16.dp))
                                .border(1.dp, CardBorderWhite, RoundedCornerShape(16.dp))
                                .clickable { viewModel.addWaterLog(500) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+500 ml", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        if (waterLogs.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .background(CardDarkElevated, RoundedCornerShape(16.dp))
                                    .border(1.dp, DangerRed.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                    .clickable { viewModel.deleteLastWaterLog() }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Undo", color = DangerRed, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Current Program Section
                Text(
                    text = "Current Program",
                    color = TextWhite,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (latestWorkout != null) {
                    WorkoutCard(
                        name = latestWorkout.name,
                        duration = latestWorkout.duration,
                        completed = latestWorkout.completed,
                        onDelete = { workoutToDelete = latestWorkout },
                        onClick = {
                            navController.navigate("workout_detail/${latestWorkout.id}")
                        }
                    )
                } else {
                    WorkoutCard(
                        name = "No Workouts Yet",
                        duration = 0,
                        completed = false,
                        onClick = { navController.navigate(Routes.Workout.route) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    text = "Start Training",
                    onClick = { navController.navigate(Routes.Workout.route) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Delete Confirmation Dialog
    workoutToDelete?.let { workout ->
        AlertDialog(
            onDismissRequest = { workoutToDelete = null },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Delete Workout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${workout.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteWorkout(workout)
                        workoutToDelete = null
                    }
                ) {
                    Text("Delete", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { workoutToDelete = null }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
}