package com.example.fittrack.presentation.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fittrack.data.local.entity.WorkoutEntity
import com.example.fittrack.presentation.components.GlowingBackground
import com.example.fittrack.presentation.components.PrimaryButton
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
fun WorkoutScreen(
    viewModel: WorkoutViewModel,
    navController: NavController
) {
    val workouts by viewModel.workouts.collectAsState()

    var workoutName by remember { mutableStateOf("") }
    var workoutDuration by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var workoutToDelete by remember { mutableStateOf<WorkoutEntity?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val presets = listOf(
        Triple("Push Heavy", 45, listOf(Triple("Bench Press", 4, 8), Triple("Incline Dumbbell Press", 3, 10), Triple("Tricep Dips", 3, 12))),
        Triple("Pull Power", 50, listOf(Triple("Deadlift", 4, 5), Triple("Lat Pulldown", 4, 10), Triple("Barbell Row", 3, 8))),
        Triple("Leg Destruction", 60, listOf(Triple("Barbell Squats", 5, 8), Triple("Leg Press", 4, 12), Triple("Calf Raises", 4, 15))),
        Triple("Full Body HIIT", 35, listOf(Triple("Burpees", 4, 15), Triple("Kettlebell Swings", 4, 20), Triple("Mountain Climbers", 4, 30)))
    )

    GlowingBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Workouts Library",
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                if (workouts.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(CardDarkElevated, RoundedCornerShape(14.dp))
                            .border(1.dp, DangerRed.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .clickable {
                                workoutToDelete = null
                                showDeleteDialog = true
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Clear All", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Presets Quick Add Row
            Text(text = "Quick Training Presets", color = TextGray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { (pName, pDur, pEx) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(CardDark, RoundedCornerShape(16.dp))
                            .border(1.dp, CardBorderWhite, RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.addPresetWorkout(pName, pDur, pEx)
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = pName.split(" ").first(), color = TextSilver, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Input Form Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardDark, RoundedCornerShape(24.dp))
                    .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                    .padding(18.dp)
            ) {
                Text(text = "Create Custom Workout Routine", color = TextWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = workoutName,
                    onValueChange = {
                        workoutName = it
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Workout Name", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = TextWhite,
                        unfocusedBorderColor = CardBorderWhite,
                        focusedLabelColor = TextWhite
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = workoutDuration,
                    onValueChange = {
                        workoutDuration = it.filter { c -> c.isDigit() }
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Target Workout Timer (mins)", color = TextGray) },
                    supportingText = { Text("Set goal time for Countdown Alarm or track with Stopwatch", color = TextGray, fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = TextWhite,
                        unfocusedBorderColor = CardBorderWhite,
                        focusedLabelColor = TextWhite
                    ),
                    singleLine = true
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage, color = DangerRed, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                PrimaryButton(
                    text = "+ Add Workout Routine",
                    onClick = {
                        val duration = workoutDuration.toIntOrNull()
                        when {
                            workoutName.isBlank() -> errorMessage = "Enter a workout name"
                            duration == null || duration <= 0 -> errorMessage = "Enter valid duration"
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
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Workouts List
            if (workouts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(CardDarkElevated, CircleShape)
                            .border(1.dp, CardBorderActive.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FitnessCenter,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "No workouts added yet", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Select a quick preset above or create a custom routine.", color = TextGray, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = workouts, key = { it.id }) { workout ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardDark, RoundedCornerShape(20.dp))
                                .border(1.dp, CardBorderWhite, RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(CardDarkElevated, CircleShape)
                                            .border(1.dp, CardBorderActive.copy(alpha = 0.3f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.FitnessCenter,
                                            contentDescription = null,
                                            tint = TextWhite,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = workout.name, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "${workout.duration} mins target", color = TextGray, fontSize = 13.sp)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (workout.completed) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = "Done",
                                                tint = TextWhite,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "Done", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    IconButton(
                                        onClick = {
                                            workoutToDelete = workout
                                            showDeleteDialog = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Workout",
                                            tint = DangerRed
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            PrimaryButton(
                                text = "Open Exercises",
                                isDarkShadowStyle = true,
                                onClick = {
                                    navController.navigate("workout_detail/${workout.id}")
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = {
                Text(
                    text = if (workoutToDelete != null) "Delete Workout?" else "Clear All Workouts?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (workoutToDelete != null)
                        "This will permanently delete '${workoutToDelete?.name}' and its exercises."
                    else
                        "This will permanently delete all workouts in your library."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (workoutToDelete != null) {
                            viewModel.deleteWorkout(workoutToDelete!!)
                        } else {
                            viewModel.deleteAllWorkouts()
                        }
                        workoutToDelete = null
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
}