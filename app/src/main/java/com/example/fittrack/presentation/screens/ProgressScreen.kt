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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.Repeat
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.data.local.entity.PersonalRecordEntity
import com.example.fittrack.presentation.components.BarChartEntry
import com.example.fittrack.presentation.components.GlowingBackground
import com.example.fittrack.presentation.components.MuscleBreakdownChart
import com.example.fittrack.presentation.components.RadialProgressDonut
import com.example.fittrack.presentation.components.StatCard
import com.example.fittrack.presentation.components.VolumeBarChart
import com.example.fittrack.presentation.theme.CardBorderActive
import com.example.fittrack.presentation.theme.CardBorderWhite
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.DangerRed
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextSilver
import com.example.fittrack.presentation.theme.TextWhite
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel
import java.util.Calendar
import java.util.Locale

@Composable
fun ProgressScreen(
    viewModel: WorkoutViewModel
) {
    val workouts by viewModel.workouts.collectAsState()
    val exercises by viewModel.allExercises.collectAsState()
    val personalRecords by viewModel.personalRecords.collectAsState()
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsState()
    val targetVolumeGoal by viewModel.targetVolumeGoal.collectAsState()

    val timeframes = listOf("7 Days", "30 Days", "All Time")

    val totalVolume = exercises.sumOf { it.sets * it.reps * it.weight }
    val totalSets = exercises.sumOf { it.sets }
    val totalReps = exercises.sumOf { it.sets * it.reps }
    val completedWorkouts = workouts.count { it.completed }

    val chartData = remember(workouts, exercises, selectedTimeframe) {
        val daysMap = mutableMapOf(
            Calendar.MONDAY to 0f,
            Calendar.TUESDAY to 0f,
            Calendar.WEDNESDAY to 0f,
            Calendar.THURSDAY to 0f,
            Calendar.FRIDAY to 0f,
            Calendar.SATURDAY to 0f,
            Calendar.SUNDAY to 0f
        )

        workouts.forEach { w ->
            if (w.completed) {
                val vol = exercises.filter { it.workoutId == w.id }.sumOf { it.sets * it.reps * it.weight }.toFloat()
                val cal = Calendar.getInstance().apply { timeInMillis = w.date }
                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                daysMap[dayOfWeek] = (daysMap[dayOfWeek] ?: 0f) + vol
            }
        }

        listOf(
            BarChartEntry("Mon", daysMap[Calendar.MONDAY] ?: 0f),
            BarChartEntry("Tue", daysMap[Calendar.TUESDAY] ?: 0f),
            BarChartEntry("Wed", daysMap[Calendar.WEDNESDAY] ?: 0f),
            BarChartEntry("Thu", daysMap[Calendar.THURSDAY] ?: 0f),
            BarChartEntry("Fri", daysMap[Calendar.FRIDAY] ?: 0f),
            BarChartEntry("Sat", daysMap[Calendar.SATURDAY] ?: 0f),
            BarChartEntry("Sun", daysMap[Calendar.SUNDAY] ?: 0f)
        )
    }

    val muscleSplit = remember(exercises) {
        val map = mutableMapOf(
            "Chest & Shoulders" to 0f,
            "Back & Biceps" to 0f,
            "Legs & Core" to 0f,
            "Cardio & Arms" to 0f
        )
        exercises.forEach { ex ->
            val nameLower = ex.name.lowercase()
            val vol = (ex.sets * ex.reps * ex.weight).toFloat()
            when {
                nameLower.contains("bench") || nameLower.contains("press") || nameLower.contains("shoulder") ->
                    map["Chest & Shoulders"] = (map["Chest & Shoulders"] ?: 0f) + vol
                nameLower.contains("pull") || nameLower.contains("row") || nameLower.contains("lat") || nameLower.contains("curl") ->
                    map["Back & Biceps"] = (map["Back & Biceps"] ?: 0f) + vol
                nameLower.contains("squat") || nameLower.contains("leg") || nameLower.contains("deadlift") ->
                    map["Legs & Core"] = (map["Legs & Core"] ?: 0f) + vol
                else ->
                    map["Cardio & Arms"] = (map["Cardio & Arms"] ?: 0f) + vol
            }
        }
        map
    }

    var showAddPrDialog by remember { mutableStateOf(false) }
    var prNameInput by remember { mutableStateOf("") }
    var prWeightInput by remember { mutableStateOf("") }
    var prToDelete by remember { mutableStateOf<PersonalRecordEntity?>(null) }

    var showGoalDialog by remember { mutableStateOf(false) }
    var goalInput by remember { mutableStateOf("") }

    GlowingBackground {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 30 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(text = "Analytics & Progress", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Performance metrics & personal records", color = TextGray, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(20.dp))

                // Timeframe Selector Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(20.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(20.dp))
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    timeframes.forEach { timeframe ->
                        val isSelected = selectedTimeframe == timeframe
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) CardDarkElevated else CardDark,
                                    RoundedCornerShape(16.dp)
                                )
                                .then(
                                    if (isSelected) Modifier.border(1.dp, CardBorderActive, RoundedCornerShape(16.dp))
                                    else Modifier
                                )
                                .clickable { viewModel.setTimeframe(timeframe) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = timeframe,
                                color = if (isSelected) TextWhite else TextGray,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Radial Goal Donut Card with Celebration Milestone
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Volume Goal Progress",
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Box(
                            modifier = Modifier
                                .background(CardDarkElevated, RoundedCornerShape(12.dp))
                                .border(1.dp, CardBorderActive, RoundedCornerShape(12.dp))
                                .clickable {
                                    goalInput = targetVolumeGoal.toInt().toString()
                                    showGoalDialog = true
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Goal",
                                    tint = TextWhite,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Set Goal", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val donutProgress = if (totalVolume > 0) (totalVolume.toFloat() / targetVolumeGoal).coerceIn(0.02f, 1f) else 0f
                    RadialProgressDonut(
                        progress = donutProgress,
                        centerTitle = if (totalVolume > 0) "$totalVolume" else "0 kg",
                        centerSubtitle = if (totalVolume > 0) "kg Lifted" else "Total Volume"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Target Goal: ${String.format(Locale.US, "%,.0f", targetVolumeGoal)} kg total volume",
                        color = TextGray,
                        fontSize = 13.sp
                    )

                    // Target Volume Goal Hit Celebration Banner
                    if (totalVolume >= targetVolumeGoal && totalVolume > 0) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFF59E0B), Color(0xFFEAB308), Color(0xFFF59E0B))
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🏆 TARGET GOAL ACHIEVED! Milestone Unlocked 🎉",
                                color = CardDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Volume Bar Chart
                VolumeBarChart(entries = chartData)

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Overview 2x2 Grid with Vector Icons
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Completed Workouts",
                        value = completedWorkouts.toString(),
                        modifier = Modifier.weight(1f),
                        vectorIcon = Icons.AutoMirrored.Rounded.ViewList
                    )
                    StatCard(
                        title = "Total Volume",
                        value = if (totalVolume > 0) "${totalVolume / 1000}k kg" else "0 kg",
                        modifier = Modifier.weight(1f),
                        vectorIcon = Icons.Rounded.FitnessCenter
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Total Sets",
                        value = totalSets.toString(),
                        modifier = Modifier.weight(1f),
                        vectorIcon = Icons.Rounded.FormatListNumbered
                    )
                    StatCard(
                        title = "Total Reps",
                        value = totalReps.toString(),
                        modifier = Modifier.weight(1f),
                        vectorIcon = Icons.Rounded.Repeat
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Muscle Group Split Chart
                MuscleBreakdownChart(breakdown = muscleSplit)

                Spacer(modifier = Modifier.height(20.dp))

                // Personal Records Section (Guaranteed 100% Horizontal Header Alignment)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(CardDarkElevated, CircleShape)
                                    .border(1.dp, CardBorderActive.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.EmojiEvents,
                                    contentDescription = "Trophy",
                                    tint = TextWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Personal Records",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .background(CardDarkElevated, RoundedCornerShape(12.dp))
                                .border(1.dp, CardBorderActive, RoundedCornerShape(12.dp))
                                .clickable { showAddPrDialog = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "+ Add PR",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (personalRecords.isEmpty()) {
                        Text(
                            text = "No PRs logged yet. Tap + Add PR to record your heaviest lifts!",
                            color = TextGray,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    } else {
                        personalRecords.forEach { pr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .background(CardDarkElevated, RoundedCornerShape(16.dp))
                                    .border(1.dp, CardBorderWhite.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = pr.exerciseName, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Achieved Max Lift", color = TextGray, fontSize = 12.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "${pr.maxWeightKg} kg", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { prToDelete = pr },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete PR",
                                            tint = DangerRed.copy(alpha = 0.8f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Editable Volume Goal Dialog
    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Set Target Volume Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter your target total volume in kilograms (kg):", color = TextGray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = goalInput,
                        onValueChange = { goalInput = it },
                        label = { Text("Target Volume (kg)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CardBorderActive,
                            unfocusedBorderColor = CardBorderWhite,
                            focusedLabelColor = TextWhite,
                            unfocusedLabelColor = TextGray,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newGoal = goalInput.toFloatOrNull()
                        if (newGoal != null && newGoal > 0f) {
                            viewModel.updateTargetVolumeGoal(newGoal)
                        }
                        showGoalDialog = false
                    }
                ) {
                    Text("Save Goal", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    // Add PR Dialog
    if (showAddPrDialog) {
        AlertDialog(
            onDismissRequest = { showAddPrDialog = false },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Add Personal Record", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = prNameInput,
                        onValueChange = { prNameInput = it },
                        label = { Text("Exercise Name (e.g. Bench Press)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CardBorderActive,
                            unfocusedBorderColor = CardBorderWhite,
                            focusedLabelColor = TextWhite,
                            unfocusedLabelColor = TextGray,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = prWeightInput,
                        onValueChange = { prWeightInput = it },
                        label = { Text("Max Weight (kg)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CardBorderActive,
                            unfocusedBorderColor = CardBorderWhite,
                            focusedLabelColor = TextWhite,
                            unfocusedLabelColor = TextGray,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val weight = prWeightInput.toIntOrNull()
                        if (prNameInput.isNotBlank() && weight != null) {
                            viewModel.addPersonalRecord(prNameInput, weight)
                            prNameInput = ""
                            prWeightInput = ""
                            showAddPrDialog = false
                        }
                    }
                ) {
                    Text("Save PR", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPrDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    // Delete PR Confirmation Dialog
    prToDelete?.let { pr ->
        AlertDialog(
            onDismissRequest = { prToDelete = null },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Delete PR?", fontWeight = FontWeight.Bold) },
            text = { Text("Remove record '${pr.exerciseName}' (${pr.maxWeightKg} kg)?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePersonalRecord(pr)
                        prToDelete = null
                    }
                ) {
                    Text("Delete", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { prToDelete = null }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
}