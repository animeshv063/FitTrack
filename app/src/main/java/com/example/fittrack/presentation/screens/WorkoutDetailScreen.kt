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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.data.local.entity.ExerciseEntity
import com.example.fittrack.data.notification.NotificationHelper
import com.example.fittrack.data.sound.SoundAlarmManager
import com.example.fittrack.presentation.components.GlowingBackground
import com.example.fittrack.presentation.components.PrimaryButton
import com.example.fittrack.presentation.theme.BackgroundDark
import com.example.fittrack.presentation.theme.CardBorderWhite
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.DangerRed
import com.example.fittrack.presentation.theme.PrimaryGreen
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextSilver
import com.example.fittrack.presentation.theme.TextWhite
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel
import kotlinx.coroutines.delay

@Composable
fun WorkoutDetailScreen(
    workoutId: Int,
    viewModel: WorkoutViewModel
) {
    val workouts by viewModel.workouts.collectAsState()
    val exercises by viewModel.getExercises(workoutId).collectAsState(initial = emptyList())
    val currentWorkout = remember(workouts, workoutId) { workouts.find { it.id == workoutId } }

    var name by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    // Workout Modes: "Stopwatch" or "Target Timer"
    var selectedTimerMode by remember { mutableStateOf("Stopwatch") }
    var workoutStarted by remember { mutableStateOf(false) }
    var isTimerPaused by remember { mutableStateOf(false) }
    var workoutElapsedTime by remember { mutableStateOf(0) }

    var targetMinutes by remember(currentWorkout) {
        mutableStateOf(currentWorkout?.duration?.takeIf { it > 0 } ?: 45)
    }
    var countdownRemainingTime by remember(targetMinutes) {
        mutableStateOf(targetMinutes * 60)
    }

    // Rest Timer state
    var defaultRestSeconds by remember { mutableStateOf(60) }
    var restTime by remember { mutableStateOf(0) }
    var restTimerRunning by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var exerciseToDelete by remember { mutableStateOf<ExerciseEntity?>(null) }

    var showRestAlarmDialog by remember { mutableStateOf(false) }
    var showWorkoutEndAlarmDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }
    val soundAlarmManager = remember { SoundAlarmManager(context) }

    val exercisePresets = listOf("Bench Press", "Squat", "Deadlift", "Pull-Up", "Bicep Curl")

    // Stop sound alarm when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            soundAlarmManager.stopAlarmSound()
        }
    }

    // 1. Stopwatch Count-Up Loop
    LaunchedEffect(workoutStarted, isTimerPaused, selectedTimerMode) {
        while (workoutStarted && !isTimerPaused && selectedTimerMode == "Stopwatch") {
            delay(1000)
            workoutElapsedTime++
        }
    }

    // 2. Countdown Target Timer Loop
    LaunchedEffect(workoutStarted, isTimerPaused, selectedTimerMode, countdownRemainingTime) {
        while (workoutStarted && !isTimerPaused && selectedTimerMode == "Target Timer" && countdownRemainingTime > 0) {
            delay(1000)
            countdownRemainingTime--
            if (countdownRemainingTime == 0) {
                workoutStarted = false
                soundAlarmManager.playAlarmSound()
                notificationHelper.showRestFinishedNotification()
                showWorkoutEndAlarmDialog = true
            }
        }
    }

    // 3. Rest Countdown Timer Loop
    LaunchedEffect(restTimerRunning, restTime) {
        while (restTimerRunning && restTime > 0) {
            delay(1000)
            restTime--
            if (restTime == 0) {
                restTimerRunning = false
                soundAlarmManager.playAlarmSound()
                notificationHelper.showRestFinishedNotification()
                showRestAlarmDialog = true
            }
        }
    }

    GlowingBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                // Header Card with Dual Workout Mode Selection
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentWorkout?.name ?: "Workout Session",
                                color = TextWhite,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (workoutStarted) "⚡ Active Session (${if (isTimerPaused) "Paused" else "Running"})" else "Select mode & start workout",
                                color = if (workoutStarted) PrimaryGreen else TextGray,
                                fontSize = 13.sp
                            )
                        }

                        if (workoutStarted) {
                            Box(
                                modifier = Modifier
                                    .background(DangerRed.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                                    .border(1.dp, DangerRed, RoundedCornerShape(14.dp))
                                    .clickable {
                                        workoutStarted = false
                                        isTimerPaused = false
                                        viewModel.completeWorkout(workoutId)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text("Finish Workout", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mode Selector Pill Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDarkElevated, RoundedCornerShape(16.dp))
                            .padding(4.dp)
                    ) {
                        listOf("Stopwatch" to "⏱️ Stopwatch", "Target Timer" to "⏳ Target Timer").forEach { (modeKey, label) ->
                            val isSelected = selectedTimerMode == modeKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) PrimaryGreen else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        if (!workoutStarted) {
                                            selectedTimerMode = modeKey
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) BackgroundDark else TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedTimerMode == "Stopwatch") {
                        // --- STOPWATCH UI ---
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "STOPWATCH ELAPSED", color = TextGray, fontSize = 12.sp, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatStopwatch(workoutElapsedTime),
                                color = TextWhite,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (!workoutStarted) {
                                    PrimaryButton(
                                        text = "▶ Start Workout Stopwatch",
                                        onClick = {
                                            workoutStarted = true
                                            isTimerPaused = false
                                        }
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(CardDarkElevated, RoundedCornerShape(14.dp))
                                            .border(1.dp, CardBorderWhite, RoundedCornerShape(14.dp))
                                            .clickable { isTimerPaused = !isTimerPaused }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isTimerPaused) "▶ Resume" else "⏸ Pause",
                                            color = TextWhite,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(PrimaryGreen.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                                            .border(1.dp, PrimaryGreen, RoundedCornerShape(14.dp))
                                            .clickable {
                                                restTime = defaultRestSeconds
                                                restTimerRunning = true
                                            }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "🔔 Start Rest (${defaultRestSeconds}s)",
                                            color = PrimaryGreen,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // --- TARGET TIMER UI ---
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = "TARGET WORKOUT TIMER", color = TextGray, fontSize = 11.sp, letterSpacing = 1.sp)

                                if (!workoutStarted) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(CardDarkElevated, RoundedCornerShape(8.dp))
                                                .clickable {
                                                    if (targetMinutes > 5) {
                                                        targetMinutes -= 5
                                                        countdownRemainingTime = targetMinutes * 60
                                                    }
                                                }
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("-5m", color = TextSilver, fontSize = 11.sp)
                                        }

                                        Text("${targetMinutes}m", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                        Box(
                                            modifier = Modifier
                                                .background(CardDarkElevated, RoundedCornerShape(8.dp))
                                                .clickable {
                                                    targetMinutes += 5
                                                    countdownRemainingTime = targetMinutes * 60
                                                }
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("+5m", color = TextSilver, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = formatTime(countdownRemainingTime),
                                color = if (countdownRemainingTime < 60) DangerRed else TextWhite,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (!workoutStarted) {
                                    PrimaryButton(
                                        text = "▶ Start ${targetMinutes}m Countdown Timer",
                                        onClick = {
                                            countdownRemainingTime = targetMinutes * 60
                                            workoutStarted = true
                                            isTimerPaused = false
                                        }
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(CardDarkElevated, RoundedCornerShape(14.dp))
                                            .border(1.dp, CardBorderWhite, RoundedCornerShape(14.dp))
                                            .clickable { isTimerPaused = !isTimerPaused }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isTimerPaused) "▶ Resume" else "⏸ Pause",
                                            color = TextWhite,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(PrimaryGreen.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                                            .border(1.dp, PrimaryGreen, RoundedCornerShape(14.dp))
                                            .clickable {
                                                restTime = defaultRestSeconds
                                                restTimerRunning = true
                                            }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "🔔 Start Rest (${defaultRestSeconds}s)",
                                            color = PrimaryGreen,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rest Time Preference Selector Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(20.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(20.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Rest Timer Preset", color = TextGray, fontSize = 13.sp)
                        Text(text = "${defaultRestSeconds}s default", color = PrimaryGreen, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(30, 60, 90, 120).forEach { sec ->
                            val isSelected = defaultRestSeconds == sec
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) PrimaryGreen.copy(alpha = 0.2f) else CardDarkElevated,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) PrimaryGreen else CardBorderWhite,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        defaultRestSeconds = sec
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${sec}s",
                                    color = if (isSelected) PrimaryGreen else TextSilver,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Active Rest Countdown Banner
                if (restTimerRunning) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDarkElevated, RoundedCornerShape(20.dp))
                            .border(1.dp, PrimaryGreen, RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "REST COUNTDOWN (ALARM AT 00:00)", color = TextGray, fontSize = 11.sp, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = formatTime(restTime), color = PrimaryGreen, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .background(CardDark, RoundedCornerShape(12.dp))
                                    .clickable { restTime += 30 }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(text = "+30s Rest", color = TextWhite, fontSize = 13.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .background(CardDark, RoundedCornerShape(12.dp))
                                    .clickable { restTimerRunning = false; restTime = 0 }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(text = "Skip Rest", color = TextGray, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Exercise Presets Row
                Text(text = "Quick Exercise Select", color = TextGray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    exercisePresets.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(CardDark, RoundedCornerShape(12.dp))
                                .border(1.dp, CardBorderWhite, RoundedCornerShape(12.dp))
                                .clickable {
                                    name = preset
                                    if (sets.isBlank()) sets = "3"
                                    if (reps.isBlank()) reps = "10"
                                    if (weight.isBlank()) weight = "40"
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = preset.split(" ").first(), color = TextSilver, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Add Exercise Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Text(text = "Add Exercise", color = TextWhite, fontSize = 17.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorMessage = "" },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Exercise Name", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                            focusedBorderColor = PrimaryGreen, unfocusedBorderColor = CardBorderWhite
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sets,
                            onValueChange = { sets = it.filter { c -> c.isDigit() }; errorMessage = "" },
                            modifier = Modifier.weight(1f),
                            label = { Text("Sets", color = TextGray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = CardBorderWhite
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = reps,
                            onValueChange = { reps = it.filter { c -> c.isDigit() }; errorMessage = "" },
                            modifier = Modifier.weight(1f),
                            label = { Text("Reps", color = TextGray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = CardBorderWhite
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it.filter { c -> c.isDigit() }; errorMessage = "" },
                            modifier = Modifier.weight(1f),
                            label = { Text("Kg", color = TextGray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = CardBorderWhite
                            ),
                            singleLine = true
                        )
                    }

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage, color = DangerRed, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    PrimaryButton(
                        text = "+ Save Exercise",
                        onClick = {
                            val setsVal = sets.toIntOrNull()
                            val repsVal = reps.toIntOrNull()
                            val weightVal = weight.toIntOrNull()

                            when {
                                name.isBlank() -> errorMessage = "Enter exercise name"
                                setsVal == null || setsVal <= 0 -> errorMessage = "Min 1 set required"
                                repsVal == null || repsVal <= 0 -> errorMessage = "Min 1 rep required"
                                weightVal == null || weightVal < 0 -> errorMessage = "Valid weight required"
                                else -> {
                                    viewModel.addExercise(
                                        ExerciseEntity(
                                            workoutId = workoutId,
                                            name = name.trim(),
                                            sets = setsVal,
                                            reps = repsVal,
                                            weight = weightVal
                                        )
                                    )
                                    name = ""
                                    sets = ""
                                    reps = ""
                                    weight = ""
                                    errorMessage = ""
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Exercises List", color = TextWhite, fontSize = 20.sp)
            }

            // Exercise items with Delete option for each exercise
            items(items = exercises, key = { it.id }) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    workoutStarted = workoutStarted,
                    onDelete = { exerciseToDelete = exercise },
                    onSetChanged = { completedSets ->
                        viewModel.updateCompletedSets(exercise.id, completedSets)
                        if (completedSets > 0 && completedSets <= exercise.sets) {
                            restTime = defaultRestSeconds
                            restTimerRunning = true
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // 1. Rest Finished Sound Alarm Dialog (Ringtone + Physical Vibration)
    if (showRestAlarmDialog) {
        AlertDialog(
            onDismissRequest = {
                soundAlarmManager.stopAlarmSound()
                showRestAlarmDialog = false
            },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔔 Rest Time Finished!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(text = "Phone Alarm sound & physical vibration ringing!")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Great recovery! Click below to stop the alarm and begin your next set.", color = TextGray, fontSize = 13.sp)
                }
            },
            confirmButton = {
                PrimaryButton(
                    text = "Stop Alarm & Next Set",
                    onClick = {
                        soundAlarmManager.stopAlarmSound()
                        showRestAlarmDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        soundAlarmManager.stopAlarmSound()
                        showRestAlarmDialog = false
                        restTime = 30
                        restTimerRunning = true
                    }
                ) {
                    Text("+30s Rest", color = PrimaryGreen)
                }
            }
        )
    }

    // 2. Target Workout Duration Reached Sound Alarm Dialog (Ringtone + Physical Vibration)
    if (showWorkoutEndAlarmDialog) {
        AlertDialog(
            onDismissRequest = {
                soundAlarmManager.stopAlarmSound()
                showWorkoutEndAlarmDialog = false
            },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🚨 Workout Target Reached!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(text = "Phone Alarm sound & physical vibration ringing!")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "You've reached your target workout timer of ${targetMinutes} minutes. Great job!", color = TextGray, fontSize = 13.sp)
                }
            },
            confirmButton = {
                PrimaryButton(
                    text = "Stop Alarm & Finish Workout",
                    onClick = {
                        soundAlarmManager.stopAlarmSound()
                        workoutStarted = false
                        viewModel.completeWorkout(workoutId)
                        showWorkoutEndAlarmDialog = false
                    }
                )
            }
        )
    }

    // Delete Exercise Dialog
    exerciseToDelete?.let { exercise ->
        AlertDialog(
            onDismissRequest = { exerciseToDelete = null },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Delete Exercise?") },
            text = { Text("Remove '${exercise.name}' from this workout routine?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteExercise(exercise)
                        exerciseToDelete = null
                    }
                ) {
                    Text("Delete", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { exerciseToDelete = null }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseEntity,
    workoutStarted: Boolean,
    onDelete: () -> Unit,
    onSetChanged: (Int) -> Unit
) {
    var completedSets by remember(exercise.id, exercise.completedSets) {
        mutableStateOf(exercise.completedSets)
    }

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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = exercise.name, color = TextWhite, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${exercise.sets} sets × ${exercise.reps} reps @ ${exercise.weight} kg",
                    color = TextSilver,
                    fontSize = 13.sp
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete exercise",
                    tint = DangerRed
                )
            }
        }

        if (workoutStarted) {
            Spacer(modifier = Modifier.height(10.dp))

            repeat(exercise.sets) { index ->
                val isCompleted = index < completedSets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { checked ->
                            completedSets = if (checked) {
                                (completedSets + 1).coerceAtMost(exercise.sets)
                            } else {
                                (completedSets - 1).coerceAtLeast(0)
                            }
                            onSetChanged(completedSets)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryGreen,
                            uncheckedColor = CardBorderWhite
                        )
                    )
                    Text(
                        text = "Set ${index + 1} (${exercise.weight} kg × ${exercise.reps} reps)",
                        color = if (isCompleted) PrimaryGreen else TextGray,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val rem = seconds % 60
    return String.format("%02d:%02d", minutes, rem)
}

private fun formatStopwatch(seconds: Int): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) {
        String.format("%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}