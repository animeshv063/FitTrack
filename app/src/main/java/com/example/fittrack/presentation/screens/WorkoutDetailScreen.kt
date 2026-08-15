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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Timer
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.navigation.NavController
import com.example.fittrack.data.local.entity.ExerciseEntity
import com.example.fittrack.data.notification.NotificationHelper
import com.example.fittrack.data.sound.SoundAlarmManager
import com.example.fittrack.presentation.components.GlowingBackground
import com.example.fittrack.presentation.components.PrimaryButton
import com.example.fittrack.presentation.theme.CardBorderActive
import com.example.fittrack.presentation.theme.CardBorderWhite
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.DangerRed
import com.example.fittrack.presentation.theme.NeonCyan
import com.example.fittrack.presentation.theme.NeonTeal
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextSilver
import com.example.fittrack.presentation.theme.TextWhite
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel
import kotlinx.coroutines.delay

val POPULAR_EXERCISE_LIBRARY = mapOf(
    "Chest" to listOf(
        Triple("Barbell Bench Press", 4, 8),
        Triple("Incline Dumbbell Press", 3, 10),
        Triple("Cable Chest Flyes", 3, 12),
        Triple("Push-Ups", 3, 15)
    ),
    "Back" to listOf(
        Triple("Deadlift", 4, 5),
        Triple("Lat Pulldown", 4, 10),
        Triple("Bent-Over Barbell Row", 3, 8),
        Triple("Seated Cable Row", 3, 10),
        Triple("Pull-Ups", 3, 8)
    ),
    "Legs" to listOf(
        Triple("Barbell Squats", 4, 8),
        Triple("Leg Press", 4, 12),
        Triple("Romanian Deadlift", 3, 10),
        Triple("Leg Extensions", 3, 12),
        Triple("Standing Calf Raises", 4, 15)
    ),
    "Shoulders" to listOf(
        Triple("Overhead Shoulder Press", 4, 8),
        Triple("Dumbbell Lateral Raises", 4, 12),
        Triple("Face Pulls", 3, 15),
        Triple("Front Dumbbell Raises", 3, 12)
    ),
    "Arms" to listOf(
        Triple("Barbell Bicep Curls", 4, 10),
        Triple("Hammer Curls", 3, 12),
        Triple("Tricep Rope Pushdowns", 3, 12),
        Triple("Skull Crushers", 3, 10),
        Triple("Dips", 3, 10)
    ),
    "Core" to listOf(
        Triple("Hanging Knee Raises", 3, 15),
        Triple("Planks", 3, 60),
        Triple("Russian Twists", 3, 20),
        Triple("Ab Wheel Rollouts", 3, 12)
    )
)

