package com.example.fittrack.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.theme.CardBorderWhite
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.DangerRed
import com.example.fittrack.presentation.theme.FlameOrange
import com.example.fittrack.presentation.theme.NeonCyan
import com.example.fittrack.presentation.theme.NeonTeal
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextSilver
import com.example.fittrack.presentation.theme.TextWhite
import java.util.Locale

@Composable
fun ActivityCard(
    steps: Int,
    calories: Int,
    stepGoal: Int = 10000,
    onUpdateStepGoal: ((Int) -> Unit)? = null
) {
    val rawProgress = if (stepGoal > 0) (steps.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 800),
        label = "progressAnimation"
    )

    var showEditGoalDialog by remember { mutableStateOf(false) }
    var editGoalText by remember { mutableStateOf(stepGoal.toString()) }
    var errorMessage by remember { mutableStateOf("") }

    val isGoalAchieved = steps >= stepGoal && stepGoal > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(24.dp))
            .border(
                1.dp,
                if (isGoalAchieved) NeonTeal.copy(alpha = 0.8f) else CardBorderWhite,
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
    ) {
        // 1. Top Header: Title on left, [Target: 10000 steps] button on right
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
                            if (isGoalAchieved) NeonTeal else NeonTeal.copy(alpha = 0.5f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.DirectionsWalk,
                        contentDescription = "Activity Steps",
                        tint = NeonTeal,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Daily Activity",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .background(NeonTeal.copy(alpha = 0.14f), RoundedCornerShape(12.dp))
                    .border(1.dp, NeonTeal.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                    .clickable {
                        editGoalText = stepGoal.toString()
                        errorMessage = ""
                        showEditGoalDialog = true
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit Step Goal",
                        tint = NeonTeal,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Target: $stepGoal",
                        color = NeonTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2. Full Width Subtitle: 100% readable with zero ellipsis
        Text(
            text = if (isGoalAchieved)
                "🎉 Daily Step Goal Achieved ($steps steps)!"
            else
                "Pedometer & Caloric Burn • ${maxOf(0, stepGoal - steps)} steps remaining",
            color = if (isGoalAchieved) NeonTeal else TextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.DirectionsWalk,
                    contentDescription = null,
                    tint = NeonTeal,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$steps / $stepGoal steps",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.LocalFireDepartment,
                    contentDescription = null,
                    tint = FlameOrange,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$calories kcal",
                    color = FlameOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Vibrant Gradient Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(CardDarkElevated, RoundedCornerShape(4.dp))
        ) {
            if (animatedProgress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(8.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(NeonTeal, NeonCyan)
                            ),
                            RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }

    // Set Daily Step Goal Modal Dialog
    if (showEditGoalDialog) {
        val goalPresets = listOf(5000, 8000, 10000, 12000, 15000, 20000)

        AlertDialog(
            onDismissRequest = { showEditGoalDialog = false },
            containerColor = CardDark,
            titleContentColor = TextWhite,
            textContentColor = TextSilver,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.DirectionsWalk,
                        contentDescription = null,
                        tint = NeonTeal,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set Daily Step Goal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Update your daily target step goal. Your recorded steps will not be modified:",
                        color = TextGray,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = editGoalText,
                        onValueChange = {
                            editGoalText = it.filter { c -> c.isDigit() }
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Daily Step Goal") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = NeonTeal,
                            unfocusedBorderColor = CardBorderWhite,
                            focusedLabelColor = NeonTeal
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Quick Goal Presets:", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(goalPresets) { preset ->
                            val isSelected = editGoalText == preset.toString()
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) NeonTeal.copy(alpha = 0.2f) else CardDarkElevated,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonTeal else CardBorderWhite,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        editGoalText = preset.toString()
                                        errorMessage = ""
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = String.format(Locale.US, "%,d", preset),
                                    color = if (isSelected) NeonTeal else TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
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
                        val parsed = editGoalText.toIntOrNull()
                        if (parsed == null || parsed <= 0) {
                            errorMessage = "Please enter a valid step goal (e.g. 10000)"
                        } else {
                            onUpdateStepGoal?.invoke(parsed)
                            showEditGoalDialog = false
                        }
                    }
                ) {
                    Text("Save Step Goal", color = NeonTeal, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditGoalDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
}