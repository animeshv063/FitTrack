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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fittrack.data.local.entity.WorkoutEntity
import com.example.fittrack.data.sound.SoundAlarmManager
import com.example.fittrack.navigation.Routes
import com.example.fittrack.presentation.components.ActivityCard
import com.example.fittrack.presentation.components.CalendarStreakCard
import com.example.fittrack.presentation.components.FitTrackLogo
import com.example.fittrack.presentation.components.GlowingBackground
import com.example.fittrack.presentation.components.PrimaryButton
import com.example.fittrack.presentation.components.StatCard
import com.example.fittrack.presentation.components.WorkoutCard
import com.example.fittrack.presentation.theme.CardBorderActive
import com.example.fittrack.presentation.theme.CardBorderWhite
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.DangerRed
import com.example.fittrack.presentation.theme.FlameOrange
import com.example.fittrack.presentation.theme.NeonCyan
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextSilver
import com.example.fittrack.presentation.theme.TextWhite
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val latestWorkout = workouts.firstOrNull { !it.completed } ?: workouts.firstOrNull()

    val totalWaterMl = waterLogs.sumOf { it.amountMl }
    val dynamicTitle = viewModel.getAthleteTitle(completedWorkouts)

    val context = LocalContext.current
    val soundAlarmManager = remember { SoundAlarmManager(context) }
    var showStepGoalCelebrationDialog by remember { mutableStateOf(false) }

    val todayDateString = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    var visible by remember { mutableStateOf(false) }
    var workoutToDelete by remember { mutableStateOf<WorkoutEntity?>(null) }

    LaunchedEffect(Unit) {
        visible = true
        viewModel.startStepCounter()
    }

    // Step Goal Achievement Celebration on App Launch / Re-open
    LaunchedEffect(steps, userProfile) {
        val currentGoal = userProfile?.stepGoal ?: 10000
        if (steps >= currentGoal && currentGoal > 0) {
            val prefs = context.getSharedPreferences("fittrack_celebrations", android.content.Context.MODE_PRIVATE)
            val lastCelebrated = prefs.getString("last_step_goal_celebrated_date", null)
            if (lastCelebrated != todayDateString) {
                prefs.edit().putString("last_step_goal_celebrated_date", todayDateString).apply()
                soundAlarmManager.playWorkoutCompletedSound()
                showStepGoalCelebrationDialog = true
            }
        }
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
                // Header Card with FitTrack Logo Branding
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FitTrackLogo(size = 32.dp, showText = true)

                    Box(
                        modifier = Modifier
                            .background(CardDarkElevated, RoundedCornerShape(14.dp))
                            .border(1.dp, CardBorderActive, RoundedCornerShape(14.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.EmojiEvents,
                                contentDescription = "Rank",
                                tint = TextWhite,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = dynamicTitle,
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Welcome back, ${userProfile?.name ?: "Athlete"}",
                    color = TextSilver,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Clean Minimal Training Session Hero Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderActive, RoundedCornerShape(24.dp))
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
                                    .size(38.dp)
                                    .background(CardDarkElevated, CircleShape)
                                    .border(1.dp, CardBorderActive.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.FitnessCenter,
                                    contentDescription = "Workout",
                                    tint = TextWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "TRAINING SESSION",
                                    color = TextGray,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = latestWorkout?.name ?: "Ready for Training",
                                    color = TextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (latestWorkout != null)
                            "${latestWorkout.duration} mins target • Record sets & rest stopwatch"
                        else
                            "Start a workout session or pick a template to begin.",
                        color = TextSilver,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PrimaryButton(
                        text = if (latestWorkout != null) "▶ Resume Training Session" else "▶ Start Training Session",
                        onClick = {
                            if (latestWorkout != null) {
                                navController.navigate("workout_detail/${latestWorkout.id}")
                            } else {
                                navController.navigate(Routes.Workout.route)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Grid
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

                // Step Activity Card with Customizable Step Goal
                ActivityCard(
                    steps = steps,
                    calories = calories,
                    stepGoal = userProfile?.stepGoal ?: 10000,
                    onUpdateStepGoal = { newGoal ->
                        viewModel.updateStepGoal(newGoal)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Hydration Quick Logger Card with Customizable Goal & Progress Bar
                val currentWaterGoal = userProfile?.waterGoalMl ?: 3000
                val waterProgress = (totalWaterMl.toFloat() / currentWaterGoal.toFloat()).coerceIn(0f, 1f)
                val isGoalAchieved = totalWaterMl >= currentWaterGoal

                var showEditWaterGoalDialog by remember { mutableStateOf(false) }
                var customWaterInput by remember { mutableStateOf(currentWaterGoal.toString()) }
                var showHydrationCelebrationDialog by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(
                            1.dp,
                            if (isGoalAchieved) Color(0xFF38BDF8).copy(alpha = 0.8f) else CardBorderWhite,
                            RoundedCornerShape(24.dp)
                        )
                        .padding(18.dp)
                ) {
                    // 1. Top Row: Icon + "Hydration" on left, [Target: 3000ml] button on right with generous spacing
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
                                    .border(
                                        1.dp,
                                        if (isGoalAchieved) Color(0xFF38BDF8) else CardBorderActive.copy(alpha = 0.3f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.WaterDrop,
                                    contentDescription = "Hydration",
                                    tint = if (isGoalAchieved) Color(0xFF38BDF8) else TextWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Hydration",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(Color(0xFF38BDF8).copy(alpha = 0.14f), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                                .clickable {
                                    customWaterInput = currentWaterGoal.toString()
                                    showEditWaterGoalDialog = true
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = "Edit Water Goal",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Target: ${currentWaterGoal}ml",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 2. Full-width Subtitle: 100% readable with zero ellipsis
                    Text(
                        text = if (isGoalAchieved) "🎉 Daily Hydration Goal Achieved (${totalWaterMl}ml)!" else "Hydration Logger • ${((1f - waterProgress) * currentWaterGoal).toInt()} ml remaining",
                        color = if (isGoalAchieved) Color(0xFF38BDF8) else TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$totalWaterMl ml logged",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(waterProgress * 100).toInt()}%",
                                color = if (isGoalAchieved) Color(0xFF38BDF8) else TextSilver,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(CardDarkElevated, CircleShape)
                        ) {
                            if (waterProgress > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(waterProgress)
                                        .height(8.dp)
                                        .background(
                                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                                colors = listOf(Color(0xFF38BDF8), Color(0xFF00F2FE))
                                            ),
                                            CircleShape
                                        )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(CardDarkElevated, RoundedCornerShape(14.dp))
                                .border(1.dp, CardBorderWhite, RoundedCornerShape(14.dp))
                                .clickable {
                                    val prev = totalWaterMl
                                    viewModel.addWaterLog(250)
                                    if (prev < currentWaterGoal && (prev + 250) >= currentWaterGoal && (userProfile?.celebrationAnimationsEnabled != false)) {
                                        showHydrationCelebrationDialog = true
                                    }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+250 ml", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(CardDarkElevated, RoundedCornerShape(14.dp))
                                .border(1.dp, CardBorderWhite, RoundedCornerShape(14.dp))
                                .clickable {
                                    val prev = totalWaterMl
                                    viewModel.addWaterLog(500)
                                    if (prev < currentWaterGoal && (prev + 500) >= currentWaterGoal && (userProfile?.celebrationAnimationsEnabled != false)) {
                                        showHydrationCelebrationDialog = true
                                    }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+500 ml", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                        }

                        if (waterLogs.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .background(CardDarkElevated, RoundedCornerShape(14.dp))
                                    .border(1.dp, DangerRed.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                    .clickable { viewModel.deleteLastWaterLog() }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Undo", color = DangerRed, fontSize = 13.sp, maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }

                // Edit Water Target Dialog
                if (showEditWaterGoalDialog) {
                    AlertDialog(
                        onDismissRequest = { showEditWaterGoalDialog = false },
                        containerColor = CardDark,
                        titleContentColor = TextWhite,
                        textContentColor = TextSilver,
                        title = { Text("Customize Daily Hydration Goal", fontWeight = FontWeight.Bold) },
                        text = {
                            Column {
                                Text(
                                    text = "Select preset or enter your custom water intake goal:",
                                    color = TextSilver,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Quick presets
                                androidx.compose.foundation.lazy.LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val presets = listOf(1500, 2000, 2500, 3000, 3500, 4000)
                                    items(presets.size) { i ->
                                        val ml = presets[i]
                                        val isSel = customWaterInput == ml.toString()
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (isSel) TextWhite else CardDarkElevated,
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSel) CardBorderActive else CardBorderWhite,
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .clickable { customWaterInput = ml.toString() }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "${ml}ml",
                                                color = if (isSel) CardDark else TextWhite,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                androidx.compose.material3.OutlinedTextField(
                                    value = customWaterInput,
                                    onValueChange = { customWaterInput = it.filter { c -> c.isDigit() } },
                                    label = { Text("Daily Water Target (ml)") },
                                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = TextWhite,
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
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    val newGoal = customWaterInput.toIntOrNull() ?: 3000
                                    if (newGoal >= 500) {
                                        viewModel.updateWaterGoal(newGoal)
                                        showEditWaterGoalDialog = false
                                    }
                                }
                            ) {
                                Text("Save Target", color = TextWhite, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            androidx.compose.material3.TextButton(onClick = { showEditWaterGoalDialog = false }) {
                                Text("Cancel", color = TextGray)
                            }
                        }
                    )
                }

                // Hydration Goal Crushed Celebration Dialog
                if (showHydrationCelebrationDialog) {
                    AlertDialog(
                        onDismissRequest = { showHydrationCelebrationDialog = false },
                        containerColor = CardDark,
                        titleContentColor = TextWhite,
                        textContentColor = TextSilver,
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.WaterDrop,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Daily Hydration Goal Achieved!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        },
                        text = {
                            Column {
                                Text(
                                    text = "Congratulations! You have reached your daily hydration target of ${totalWaterMl} of ${currentWaterGoal} ml.",
                                    color = TextWhite,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "✓ Optimal cellular hydration active\n✓ Maximum muscle nutrient transport\n✓ Accelerated metabolic recovery",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        },
                        confirmButton = {
                            PrimaryButton(
                                text = "Continue",
                                onClick = { showHydrationCelebrationDialog = false }
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(110.dp))
            }
        }
    }

    // Step Goal Achieved Celebration Modal Dialog
    if (showStepGoalCelebrationDialog) {
        val currentGoal = userProfile?.stepGoal ?: 10000
        val estDistanceKm = steps * 0.00075
        val estCalories = (steps * 0.04).toInt()

        AlertDialog(
            onDismissRequest = { showStepGoalCelebrationDialog = false },
            containerColor = CardDark,
            shape = RoundedCornerShape(24.dp),
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF00FFA3).copy(alpha = 0.3f),
                                        Color(0xFF00FFA3).copy(alpha = 0.05f),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            )
                            .border(2.dp, Color(0xFF00FFA3), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "Trophy",
                            tint = Color(0xFF00FFA3),
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Daily Step Goal Conquered! 🏆",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Outstanding dedication! You have successfully reached your daily target of ${String.format(Locale.US, "%,d", currentGoal)} steps by logging ${String.format(Locale.US, "%,d", steps)} steps today.",
                        color = TextSilver,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3-Metric Summary Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDarkElevated, RoundedCornerShape(16.dp))
                            .border(1.dp, CardBorderActive.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Total Steps", color = TextGray, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format(Locale.US, "%,d", steps),
                                color = Color(0xFF00FFA3),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Burned", color = TextGray, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$estCalories kcal",
                                color = FlameOrange,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Distance", color = TextGray, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format(Locale.US, "%.2f km", estDistanceKm),
                                color = NeonCyan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your consistency is building unstoppable momentum.",
                        color = TextGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    PrimaryButton(
                        text = "Keep Crushing It 🔥",
                        onClick = { showStepGoalCelebrationDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
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