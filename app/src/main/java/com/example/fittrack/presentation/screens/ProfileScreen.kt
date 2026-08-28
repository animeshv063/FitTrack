package com.example.fittrack.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.fittrack.data.local.entity.GoalEntity
import com.example.fittrack.data.local.entity.UserProfileEntity
import com.example.fittrack.presentation.components.GiftBurstDialog
import com.example.fittrack.presentation.components.GlowingBackground
import com.example.fittrack.presentation.components.PrimaryButton
import com.example.fittrack.presentation.components.StatCard
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
fun ProfileScreen(
    viewModel: WorkoutViewModel
) {
    val context = LocalContext.current

    val userProfile by viewModel.userProfile.collectAsState()
    val steps by viewModel.steps.collectAsState()
    val workouts by viewModel.workouts.collectAsState()
    val exercises by viewModel.allExercises.collectAsState()
    val goals by viewModel.goals.collectAsState()

    val activeGoals = remember(goals) { goals.filter { !it.isCompleted } }
    val achievedGoals = remember(goals) { goals.filter { it.isCompleted } }

    LaunchedEffect(Unit) {
        viewModel.startStepCounter()
    }

    val completedWorkouts = workouts.count { it.completed }
    val totalVolume = exercises.sumOf { it.sets * it.reps * it.weight }
    val currentProfile = userProfile ?: UserProfileEntity()
    val dynamicTitle = viewModel.getAthleteTitle(completedWorkouts)

    val weightDisplay = if (currentProfile.weightKg > 0f) "${currentProfile.weightKg} kg" else "--"
    val heightDisplay = if (currentProfile.heightCm > 0f) "${currentProfile.heightCm.toInt()} cm" else "--"

    val heightM = currentProfile.heightCm / 100f
    val bmiDisplay = if (currentProfile.weightKg > 0f && heightM > 0f) {
        String.format("%.1f", currentProfile.weightKg / (heightM * heightM))
    } else {
        "--"
    }

    var showPhotoOptionsDialog by remember { mutableStateOf(false) }
    var showCropDialog by remember { mutableStateOf(false) }
    var tempCropPhotoUri by remember { mutableStateOf<Uri?>(null) }



    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { pickedUri ->
            tempCropPhotoUri = pickedUri
            showCropDialog = true
        }
    }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(currentProfile.name) }
    var editGender by remember { mutableStateOf(currentProfile.gender) }
    var editWeight by remember { mutableStateOf(if (currentProfile.weightKg > 0f) currentProfile.weightKg.toString() else "") }
    var editHeight by remember { mutableStateOf(if (currentProfile.heightCm > 0f) currentProfile.heightCm.toString() else "") }
    var editStepGoal by remember { mutableStateOf(currentProfile.stepGoal.toString()) }
    var editWaterGoal by remember { mutableStateOf(currentProfile.waterGoalMl.toString()) }

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var goalTitleInput by remember { mutableStateOf("") }
    var goalTargetInput by remember { mutableStateOf("") }
    var goalUnitInput by remember { mutableStateOf("kg") }
    var goalToDelete by remember { mutableStateOf<GoalEntity?>(null) }
    var achievedGoalToDelete by remember { mutableStateOf<GoalEntity?>(null) }

    // Gift Burst Animation State
    var celebratingGoal by remember { mutableStateOf<GoalEntity?>(null) }

    // Multi-Step Confirmation Dialogs for Reset
    var showResetProfileDialog by remember { mutableStateOf(false) }
    var showResetAllDataConfirmDialog by remember { mutableStateOf(false) }

    GlowingBackground {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 30 })
        ) {
            val statusBarsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            val safeTopPadding = if (statusBarsTop > 0.dp) statusBarsTop + 20.dp else 52.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = safeTopPadding, bottom = 120.dp)
            ) {
                // Header
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ATHLETE PROFILE",
                        color = TextGray,
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Personal Stats & Goals",
                        color = TextWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Profile Header Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(CardDarkElevated, RoundedCornerShape(24.dp))
                            .border(1.5.dp, CardBorderActive, RoundedCornerShape(24.dp))
                            .clickable {
                                if (currentProfile.profileImageUri != null) {
                                    showPhotoOptionsDialog = true
                                } else {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentProfile.profileImageUri != null) {
                            AsyncImage(
                                model = Uri.parse(currentProfile.profileImageUri),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(24.dp))
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "Avatar",
                                tint = TextWhite,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        // Edit / Change Photo Button (Bottom-End)
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.BottomEnd)
                                .background(TextWhite, CircleShape)
                                .border(1.5.dp, CardDark, CircleShape)
                                .clickable {
                                    if (currentProfile.profileImageUri != null) {
                                        showPhotoOptionsDialog = true
                                    } else {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PhotoCamera,
                                contentDescription = "Change photo",
                                tint = CardDark,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = currentProfile.name, color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(CardDarkElevated, CircleShape)
                            .border(1.dp, CardBorderActive, CircleShape)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "Badge",
                            tint = TextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = dynamicTitle, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    PrimaryButton(
                        text = "Edit Profile & Metrics",
                        onClick = {
                            editName = currentProfile.name
                            editGender = currentProfile.gender
                            editWeight = if (currentProfile.weightKg > 0f) currentProfile.weightKg.toString() else ""
                            editHeight = if (currentProfile.heightCm > 0f) currentProfile.heightCm.toString() else ""
                            editStepGoal = currentProfile.stepGoal.toString()
                            editWaterGoal = currentProfile.waterGoalMl.toString()
                            showEditProfileDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats 2x2 Grid
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Weight",
                        value = weightDisplay,
                        modifier = Modifier.weight(1f),
                        vectorIcon = Icons.Rounded.FitnessCenter
                    )
                    StatCard(
                        title = "Height",
                        value = heightDisplay,
                        modifier = Modifier.weight(1f),
                        vectorIcon = Icons.Rounded.Person
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "BMI Index",
                        value = bmiDisplay,
                        modifier = Modifier.weight(1f),
                        vectorIcon = Icons.Rounded.EmojiEvents
                    )
                    StatCard(
                        title = "Gender",
                        value = currentProfile.gender,
                        modifier = Modifier.weight(1f),
                        vectorIcon = Icons.Rounded.Person
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Active Custom Goals Card
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
                                    .border(1.dp, CardBorderActive.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.TrackChanges,
                                    contentDescription = "Goals",
                                    tint = TextWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Active Custom Goals (${activeGoals.size})",
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
                                .clickable { showAddGoalDialog = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "+ Add Goal",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (activeGoals.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No active custom goals. Tap '+ Add Goal' to track new milestones!", color = TextGray, fontSize = 13.sp)
                        }
                    } else {
                        activeGoals.forEach { goal ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(CardDarkElevated, RoundedCornerShape(16.dp))
                                    .border(1.dp, CardBorderWhite, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = goal.title,
                                        color = TextWhite,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Target: ${goal.targetValue} ${goal.unit}",
                                        color = TextGray,
                                        fontSize = 12.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Mark as Done / Complete button (Triggers Gift Burst celebration!)
                                    Box(
                                        modifier = Modifier
                                            .background(CardDark, RoundedCornerShape(10.dp))
                                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(10.dp))
                                            .clickable {
                                                celebratingGoal = goal
                                                viewModel.completeGoal(goal)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = "Complete",
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(text = "Done", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(
                                        onClick = { goalToDelete = goal },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Goal",
                                            tint = DangerRed.copy(alpha = 0.8f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Achieved Goals (Trophy Cabinet / Stored in Phone)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(CardDarkElevated, CircleShape)
                                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.MilitaryTech,
                                    contentDescription = "Achieved",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Achieved Goals (${achievedGoals.size})",
                                    color = TextWhite,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Text(
                                    text = "Milestones Conquered & Saved",
                                    color = TextGray,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (achievedGoals.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardDarkElevated, RoundedCornerShape(14.dp))
                                .border(1.dp, CardBorderWhite, RoundedCornerShape(14.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "No achieved goals yet", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Complete your active goals above to add them here!", color = TextGray, fontSize = 11.sp, maxLines = 1)
                            }
                        }
                    } else {
                        achievedGoals.forEach { goal ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(CardDarkElevated, RoundedCornerShape(16.dp))
                                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "✓", color = Color(0xFF10B981), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = goal.title,
                                            color = TextWhite,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Achieved: ${goal.targetValue} ${goal.unit}",
                                        color = TextSilver,
                                        fontSize = 12.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(CardDark, RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = "Achieved", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(
                                        onClick = { achievedGoalToDelete = goal },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Achieved Goal",
                                            tint = DangerRed.copy(alpha = 0.8f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Display & Ambient Aurora Glow Animation Toggle Card
                val isGlowAnimationActive by com.example.fittrack.presentation.components.BackgroundAnimationManager.isGlowEnabled.collectAsState()

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
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(CardDarkElevated, CircleShape)
                                    .border(1.dp, if (isGlowAnimationActive) Color(0xFF00F2FE).copy(alpha = 0.5f) else CardBorderWhite, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.TrackChanges,
                                    contentDescription = "Aurora Glow",
                                    tint = if (isGlowAnimationActive) Color(0xFF00F2FE) else TextGray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Ambient Aurora Glow",
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isGlowAnimationActive) "Calm breathing aurora waves enabled" else "Solid pure AMOLED dark mode",
                                    color = TextGray,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Toggle Switch Pill
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isGlowAnimationActive) Color(0xFF00F2FE) else CardDarkElevated,
                                    RoundedCornerShape(16.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isGlowAnimationActive) Color(0xFF00F2FE) else CardBorderWhite,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    com.example.fittrack.presentation.components.BackgroundAnimationManager.setGlowEnabled(!isGlowAnimationActive)
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = if (isGlowAnimationActive) "ON" else "OFF",
                                color = if (isGlowAnimationActive) CardDark else TextSilver,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Account & Data Persistence Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Text(text = "Data Persistence & Reset Options", color = TextWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "All workouts, steps, logs, goals, and stats are saved permanently on this device until you explicitly reset them.",
                        color = TextGray,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(CardDarkElevated, RoundedCornerShape(14.dp))
                                .border(1.dp, CardBorderWhite, RoundedCornerShape(14.dp))
                                .clickable { showResetProfileDialog = true }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Reset Profile Info", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(CardDarkElevated, RoundedCornerShape(14.dp))
                                .border(1.dp, DangerRed.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                                .clickable { showResetAllDataConfirmDialog = true }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Erase All History", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                        }
                    }
                }



                Spacer(modifier = Modifier.height(20.dp))

                // About FitTrack & Developer Credits
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "FitTrack v2.0 • AMOLED Performance Edition",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Designed & Developed by Animesh Verma",
                        color = Color(0xFF00FFA3),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Computer Science Student & Android Developer",
                        color = TextSilver,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDarkElevated, RoundedCornerShape(14.dp))
                            .border(1.dp, CardBorderActive.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column {
                            Text(
                                text = "⚡ Engineering & Tech Stack",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Pure Kotlin & Jetpack Compose declarative UI\n• Local Room Database with zero-bloat offline persistence\n• Hardware Step Sensor foreground tracking service\n• Strict workout volume & stopwatch analytics engine",
                                color = TextGray,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Direct Send Feedback Button to animeshv063@gmail.com
                    val context = LocalContext.current
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF00F2FE), Color(0xFF00FFA3))
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                try {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:animeshv063@gmail.com")
                                        putExtra(Intent.EXTRA_SUBJECT, "FitTrack App Feedback & Suggestions")
                                    }
                                    context.startActivity(emailIntent)
                                } catch (e: Exception) {
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "message/rfc822"
                                        putExtra(Intent.EXTRA_EMAIL, arrayOf("animeshv063@gmail.com"))
                                        putExtra(Intent.EXTRA_SUBJECT, "FitTrack App Feedback")
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Send Feedback via Email"))
                                }
                            }
                            .padding(vertical = 14.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Email,
                                contentDescription = "Email Feedback",
                                tint = CardDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Send Feedback (animeshv063@gmail.com)",
                                color = CardDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(110.dp))
            }
        }
    }

    // Gift Burst Celebration Animation Dialog
    celebratingGoal?.let { goal ->
        GiftBurstDialog(
            goalTitle = goal.title,
            goalTarget = "${goal.targetValue} ${goal.unit}",
            onDismiss = { celebratingGoal = null }
        )
    }

    // Photo Action Options Dialog
    if (showPhotoOptionsDialog && currentProfile.profileImageUri != null) {
        Dialog(onDismissRequest = { showPhotoOptionsDialog = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardDark, RoundedCornerShape(24.dp))
                    .border(1.dp, CardBorderActive, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Profile Photo",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Local Storage & Precision Crop",
                            color = Color(0xFF00FFA3),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(onClick = { showPhotoOptionsDialog = false }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = TextGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action: Choose New Photo & Crop
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDarkElevated, RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(16.dp))
                        .clickable {
                            showPhotoOptionsDialog = false
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF00FFA3).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PhotoCamera,
                                contentDescription = "Choose Photo",
                                tint = Color(0xFF00FFA3),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Choose New Photo", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Pick from gallery & open Crop/Zoom", color = TextGray, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action: Adjust Crop & Precision Zoom on current photo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDarkElevated, RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorderActive, RoundedCornerShape(16.dp))
                        .clickable {
                            showPhotoOptionsDialog = false
                            currentProfile.profileImageUri?.let { uriStr ->
                                tempCropPhotoUri = Uri.parse(uriStr)
                                showCropDialog = true
                            }
                        }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF00FFA3).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Crop,
                                contentDescription = "Adjust Crop",
                                tint = Color(0xFF00FFA3),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Adjust Crop & Precision Zoom", color = Color(0xFF00FFA3), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Reposition, zoom in/out, and re-frame", color = TextGray, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action: Remove Photo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DangerRed.copy(alpha = 0.10f), RoundedCornerShape(16.dp))
                        .border(1.dp, DangerRed.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .clickable {
                            viewModel.saveUserProfile(currentProfile.copy(profileImageUri = null))
                            showPhotoOptionsDialog = false
                        }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(DangerRed.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Remove Photo",
                                tint = DangerRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Remove Photo", color = DangerRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Revert back to default avatar", color = TextGray, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Interactive Local Photo Crop & Precision Zoom Modal
    if (showCropDialog && tempCropPhotoUri != null) {
        Dialog(onDismissRequest = { showCropDialog = false }) {
            val density = LocalDensity.current
            val containerSizeDp = 270.dp
            val containerSizePx = with(density) { containerSizeDp.toPx() }

            var scale by remember { mutableStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardDark, RoundedCornerShape(24.dp))
                    .border(1.dp, CardBorderActive, RoundedCornerShape(24.dp))
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Crop & Precision Zoom",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pinch, drag, or slider to frame photo",
                            color = Color(0xFF00FFA3),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(onClick = { showCropDialog = false }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = TextGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Crop Viewport Box
                Box(
                    modifier = Modifier
                        .size(containerSizeDp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF080C10))
                        .border(1.5.dp, CardBorderActive, RoundedCornerShape(24.dp))
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(0.8f, 4.5f)
                                offset = Offset(offset.x + pan.x, offset.y + pan.y)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = tempCropPhotoUri,
                        contentDescription = "Crop Frame Preview",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    )

                    // Rule-of-thirds framing grid
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeColor = Color.White.copy(alpha = 0.2f)
                        val strokeW = 1.dp.toPx()

                        // Horizontal lines
                        drawLine(
                            color = strokeColor,
                            start = Offset(0f, size.height / 3f),
                            end = Offset(size.width, size.height / 3f),
                            strokeWidth = strokeW
                        )
                        drawLine(
                            color = strokeColor,
                            start = Offset(0f, 2f * size.height / 3f),
                            end = Offset(size.width, 2f * size.height / 3f),
                            strokeWidth = strokeW
                        )
                        // Vertical lines
                        drawLine(
                            color = strokeColor,
                            start = Offset(size.width / 3f, 0f),
                            end = Offset(size.width / 3f, size.height),
                            strokeWidth = strokeW
                        )
                        drawLine(
                            color = strokeColor,
                            start = Offset(2f * size.width / 3f, 0f),
                            end = Offset(2f * size.width / 3f, size.height),
                            strokeWidth = strokeW
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Precision Zoom Header & Percentage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PRECISION ZOOM",
                        color = TextSilver,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${(scale * 100).toInt()}%",
                        color = Color(0xFF00FFA3),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Precision Zoom Slider Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { scale = (scale - 0.15f).coerceAtLeast(0.8f) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ZoomOut,
                            contentDescription = "Zoom Out",
                            tint = TextSilver,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Slider(
                        value = scale,
                        onValueChange = { scale = it },
                        valueRange = 0.8f..4.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00FFA3),
                            activeTrackColor = Color(0xFF00FFA3),
                            inactiveTrackColor = CardBorderWhite
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { scale = (scale + 0.15f).coerceAtMost(4.5f) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ZoomIn,
                            contentDescription = "Zoom In",
                            tint = TextSilver,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Reset position button
                TextButton(
                    onClick = {
                        scale = 1f
                        offset = Offset.Zero
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.RestartAlt,
                        contentDescription = "Reset",
                        tint = TextSilver,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Position & Zoom", color = TextSilver, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { showCropDialog = false }) {
                        Text("Cancel", color = TextGray)
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF00FFA3), RoundedCornerShape(14.dp))
                            .clickable {
                                tempCropPhotoUri?.let { uri ->
                                    val croppedUri = saveCroppedProfileImage(
                                        context = context,
                                        imageUri = uri,
                                        scale = scale,
                                        offset = offset,
                                        containerSizePx = containerSizePx
                                    ) ?: uri
                                    viewModel.saveUserProfile(
                                        currentProfile.copy(profileImageUri = croppedUri.toString())
                                    )
                                }
                                showCropDialog = false
                            }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Save & Apply",
                            color = CardDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }


    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Edit Athlete Info", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    // Photo Preview & Change in Edit Athlete Info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDarkElevated, RoundedCornerShape(16.dp))
                            .border(1.dp, CardBorderActive, RoundedCornerShape(16.dp))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(CardDark, RoundedCornerShape(14.dp))
                                .border(1.dp, CardBorderWhite, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentProfile.profileImageUri != null) {
                                AsyncImage(
                                    model = Uri.parse(currentProfile.profileImageUri),
                                    contentDescription = "Current Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = "No Photo",
                                    tint = TextWhite,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Profile Photo",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Tap to pick & crop with Precision Zoom",
                                color = Color(0xFF00FFA3),
                                fontSize = 11.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.PhotoCamera,
                            contentDescription = "Pick Photo",
                            tint = Color(0xFF00FFA3),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextWhite,
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
                        value = editGender,
                        onValueChange = { editGender = it },
                        label = { Text("Gender (Male / Female / Other)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextWhite,
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
                        value = editWeight,
                        onValueChange = { input ->
                            // Allow digits and at most one decimal point
                            if (input.count { it == '.' } <= 1 && input.all { it.isDigit() || it == '.' }) {
                                editWeight = input
                            }
                        },
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextWhite,
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
                        value = editHeight,
                        onValueChange = { input ->
                            if (input.count { it == '.' } <= 1 && input.all { it.isDigit() || it == '.' }) {
                                editHeight = input
                            }
                        },
                        label = { Text("Height (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextWhite,
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
                        value = editStepGoal,
                        onValueChange = { editStepGoal = it.filter { c -> c.isDigit() } },
                        label = { Text("Daily Step Target") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextWhite,
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
                        value = editWaterGoal,
                        onValueChange = { editWaterGoal = it.filter { c -> c.isDigit() } },
                        label = { Text("Daily Water Target (ml)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
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
                TextButton(
                    onClick = {
                        val weight = editWeight.toFloatOrNull() ?: 0f
                        val height = editHeight.toFloatOrNull() ?: 0f
                        val stepGoal = editStepGoal.toIntOrNull() ?: 10000
                        val waterGoal = editWaterGoal.toIntOrNull() ?: 3000

                        viewModel.saveUserProfile(
                            currentProfile.copy(
                                name = editName.ifBlank { "Athlete" },
                                gender = editGender.ifBlank { "Male" },
                                weightKg = weight,
                                heightCm = height,
                                stepGoal = stepGoal,
                                waterGoalMl = waterGoal
                            )
                        )
                        showEditProfileDialog = false
                    }
                ) {
                    Text("Save Changes", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    // Add Custom Goal Dialog
    if (showAddGoalDialog) {
        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Add Personal Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = goalTitleInput,
                        onValueChange = { goalTitleInput = it },
                        label = { Text("Goal Name (e.g. Bench 100kg)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextWhite,
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
                        value = goalTargetInput,
                        onValueChange = { goalTargetInput = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Target Value (e.g. 100)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextWhite,
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
                        value = goalUnitInput,
                        onValueChange = { goalUnitInput = it },
                        label = { Text("Unit (e.g. kg, reps, km)") },
                        colors = OutlinedTextFieldDefaults.colors(
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
                TextButton(
                    onClick = {
                        val target = goalTargetInput.toDoubleOrNull() ?: 0.0
                        if (goalTitleInput.isNotBlank() && target > 0.0) {
                            viewModel.addGoal(
                                GoalEntity(
                                    title = goalTitleInput.trim(),
                                    targetValue = target,
                                    unit = goalUnitInput.ifBlank { "units" }
                                )
                            )
                            goalTitleInput = ""
                            goalTargetInput = ""
                            showAddGoalDialog = false
                        }
                    }
                ) {
                    Text("Save Goal", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGoalDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    // Delete Active Goal Confirmation Dialog
    goalToDelete?.let { goal ->
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Delete Goal?", fontWeight = FontWeight.Bold) },
            text = { Text("Remove goal '${goal.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGoal(goal)
                        goalToDelete = null
                    }
                ) {
                    Text("Delete", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    // Delete Achieved Goal Confirmation Dialog
    achievedGoalToDelete?.let { goal ->
        AlertDialog(
            onDismissRequest = { achievedGoalToDelete = null },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Remove Achieved Milestone?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${goal.title}' from your achieved milestones?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGoal(goal)
                        achievedGoalToDelete = null
                    }
                ) {
                    Text("Remove", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { achievedGoalToDelete = null }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    // Reset Profile Dialog (Requires Confirmation)
    if (showResetProfileDialog) {
        AlertDialog(
            onDismissRequest = { showResetProfileDialog = false },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            icon = { Icon(imageVector = Icons.Rounded.Warning, contentDescription = null, tint = DangerRed) },
            title = { Text("Reset Profile Details?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure? This will reset your name, weight, height, and step target to defaults.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetUserProfile()
                        showResetProfileDialog = false
                    }
                ) {
                    Text("Confirm Profile Reset", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetProfileDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    // Erase All History Dialog (Requires Explicit Confirmation)
    if (showResetAllDataConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetAllDataConfirmDialog = false },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            icon = { Icon(imageVector = Icons.Rounded.Warning, contentDescription = null, tint = DangerRed) },
            title = { Text("Confirm Erase All Data?", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("This action CANNOT be undone.", color = DangerRed, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("All logged workouts, exercises, step history, personal records, active/achieved goals, and hydration logs will be erased from local phone storage.")
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .background(DangerRed, RoundedCornerShape(12.dp))
                        .clickable {
                            viewModel.resetAllData()
                            showResetAllDataConfirmDialog = false
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("CONFIRM DATA RESET", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAllDataConfirmDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
}

private fun saveCroppedProfileImage(
    context: android.content.Context,
    imageUri: Uri,
    scale: Float,
    offset: Offset,
    containerSizePx: Float
): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(imageUri) ?: return null
        val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
        inputStream.close()

        // Handle EXIF orientation
        val exifInputStream = context.contentResolver.openInputStream(imageUri)
        val orientation = if (exifInputStream != null) {
            val exif = ExifInterface(exifInputStream)
            val attr = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            exifInputStream.close()
            attr
        } else {
            ExifInterface.ORIENTATION_NORMAL
        }

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        val orientedBitmap = if (!matrix.isIdentity) {
            val rotated = Bitmap.createBitmap(
                originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true
            )
            originalBitmap.recycle()
            rotated
        } else {
            originalBitmap
        }

        val containerSize = containerSizePx.coerceAtLeast(1f)
        val srcW = orientedBitmap.width.toFloat()
        val srcH = orientedBitmap.height.toFloat()

        // Fit scale inside square container
        val fitScale = minOf(containerSize / srcW, containerSize / srcH)
        val dispW = srcW * fitScale
        val dispH = srcH * fitScale
        val startX = (containerSize - dispW) / 2f
        val startY = (containerSize - dispH) / 2f
        val cx = containerSize / 2f
        val cy = containerSize / 2f

        // Map container pixel space back to source bitmap coordinate space
        val effectiveScale = scale.coerceAtLeast(0.1f)
        val bmLeft = ((0f - cx - offset.x) / effectiveScale + cx - startX) / fitScale
        val bmTop = ((0f - cy - offset.y) / effectiveScale + cy - startY) / fitScale
        val bmWidth = (containerSize / effectiveScale) / fitScale
        val bmHeight = (containerSize / effectiveScale) / fitScale

        val cropX = bmLeft.toInt().coerceIn(0, (srcW - 1).toInt())
        val cropY = bmTop.toInt().coerceIn(0, (srcH - 1).toInt())
        val maxAvailableW = (srcW - cropX).toInt().coerceAtLeast(1)
        val maxAvailableH = (srcH - cropY).toInt().coerceAtLeast(1)
        val cropW = bmWidth.toInt().coerceIn(1, maxAvailableW)
        val cropH = bmHeight.toInt().coerceIn(1, maxAvailableH)
        val cropSize = minOf(cropW, cropH)

        val croppedBitmap = Bitmap.createBitmap(orientedBitmap, cropX, cropY, cropSize, cropSize)

        // Output in sharp high-definition (up to 1080x1080)
        val targetOutputSize = minOf(cropSize, 1080).coerceAtLeast(512)
        val finalBitmap = if (cropSize != targetOutputSize) {
            val scaled = Bitmap.createScaledBitmap(croppedBitmap, targetOutputSize, targetOutputSize, true)
            croppedBitmap.recycle()
            scaled
        } else {
            croppedBitmap
        }

        orientedBitmap.recycle()

        val avatarFile = java.io.File(
            context.filesDir,
            "athlete_crop_${System.currentTimeMillis()}.jpg"
        )
        val outputStream = java.io.FileOutputStream(avatarFile)
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 96, outputStream)
        outputStream.flush()
        outputStream.close()
        finalBitmap.recycle()

        Uri.fromFile(avatarFile)
    } catch (e: Exception) {
        e.printStackTrace()
        imageUri
    }
}