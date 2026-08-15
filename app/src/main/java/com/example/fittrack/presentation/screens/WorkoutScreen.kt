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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SportsGymnastics
import androidx.compose.material.icons.rounded.Whatshot
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
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
import com.example.fittrack.presentation.viewmodel.CustomExerciseDraft
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.DangerRed
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextSilver
import com.example.fittrack.presentation.theme.TextWhite
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel

data class PredefinedWorkoutTemplate(
    val category: String,
    val title: String,
    val description: String,
    val durationMins: Int,
    val exercises: List<Triple<String, Int, Int>> // Name, Sets, Reps
)

val PREDEFINED_TEMPLATES = listOf(
    PredefinedWorkoutTemplate(
        category = "Push / Chest",
        title = "Push Day (Chest, Shoulders & Triceps)",
        description = "Heavy compound pressing & tricep isolation",
        durationMins = 45,
        exercises = listOf(
            Triple("Barbell Bench Press", 4, 8),
            Triple("Incline Dumbbell Press", 3, 10),
            Triple("Dumbbell Lateral Raises", 4, 12),
            Triple("Tricep Rope Pushdowns", 3, 12),
            Triple("Dips", 3, 10)
        )
    ),
    PredefinedWorkoutTemplate(
        category = "Pull / Back",
        title = "Pull Day (Back, Lats & Biceps)",
        description = "V-Taper lat width, heavy rows & bicep mass",
        durationMins = 45,
        exercises = listOf(
            Triple("Deadlift", 4, 5),
            Triple("Lat Pulldown", 4, 10),
            Triple("Seated Cable Row", 3, 10),
            Triple("Barbell Bicep Curls", 3, 12),
            Triple("Hammer Curls", 3, 12)
        )
    ),
    PredefinedWorkoutTemplate(
        category = "Legs",
        title = "Leg Day (Quads, Hamstrings & Calves)",
        description = "Lower body power, squats & leg press",
        durationMins = 50,
        exercises = listOf(
            Triple("Barbell Squats", 4, 8),
            Triple("Leg Press", 4, 12),
            Triple("Romanian Deadlift", 3, 10),
            Triple("Leg Extensions", 3, 15),
            Triple("Standing Calf Raises", 4, 15)
        )
    ),
    PredefinedWorkoutTemplate(
        category = "Upper Body",
        title = "Upper Body Hypertrophy",
        description = "Balanced chest, back, shoulders & arms blast",
        durationMins = 50,
        exercises = listOf(
            Triple("Incline Bench Press", 4, 8),
            Triple("Bent-Over Barbell Row", 4, 8),
            Triple("Overhead Shoulder Press", 3, 10),
            Triple("Cable Bicep Curls", 3, 12),
            Triple("Tricep Overhead Extension", 3, 12)
        )
    ),
    PredefinedWorkoutTemplate(
        category = "Arms & Abs",
        title = "Arm Blitz & Core Crusher",
        description = "Supersets for peak biceps, triceps & rock-hard abs",
        durationMins = 35,
        exercises = listOf(
            Triple("Barbell Bicep Curls", 4, 10),
            Triple("Skull Crushers", 4, 10),
            Triple("Incline Dumbbell Curls", 3, 12),
            Triple("Tricep Rope Pushdowns", 3, 12),
            Triple("Hanging Knee Raises", 3, 15),
            Triple("Planks", 3, 60)
        )
    ),
    PredefinedWorkoutTemplate(
        category = "HIIT & Cardio",
        title = "Full Body HIIT & Calorie Burn",
        description = "High intensity explosive intervals & conditioning",
        durationMins = 30,
        exercises = listOf(
            Triple("Jump Rope", 4, 100),
            Triple("Kettlebell Swings", 4, 15),
            Triple("Burpees", 4, 12),
            Triple("Mountain Climbers", 4, 30),
            Triple("Russian Twists", 3, 20)
        )
    ),
    PredefinedWorkoutTemplate(
        category = "Recovery",
        title = "Mobility & Active Stretching",
        description = "Decompression, hip mobility & post-workout recovery",
        durationMins = 25,
        exercises = listOf(
            Triple("Hamstring & Glute Stretch", 3, 10),
            Triple("Hip Flexor Opener", 3, 10),
            Triple("Cat-Cow Spine Flow", 3, 10),
            Triple("Shoulder Pass-Throughs", 3, 10)
        )
    )
)