@Composable
fun WorkoutDetailScreen(
    workoutId: Int,
    viewModel: WorkoutViewModel,
    navController: NavController? = null
) {
    val workouts by viewModel.workouts.collectAsState()
    val exercises by viewModel.getExercises(workoutId).collectAsState(initial = emptyList())
    val currentWorkout = remember(workouts, workoutId) { workouts.find { it.id == workoutId } }

    var exerciseNameInput by remember { mutableStateOf("") }
    var setsInput by remember { mutableStateOf("") }
    var repsInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }

    var selectedExerciseCategory by remember { mutableStateOf("Chest") }
    val exerciseCategories = listOf("Chest", "Back", "Legs", "Shoulders", "Arms", "Core")

    // Live Session Stopwatch: Starts immediately on opening the workout
    var sessionSeconds by remember { mutableIntStateOf(0) }
    var isSessionActive by remember { mutableStateOf(true) }
    var recordedDurationSeconds by remember { mutableIntStateOf(0) }

    // Rest Countdown Timer State
    var selectedRestPreset by remember { mutableIntStateOf(60) }
    var restTimeRemaining by remember { mutableIntStateOf(0) }
    var restTimerActive by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var exerciseToDelete by remember { mutableStateOf<ExerciseEntity?>(null) }
    var showRestAlarmDialog by remember { mutableStateOf(false) }
    var showWorkoutCompletedCelebration by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }
    val soundAlarmManager = remember { SoundAlarmManager(context) }

    DisposableEffect(Unit) {
        onDispose {
            soundAlarmManager.stopAlarmSound()
        }
    }

    // Live session stopwatch loop: Only ticks while session is active
    LaunchedEffect(isSessionActive) {
        while (isSessionActive) {
            delay(1000)
            sessionSeconds++
        }
    }

    // Freeze timer if workout was already completed previously
    LaunchedEffect(currentWorkout?.completed) {
        if (currentWorkout?.completed == true && recordedDurationSeconds == 0) {
            isSessionActive = false
            sessionSeconds = (currentWorkout.duration * 60)
            recordedDurationSeconds = sessionSeconds
        }
    }

    // Rest countdown loop
    LaunchedEffect(restTimerActive, restTimeRemaining) {
        while (restTimerActive && restTimeRemaining > 0) {
            delay(1000)
            restTimeRemaining--
            if (restTimeRemaining == 0) {
                restTimerActive = false
                soundAlarmManager.playAlarmSound()
                notificationHelper.showRestFinishedNotification()
                showRestAlarmDialog = true
            }
        }
    }

    // Real-time volume calculation across completed sets
    val currentTotalVolume = remember(exercises) {
        exercises.sumOf { it.completedSets * it.reps * it.weight }
    }
    val totalPlannedSets = remember(exercises) {
        exercises.sumOf { it.sets }
    }
    val totalCompletedSets = remember(exercises) {
        exercises.sumOf { it.completedSets }
    }

    val isAllExercisesDone = exercises.isNotEmpty() && exercises.all { it.completedSets >= it.sets }

    GlowingBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 20.dp,
                end = 20.dp,
                bottom = 120.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. TOP HEADER: Routine Name, Live Timer, Volume & Finish Button
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (navController != null) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(CardDarkElevated, CircleShape)
                                        .border(1.dp, CardBorderWhite, CircleShape)
                                        .clickable { navController.popBackStack() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = "Back",
                                        tint = TextWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                            }

                            Column {
                                Text(
                                    text = currentWorkout?.name ?: "Active Workout",
                                    color = TextWhite,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(if (isSessionActive) NeonTeal else TextSilver, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isSessionActive) {
                                            "⏱ ${formatSecondsToMMSS(sessionSeconds)} Live Session"
                                        } else {
                                            val displaySec = if (recordedDurationSeconds > 0) recordedDurationSeconds else sessionSeconds
                                            "⏱ ${formatSecondsToMMSS(displaySec)} Session Finished"
                                        },
                                        color = if (isSessionActive) NeonTeal else TextSilver,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Finish & Record Workout Button
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isAllExercisesDone) NeonTeal else Color(0xFF00F2FE).copy(alpha = 0.15f),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isAllExercisesDone) NeonTeal else Color(0xFF00F2FE).copy(alpha = 0.5f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    // Freeze live timer immediately so it stops incrementing
                                    isSessionActive = false
                                    restTimerActive = false
                                    recordedDurationSeconds = sessionSeconds

                                    val finalDurationMins = maxOf(1, (sessionSeconds + 59) / 60)
                                    if (currentWorkout != null) {
                                        viewModel.updateWorkout(
                                            currentWorkout.copy(
                                                duration = finalDurationMins,
                                                completed = true,
                                                date = System.currentTimeMillis()
                                            )
                                        )
                                    }
                                    viewModel.completeWorkout(workoutId)
                                    soundAlarmManager.playWorkoutCompletedSound()
                                    showWorkoutCompletedCelebration = true
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = if (isAllExercisesDone) "✓ Finish" else "End & Save",
                                color = if (isAllExercisesDone) CardDark else Color(0xFF00F2FE),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Volume & Sets Metric Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDarkElevated, RoundedCornerShape(14.dp))
                            .border(1.dp, CardBorderWhite, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.FitnessCenter,
                                contentDescription = null,
                                tint = NeonTeal,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$currentTotalVolume kg Lifted",
                                color = if (currentTotalVolume > 0) NeonTeal else TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "$totalCompletedSets / $totalPlannedSets Sets Completed",
                            color = TextSilver,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 2. INLINE REST TIMER BAR (Directly adjacent to sets, no scrolling!)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(18.dp))
                        .border(1.dp, if (restTimerActive) NeonTeal else CardBorderWhite, RoundedCornerShape(18.dp))
                        .padding(12.dp)
                ) {
                    if (restTimerActive) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(NeonTeal, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "REST: ${formatSecondsToMMSS(restTimeRemaining)}",
                                    color = NeonTeal,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .background(CardDarkElevated, RoundedCornerShape(8.dp))
                                        .border(1.dp, CardBorderWhite, RoundedCornerShape(8.dp))
                                        .clickable { restTimeRemaining += 30 }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "+30s", color = TextWhite, fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(CardDarkElevated, RoundedCornerShape(8.dp))
                                        .border(1.dp, CardBorderWhite, RoundedCornerShape(8.dp))
                                        .clickable {
                                            restTimerActive = false
                                            restTimeRemaining = 0
                                        }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "Skip", color = TextSilver, fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Timer,
                                    contentDescription = "Rest",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Rest Interval:",
                                    color = TextSilver,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(30, 60, 90, 120).forEach { sec ->
                                    val isSelected = selectedRestPreset == sec
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isSelected) NeonCyan.copy(alpha = 0.2f) else CardDarkElevated,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) NeonCyan else CardBorderWhite,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { selectedRestPreset = sec }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${sec}s",
                                            color = if (isSelected) NeonCyan else TextSilver,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. ACTIVE EXERCISE CARDS & SET ROWS (IMMEDIATELY VISIBLE AT THE TOP)
            if (exercises.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDark, RoundedCornerShape(20.dp))
                            .border(1.dp, CardBorderWhite, RoundedCornerShape(20.dp))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "No exercises in this workout routine yet", color = TextSilver, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Use the library or custom form below to add exercises.", color = TextGray, fontSize = 12.sp)
                    }
                }
            } else {
                items(items = exercises, key = { it.id }) { exercise ->
                    ExerciseCardItem(
                        exercise = exercise,
                        selectedRestPreset = selectedRestPreset,
                        onDelete = { exerciseToDelete = exercise },
                        onAddSet = {
                            viewModel.updateExercise(
                                exercise.copy(sets = exercise.sets + 1)
                            )
                        },
                        onSetChanged = { completedSets, triggerTimer ->
                            viewModel.updateCompletedSets(exercise.id, completedSets)
                            soundAlarmManager.playSetCompletedSound()
                            if (triggerTimer && completedSets > 0) {
                                restTimeRemaining = selectedRestPreset
                                restTimerActive = true
                            }
                        }
                    )
                }
            }

            // 4. BOTTOM SECTION: QUICK EXERCISE LIBRARY PICKER (Below active workout so it doesn't push sets down)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(20.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(20.dp))
                        .padding(14.dp)
                ) {
                    Text(text = "+ Add from Exercise Library", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(exerciseCategories) { cat ->
                            val isSelected = selectedExerciseCategory == cat
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) Color(0xFF00FFA3).copy(alpha = 0.15f) else CardDarkElevated,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF00FFA3) else CardBorderWhite,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedExerciseCategory = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) Color(0xFF00FFA3) else TextSilver,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val categoryExercises = POPULAR_EXERCISE_LIBRARY[selectedExerciseCategory] ?: emptyList()
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        categoryExercises.forEach { (name, s, r) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CardDarkElevated, RoundedCornerShape(12.dp))
                                    .border(1.dp, CardBorderWhite, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = name, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "$s sets × $r reps", color = TextGray, fontSize = 11.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF00FFA3), RoundedCornerShape(8.dp))
                                        .clickable {
                                            val defaultWeight = when {
                                                name.contains("Squat", ignoreCase = true) -> 60
                                                name.contains("Deadlift", ignoreCase = true) -> 80
                                                name.contains("Bench", ignoreCase = true) -> 50
                                                name.contains("Press", ignoreCase = true) -> 35
                                                name.contains("Curl", ignoreCase = true) -> 15
                                                name.contains("Plank", ignoreCase = true) -> 0
                                                else -> 25
                                            }
                                            viewModel.addExercise(
                                                ExerciseEntity(
                                                    workoutId = workoutId,
                                                    name = name,
                                                    sets = s,
                                                    reps = r,
                                                    weight = defaultWeight
                                                )
                                            )
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(text = "+ Add", color = CardDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 5. CUSTOM EXERCISE FORM
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Text(text = "Or Create Custom Exercise", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = exerciseNameInput,
                        onValueChange = {
                            exerciseNameInput = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Exercise Name", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = TextWhite,
                            unfocusedBorderColor = CardBorderWhite,
                            focusedLabelColor = TextWhite
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = setsInput,
                            onValueChange = {
                                setsInput = it.filter { c -> c.isDigit() }
                                errorMessage = ""
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Sets", color = TextGray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = TextWhite,
                                unfocusedBorderColor = CardBorderWhite,
                                focusedLabelColor = TextWhite
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = repsInput,
                            onValueChange = {
                                repsInput = it.filter { c -> c.isDigit() }
                                errorMessage = ""
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Reps", color = TextGray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = TextWhite,
                                unfocusedBorderColor = CardBorderWhite,
                                focusedLabelColor = TextWhite
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = {
                                weightInput = it.filter { c -> c.isDigit() }
                                errorMessage = ""
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Kg", color = TextGray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = TextWhite,
                                unfocusedBorderColor = CardBorderWhite,
                                focusedLabelColor = TextWhite
                            ),
                            singleLine = true
                        )
                    }

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage, color = DangerRed, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    PrimaryButton(
                        text = "+ Save Custom Exercise",
                        onClick = {
                            val setsVal = setsInput.toIntOrNull()
                            val repsVal = repsInput.toIntOrNull()
                            val weightVal = weightInput.toIntOrNull() ?: 0

                            when {
                                exerciseNameInput.isBlank() -> errorMessage = "Enter exercise name"
                                setsVal == null || setsVal <= 0 -> errorMessage = "Minimum 1 set required"
                                repsVal == null || repsVal <= 0 -> errorMessage = "Minimum 1 rep required"
                                else -> {
                                    viewModel.addExercise(
                                        ExerciseEntity(
                                            workoutId = workoutId,
                                            name = exerciseNameInput.trim(),
                                            sets = setsVal,
                                            reps = repsVal,
                                            weight = weightVal
                                        )
                                    )
                                    exerciseNameInput = ""
                                    setsInput = ""
                                    repsInput = ""
                                    weightInput = ""
                                    errorMessage = ""
                                }
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Rest Alarm Completed Dialog
    if (showRestAlarmDialog) {
        AlertDialog(
            onDismissRequest = {
                soundAlarmManager.stopAlarmSound()
                showRestAlarmDialog = false
            },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text(text = "🔔 Rest Time Finished!", fontWeight = FontWeight.Bold) },
            text = {
                Text(text = "Rest interval completed. Ready for your next set?")
            },
            confirmButton = {
                PrimaryButton(
                    text = "Next Set",
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
                        restTimeRemaining = 30
                        restTimerActive = true
                    }
                ) {
                    Text("+30s", color = TextWhite)
                }
            }
        )
    }

    // Workout Completed Celebration Dialog
    if (showWorkoutCompletedCelebration) {
        AlertDialog(
            onDismissRequest = { showWorkoutCompletedCelebration = false },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("🎉 Workout Routine Completed!", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Great discipline! Your training session has been saved:",
                        color = TextWhite,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val displayFinalSeconds = if (recordedDurationSeconds > 0) recordedDurationSeconds else sessionSeconds
                    Text(text = "✓ Time Trained: ${formatSecondsToMMSS(displayFinalSeconds)}", color = NeonTeal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "✓ Sets Completed: $totalCompletedSets / $totalPlannedSets", color = NeonTeal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "✓ Total Volume: $currentTotalVolume kg", color = NeonTeal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                PrimaryButton(
                    text = "Done",
                    onClick = {
                        showWorkoutCompletedCelebration = false
                        navController?.popBackStack()
                    }
                )
            }
        )
    }

    // Delete Exercise Dialog
    exerciseToDelete?.let { ex ->
        AlertDialog(
            onDismissRequest = { exerciseToDelete = null },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Delete Exercise?", fontWeight = FontWeight.Bold) },
            text = { Text("Remove '${ex.name}' from this workout routine?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteExercise(ex)
                        exerciseToDelete = null
                    }
                ) {
                    Text("Delete", color = DangerRed, fontWeight = FontWeight.Bold)
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
private fun ExerciseCardItem(
    exercise: ExerciseEntity,
    selectedRestPreset: Int,
    onDelete: () -> Unit,
    onAddSet: () -> Unit,
    onSetChanged: (Int, Boolean) -> Unit
) {
    var completedSets by remember(exercise.id, exercise.completedSets) {
        mutableIntStateOf(exercise.completedSets)
    }

    val isExerciseFullyDone = completedSets >= exercise.sets && exercise.sets > 0
    val exerciseTotalVolume = completedSets * exercise.reps * exercise.weight

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(20.dp))
            .border(
                1.dp,
                if (isExerciseFullyDone) NeonTeal.copy(alpha = 0.8f) else CardBorderWhite,
                RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = exercise.name, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${exercise.sets} sets × ${exercise.reps} reps @ ${exercise.weight} kg ($exerciseTotalVolume kg lifted)",
                    color = TextSilver,
                    fontSize = 12.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onAddSet, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Set",
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete exercise",
                        tint = DangerRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Set Checkbox Grid
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(exercise.sets) { index ->
                val isCompleted = index < completedSets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isCompleted) NeonTeal.copy(alpha = 0.08f) else CardDarkElevated,
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            if (isCompleted) NeonTeal.copy(alpha = 0.6f) else CardBorderWhite,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            val newCompleted = if (isCompleted) index else index + 1
                            completedSets = newCompleted
                            onSetChanged(completedSets, !isCompleted)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isCompleted,
                            onCheckedChange = { checked ->
                                val newCompleted = if (checked) (completedSets + 1).coerceAtMost(exercise.sets) else (completedSets - 1).coerceAtLeast(0)
                                completedSets = newCompleted
                                onSetChanged(completedSets, checked)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = NeonTeal,
                                checkmarkColor = CardDark,
                                uncheckedColor = CardBorderWhite
                            )
                        )
                        Text(
                            text = "Set ${index + 1}: ${exercise.weight} kg × ${exercise.reps} reps",
                            color = if (isCompleted) NeonTeal else TextSilver,
                            fontSize = 13.sp,
                            fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    if (isCompleted) {
                        Text(
                            text = "✓ DONE",
                            color = NeonTeal,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "Tap to Complete",
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

private fun formatSecondsToMMSS(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}