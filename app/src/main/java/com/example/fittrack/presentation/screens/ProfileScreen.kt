package com.example.fittrack.presentation.screens

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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.fittrack.data.local.entity.GoalEntity
import com.example.fittrack.data.local.entity.UserProfileEntity
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
    val waterLogs by viewModel.waterLogs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startStepCounter()
    }

    val completedWorkouts = workouts.count { it.completed }
    val totalVolume = exercises.sumOf { it.sets * it.reps * it.weight }
    val currentProfile = userProfile ?: UserProfileEntity()
    val dynamicTitle = viewModel.getAthleteTitle(completedWorkouts)

    val stepProgress = (steps.toFloat() / currentProfile.stepGoal.toFloat()).coerceIn(0f, 1f)

    val weightDisplay = if (currentProfile.weightKg > 0f) "${currentProfile.weightKg} kg" else "--"
    val heightDisplay = if (currentProfile.heightCm > 0f) "${currentProfile.heightCm.toInt()} cm" else "--"

    val heightM = currentProfile.heightCm / 100f
    val bmiDisplay = if (currentProfile.weightKg > 0f && heightM > 0f) {
        String.format("%.1f", currentProfile.weightKg / (heightM * heightM))
    } else {
        "--"
    }

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showCircularCropDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            tempPhotoUri = it
            showCircularCropDialog = true
        }
    }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(currentProfile.name) }
    var editGender by remember { mutableStateOf(currentProfile.gender) }
    var editWeight by remember { mutableStateOf(if (currentProfile.weightKg > 0f) currentProfile.weightKg.toString() else "") }
    var editHeight by remember { mutableStateOf(if (currentProfile.heightCm > 0f) currentProfile.heightCm.toString() else "") }
    var editStepGoal by remember { mutableStateOf(currentProfile.stepGoal.toString()) }

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var goalTitleInput by remember { mutableStateOf("") }
    var goalTargetInput by remember { mutableStateOf("") }
    var goalUnitInput by remember { mutableStateOf("kg") }
    var goalToDelete by remember { mutableStateOf<GoalEntity?>(null) }

    var showResetProfileDialog by remember { mutableStateOf(false) }
    var showResetAllDataDialog by remember { mutableStateOf(false) }

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
                Text(text = "Athlete Profile", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Personal targets & body stats", color = TextGray, fontSize = 13.sp)

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
                            .size(90.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentProfile.profileImageUri != null) {
                            AsyncImage(
                                model = Uri.parse(currentProfile.profileImageUri),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(86.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, TextWhite, CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(86.dp)
                                    .background(CardDarkElevated, CircleShape)
                                    .border(2.dp, CardBorderActive, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = "Avatar",
                                    tint = TextWhite,
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.BottomEnd)
                                .background(TextWhite, CircleShape)
                                .border(1.5.dp, CardDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change photo",
                                tint = CardDark,
                                modifier = Modifier.size(14.dp)
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
                            showEditProfileDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Overview 2x2 Grid
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

                // Unique Athlete Fitness Trophies Showcase Card
                val personalRecords by viewModel.personalRecords.collectAsState()

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
                                    .border(1.dp, CardBorderActive.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.EmojiEvents,
                                    contentDescription = "Trophy Showcase",
                                    tint = TextWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Athlete Mastery Trophies", color = TextWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val isMonarchUnlocked = completedWorkouts >= 5
                    val isTitanUnlocked = totalVolume >= 10000
                    val isPrUnlocked = personalRecords.isNotEmpty()

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardDarkElevated, RoundedCornerShape(16.dp))
                                .border(1.dp, if (isMonarchUnlocked) CardBorderActive else CardBorderWhite, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "🏆 Consistency Monarch", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Complete 5 or more workouts ($completedWorkouts/5)", color = TextGray, fontSize = 11.sp)
                            }
                            Text(
                                text = if (isMonarchUnlocked) "UNLOCKED" else "LOCKED",
                                color = if (isMonarchUnlocked) TextWhite else TextGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardDarkElevated, RoundedCornerShape(16.dp))
                                .border(1.dp, if (isTitanUnlocked) CardBorderActive else CardBorderWhite, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "⚡ Volume Titan", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Lift 10,000+ kg total volume (${totalVolume}kg)", color = TextGray, fontSize = 11.sp)
                            }
                            Text(
                                text = if (isTitanUnlocked) "UNLOCKED" else "LOCKED",
                                color = if (isTitanUnlocked) TextWhite else TextGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardDarkElevated, RoundedCornerShape(16.dp))
                                .border(1.dp, if (isPrUnlocked) CardBorderActive else CardBorderWhite, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "🎯 PR Legend", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Log at least 1 Personal Record (${personalRecords.size} PRs)", color = TextGray, fontSize = 11.sp)
                            }
                            Text(
                                text = if (isPrUnlocked) "UNLOCKED" else "LOCKED",
                                color = if (isPrUnlocked) TextWhite else TextGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Custom Personal Goals Card (Clean Horizontal Layout)
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
                                text = "Custom Goals",
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

                    if (goals.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No custom goals set yet. Tap '+ Add Goal' to set your targets.", color = TextGray, fontSize = 13.sp)
                        }
                    } else {
                        goals.forEach { goal ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(CardDarkElevated, RoundedCornerShape(16.dp))
                                    .border(1.dp, CardBorderWhite, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = goal.title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "Target: ${goal.targetValue} ${goal.unit}", color = TextGray, fontSize = 12.sp)
                                }

                                IconButton(
                                    onClick = { goalToDelete = goal },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Goal",
                                        tint = DangerRed.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Account & Reset Actions Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Text(text = "Account & Data Controls", color = TextWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(14.dp))

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
                            Text(text = "Reset Profile Info", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(CardDarkElevated, RoundedCornerShape(14.dp))
                                .border(1.dp, DangerRed.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                                .clickable { showResetAllDataDialog = true }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Erase All History", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // WhatsApp-Style Circular Photo Crop Selector Modal
    if (showCircularCropDialog && tempPhotoUri != null) {
        Dialog(onDismissRequest = { showCircularCropDialog = false }) {
            var scale by remember { mutableStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardDark, RoundedCornerShape(24.dp))
                    .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Crop Profile Picture",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Drag & zoom photo inside the circular stencil",
                    color = TextGray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Circular Canvas Mask Area
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black)
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(0.5f, 4f)
                                offset = Offset(offset.x + pan.x, offset.y + pan.y)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = tempPhotoUri,
                        contentDescription = "Crop Photo",
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

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val circleRadius = size.minDimension * 0.45f
                        val circleCenter = center

                        val path = Path().apply {
                            addRect(Rect(0f, 0f, size.width, size.height))
                            addOval(
                                Rect(
                                    circleCenter.x - circleRadius,
                                    circleCenter.y - circleRadius,
                                    circleCenter.x + circleRadius,
                                    circleCenter.y + circleRadius
                                )
                            )
                            fillType = PathFillType.EvenOdd
                        }
                        drawPath(path, color = Color.Black.copy(alpha = 0.65f))

                        drawCircle(
                            color = Color.White,
                            radius = circleRadius,
                            center = circleCenter,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(CardDarkElevated, CircleShape)
                            .border(1.dp, CardBorderWhite, CircleShape)
                            .clickable { scale = (scale - 0.2f).coerceAtLeast(0.5f) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("- Zoom Out", color = TextSilver, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier
                            .background(CardDarkElevated, CircleShape)
                            .border(1.dp, CardBorderWhite, CircleShape)
                            .clickable {
                                scale = 1f
                                offset = Offset.Zero
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Reset", color = TextSilver, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier
                            .background(CardDarkElevated, CircleShape)
                            .border(1.dp, CardBorderWhite, CircleShape)
                            .clickable { scale = (scale + 0.2f).coerceAtMost(4f) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("+ Zoom In", color = TextSilver, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { showCircularCropDialog = false }) {
                        Text("Cancel", color = TextGray)
                    }

                    Box(
                        modifier = Modifier
                            .background(TextWhite, RoundedCornerShape(14.dp))
                            .clickable {
                                tempPhotoUri?.let { uri ->
                                    val croppedUri = saveCroppedProfileImage(context, uri, scale, offset) ?: uri
                                    viewModel.saveUserProfile(
                                        currentProfile.copy(profileImageUri = croppedUri.toString())
                                    )
                                }
                                showCircularCropDialog = false
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text("Set Profile Picture", color = CardDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
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
                        value = editGender,
                        onValueChange = { editGender = it },
                        label = { Text("Gender (Male / Female / Other)") },
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
                        value = editWeight,
                        onValueChange = { editWeight = it },
                        label = { Text("Weight (kg)") },
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
                        value = editHeight,
                        onValueChange = { editHeight = it },
                        label = { Text("Height (cm)") },
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
                        value = editStepGoal,
                        onValueChange = { editStepGoal = it },
                        label = { Text("Daily Step Target") },
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
                        val weight = editWeight.toFloatOrNull() ?: 0f
                        val height = editHeight.toFloatOrNull() ?: 0f
                        val stepGoal = editStepGoal.toIntOrNull() ?: 10000

                        viewModel.saveUserProfile(
                            currentProfile.copy(
                                name = editName.ifBlank { "Athlete" },
                                gender = editGender.ifBlank { "Male" },
                                weightKg = weight,
                                heightCm = height,
                                stepGoal = stepGoal
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
                        value = goalTargetInput,
                        onValueChange = { goalTargetInput = it },
                        label = { Text("Target Value (e.g. 100)") },
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
                        value = goalUnitInput,
                        onValueChange = { goalUnitInput = it },
                        label = { Text("Unit (e.g. kg, reps, km)") },
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
                        val target = goalTargetInput.toDoubleOrNull() ?: 0.0
                        if (goalTitleInput.isNotBlank() && target > 0.0) {
                            viewModel.addGoal(
                                GoalEntity(
                                    title = goalTitleInput,
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

    // Delete Goal Dialog
    goalToDelete?.let { goal ->
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Delete Goal?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${goal.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGoal(goal)
                        goalToDelete = null
                    }
                ) {
                    Text("Delete", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    // Reset Profile Dialog
    if (showResetProfileDialog) {
        AlertDialog(
            onDismissRequest = { showResetProfileDialog = false },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Reset Profile Details?", fontWeight = FontWeight.Bold) },
            text = { Text("Reset name, weight, height, and step goal back to default values?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetUserProfile()
                        showResetProfileDialog = false
                    }
                ) {
                    Text("Reset Profile", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetProfileDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    // Reset All Data Dialog
    if (showResetAllDataDialog) {
        AlertDialog(
            onDismissRequest = { showResetAllDataDialog = false },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = { Text("Reset All Fitness History?", fontWeight = FontWeight.Bold) },
            text = { Text("This will erase all logged workouts, exercises, goals, PRs, and hydration data from phone memory.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllData()
                        showResetAllDataDialog = false
                    }
                ) {
                    Text("Reset Everything", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAllDataDialog = false }) {
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
    containerSizePx: Float = 240f
): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(imageUri) ?: return null
        val originalBitmap = android.graphics.BitmapFactory.decodeStream(inputStream) ?: return null

        val outputSize = 400
        val croppedBitmap = android.graphics.Bitmap.createBitmap(outputSize, outputSize, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(croppedBitmap)

        val srcW = originalBitmap.width.toFloat()
        val srcH = originalBitmap.height.toFloat()

        val baseScale = Math.max(outputSize / srcW, outputSize / srcH)
        val totalScale = baseScale * scale

        val normalizedDx = (offset.x / containerSizePx) * outputSize
        val normalizedDy = (offset.y / containerSizePx) * outputSize

        val tx = (outputSize - srcW * totalScale) / 2f + normalizedDx
        val ty = (outputSize - srcH * totalScale) / 2f + normalizedDy

        val matrix = android.graphics.Matrix()
        matrix.postScale(totalScale, totalScale)
        matrix.postTranslate(tx, ty)

        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(originalBitmap, matrix, paint)

        val avatarFile = java.io.File(context.filesDir, "cropped_avatar.jpg")
        val outputStream = java.io.FileOutputStream(avatarFile)
        croppedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, outputStream)
        outputStream.flush()
        outputStream.close()

        Uri.fromFile(avatarFile)
    } catch (e: Exception) {
        e.printStackTrace()
        imageUri
    }
}