@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel,
    navController: NavController
) {
    val workouts by viewModel.workouts.collectAsState()
    val allExercises by viewModel.allExercises.collectAsState()

    var selectedCategoryFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "Push / Chest", "Pull / Back", "Legs", "Upper Body", "Arms & Abs", "HIIT & Cardio", "Recovery")

    val filteredTemplates = remember(selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") PREDEFINED_TEMPLATES
        else PREDEFINED_TEMPLATES.filter { it.category == selectedCategoryFilter }
    }

    var showCreateCustomDialog by remember { mutableStateOf(false) }
    var customWorkoutName by remember { mutableStateOf("") }
    var customDuration by remember { mutableStateOf("45") }
    val customDraftExercises = remember { mutableStateListOf<CustomExerciseDraft>() }

    var draftExerciseName by remember { mutableStateOf("") }
    var draftSets by remember { mutableStateOf("3") }
    var draftReps by remember { mutableStateOf("10") }
    var draftWeight by remember { mutableStateOf("20") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var workoutToDelete by remember { mutableStateOf<WorkoutEntity?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    GlowingBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 20.dp,
                end = 20.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WORKOUT HUB",
                            color = TextGray,
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Workouts & Routines",
                            color = TextWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF00FFA3), RoundedCornerShape(14.dp))
                            .clickable { showCreateCustomDialog = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New",
                                tint = CardDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Custom",
                                color = CardDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Predefined Workout Templates Section
            item {
                Column {
                    Text(
                        text = "Pre-Defined Workout Templates",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Filter Chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) Color(0xFF00FFA3).copy(alpha = 0.15f) else CardDarkElevated,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF00FFA3) else CardBorderWhite,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedCategoryFilter = cat }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) Color(0xFF00FFA3) else TextSilver,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pre-defined Templates List - Fully Readable with Complete Exercise Breakdown
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        filteredTemplates.forEach { template ->
                            val catColor = when (template.category.lowercase()) {
                                "push / chest" -> Color(0xFF00F2FE)
                                "pull / back" -> Color(0xFF38BDF8)
                                "legs" -> Color(0xFF00FFA3)
                                "upper body" -> Color(0xFFA855F7)
                                "arms & abs" -> Color(0xFFFBBF24)
                                "hiit & cardio" -> Color(0xFFFF6B00)
                                else -> Color(0xFF00FFA3)
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CardDark, RoundedCornerShape(22.dp))
                                    .border(1.dp, CardBorderWhite, RoundedCornerShape(22.dp))
                                    .padding(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(catColor.copy(alpha = 0.14f), RoundedCornerShape(8.dp))
                                            .border(1.dp, catColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = template.category.uppercase(),
                                            color = catColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                    Text(
                                        text = "⏱ ${template.durationMins} mins",
                                        color = TextSilver,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = template.title,
                                    color = TextWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = template.description,
                                    color = TextSilver,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Prescribed Exercises (${template.exercises.size}):",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CardDarkElevated, RoundedCornerShape(14.dp))
                                        .border(1.dp, CardBorderWhite.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    template.exercises.forEachIndexed { idx, (name, sets, reps) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${idx + 1}. $name",
                                                color = TextWhite,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "$sets sets × $reps reps",
                                                color = Color(0xFF00FFA3),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(CardDarkElevated, RoundedCornerShape(14.dp))
                                            .border(1.dp, Color(0xFF00FFA3).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                            .clickable {
                                                viewModel.addPresetWorkout(
                                                    presetName = template.title,
                                                    durationMin = template.durationMins,
                                                    exerciseList = template.exercises
                                                )
                                            }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "+ Add to My List",
                                            color = Color(0xFF00FFA3),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(Color(0xFF00FFA3), RoundedCornerShape(14.dp))
                                            .clickable {
                                                viewModel.addPresetWorkout(
                                                    presetName = template.title,
                                                    durationMin = template.durationMins,
                                                    exerciseList = template.exercises
                                                ) { newId ->
                                                    navController.navigate("workout_detail/$newId")
                                                }
                                            }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "▶ Start Now",
                                            color = CardDark,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Saved / Custom Workouts Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Workout Routines (${workouts.size})",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )

                    if (workouts.isNotEmpty()) {
                        Text(
                            text = "Clear All",
                            color = DangerRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                workoutToDelete = null
                                showDeleteDialog = true
                            },
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            if (workouts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDark, RoundedCornerShape(22.dp))
                            .border(1.dp, CardBorderWhite, RoundedCornerShape(22.dp))
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FitnessCenter,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No saved workouts yet",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap any template above or '+ Custom' to create your own routine!",
                            color = TextGray,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(items = workouts, key = { it.id }) { workout ->
                    val routineExercises = allExercises.filter { it.workoutId == workout.id }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDark, RoundedCornerShape(22.dp))
                            .border(1.dp, CardBorderWhite, RoundedCornerShape(22.dp))
                            .padding(18.dp)
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
                                        .background(Color(0xFF00FFA3).copy(alpha = 0.14f), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFF00FFA3).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "MY ROUTINE",
                                        color = Color(0xFF00FFA3),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }

                                if (workout.completed) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF10B981).copy(alpha = 0.14f), RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "COMPLETED",
                                            color = Color(0xFF10B981),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "⏱ ${workout.duration} mins",
                                    color = TextSilver,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        workoutToDelete = workout
                                        showDeleteDialog = true
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = DangerRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = workout.name,
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Configured Exercises (${routineExercises.size}):",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        if (routineExercises.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CardDarkElevated, RoundedCornerShape(14.dp))
                                    .border(1.dp, CardBorderWhite.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                routineExercises.forEachIndexed { idx, ex ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${idx + 1}. ${ex.name}",
                                            color = TextWhite,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "${ex.sets} sets × ${ex.reps} reps • ${ex.weight} kg",
                                            color = Color(0xFF00FFA3),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CardDarkElevated, RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "• No exercises added yet. Tap below to start and add exercises.",
                                    color = TextGray,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        PrimaryButton(
                            text = "▶ Open & Track Workout",
                            onClick = {
                                navController.navigate("workout_detail/${workout.id}")
                            }
                        )
                    }
                }
            }
        }
    }

    // Multi-Exercise Custom Workout Creation Modal Dialog
    if (showCreateCustomDialog) {
        val suggestionChips = listOf(
            "Barbell Bench Press", "Incline DB Press", "Barbell Squats",
            "Deadlifts", "Pull-ups", "Lateral Raises", "Barbell Rows",
            "Bicep Curls", "Tricep Pushdowns", "Leg Press", "Dips"
        )

        AlertDialog(
            onDismissRequest = { showCreateCustomDialog = false },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Create Custom Routine", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("1. Routine Details", color = Color(0xFF00FFA3), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customWorkoutName,
                        onValueChange = {
                            customWorkoutName = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Routine Name (e.g. Upper Hypertrophy)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = Color(0xFF00FFA3),
                            unfocusedBorderColor = CardBorderWhite,
                            focusedLabelColor = Color(0xFF00FFA3)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customDuration,
                        onValueChange = {
                            customDuration = it.filter { c -> c.isDigit() }
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Target Duration (mins)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = Color(0xFF00FFA3),
                            unfocusedBorderColor = CardBorderWhite,
                            focusedLabelColor = Color(0xFF00FFA3)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("2. Add Multiple Exercises", color = Color(0xFF00FFA3), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick suggestion chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(suggestionChips) { chip ->
                            Box(
                                modifier = Modifier
                                    .background(CardDarkElevated, RoundedCornerShape(10.dp))
                                    .border(1.dp, CardBorderWhite, RoundedCornerShape(10.dp))
                                    .clickable {
                                        draftExerciseName = chip
                                        errorMessage = ""
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(text = chip, color = TextSilver, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = draftExerciseName,
                        onValueChange = {
                            draftExerciseName = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Exercise Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = Color(0xFF00F2FE),
                            unfocusedBorderColor = CardBorderWhite,
                            focusedLabelColor = Color(0xFF00F2FE)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = draftSets,
                            onValueChange = { draftSets = it.filter { c -> c.isDigit() } },
                            modifier = Modifier.weight(1f),
                            label = { Text("Sets") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = Color(0xFF00F2FE),
                                unfocusedBorderColor = CardBorderWhite,
                                focusedLabelColor = Color(0xFF00F2FE)
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = draftReps,
                            onValueChange = { draftReps = it.filter { c -> c.isDigit() } },
                            modifier = Modifier.weight(1f),
                            label = { Text("Reps") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = Color(0xFF00F2FE),
                                unfocusedBorderColor = CardBorderWhite,
                                focusedLabelColor = Color(0xFF00F2FE)
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = draftWeight,
                            onValueChange = { draftWeight = it.filter { c -> c.isDigit() } },
                            modifier = Modifier.weight(1f),
                            label = { Text("Kg") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = Color(0xFF00F2FE),
                                unfocusedBorderColor = CardBorderWhite,
                                focusedLabelColor = Color(0xFF00F2FE)
                            ),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Button to Add Exercise to Current List
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDarkElevated, RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF00F2FE).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .clickable {
                                if (draftExerciseName.isBlank()) {
                                    errorMessage = "Please enter exercise name"
                                } else {
                                    val s = draftSets.toIntOrNull() ?: 3
                                    val r = draftReps.toIntOrNull() ?: 10
                                    val w = draftWeight.toIntOrNull() ?: 20
                                    customDraftExercises.add(
                                        CustomExerciseDraft(
                                            name = draftExerciseName.trim(),
                                            sets = s,
                                            reps = r,
                                            weight = w
                                        )
                                    )
                                    draftExerciseName = ""
                                    errorMessage = ""
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+ Add Exercise to Routine",
                            color = Color(0xFF00F2FE),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Added Exercises List Preview
                    Text("Added Exercises (${customDraftExercises.size}):", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    if (customDraftExercises.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardDarkElevated, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Text("No exercises added yet. Use the inputs above to add exercises.", color = TextGray, fontSize = 11.sp)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardDarkElevated, RoundedCornerShape(10.dp))
                                .border(1.dp, CardBorderWhite.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            customDraftExercises.forEachIndexed { index, ex ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${index + 1}. ${ex.name}", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Text("${ex.sets} sets × ${ex.reps} reps • ${ex.weight} kg", color = Color(0xFF00FFA3), fontSize = 11.sp)
                                    }
                                    IconButton(
                                        onClick = { customDraftExercises.removeAt(index) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = DangerRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage, color = DangerRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val dur = customDuration.toIntOrNull()
                        when {
                            customWorkoutName.isBlank() -> errorMessage = "Enter routine name"
                            dur == null || dur <= 0 -> errorMessage = "Enter valid duration"
                            customDraftExercises.isEmpty() -> errorMessage = "Please add at least 1 exercise"
                            else -> {
                                viewModel.addCustomWorkoutWithExercises(
                                    name = customWorkoutName.trim(),
                                    durationMin = dur,
                                    exercises = customDraftExercises.toList()
                                )
                                customWorkoutName = ""
                                customDuration = "45"
                                customDraftExercises.clear()
                                draftExerciseName = ""
                                showCreateCustomDialog = false
                            }
                        }
                    }
                ) {
                    Text("Save to My Routines", color = Color(0xFF00FFA3), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCreateCustomDialog = false
                    customDraftExercises.clear()
                    draftExerciseName = ""
                }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = {
                Text(
                    text = if (workoutToDelete != null) "Delete Workout Routine?" else "Clear All Routines?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (workoutToDelete != null)
                        "Permanently delete '${workoutToDelete?.name}' and its exercises?"
                    else
                        "Permanently delete all custom and saved workouts?"
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
                    Text("Delete", color = DangerRed, fontWeight = FontWeight.Bold)
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