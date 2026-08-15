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
    onUpdateSteps: ((Int) -> Unit)? = null
) {
    val rawProgress = (steps.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 800),
        label = "progressAnimation"
    )

    var showEditStepsDialog by remember { mutableStateOf(false) }
    var editStepText by remember { mutableStateOf(steps.toString()) }
    var errorMessage by remember { mutableStateOf("") }

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
                        .size(38.dp)
                        .background(CardDarkElevated, CircleShape)
                        .border(1.dp, NeonTeal.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.DirectionsWalk,
                        contentDescription = "Activity Steps",
                        tint = NeonTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Daily Activity",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "Pedometer & Caloric Burn",
                        color = TextGray,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onUpdateSteps != null) {
                    Box(
                        modifier = Modifier
                            .background(CardDarkElevated, RoundedCornerShape(12.dp))
                            .border(1.dp, CardBorderWhite, RoundedCornerShape(12.dp))
                            .clickable {
                                editStepText = steps.toString()
                                errorMessage = ""
                                showEditStepsDialog = true
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "Edit Steps",
                                tint = NeonTeal,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Edit",
                                color = NeonTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Box(
                    modifier = Modifier
                        .background(NeonTeal.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .border(1.dp, NeonTeal.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}% Goal",
                        color = NeonTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    fontSize = 15.sp,
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

    // Edit Steps Modal Dialog
    if (showEditStepsDialog) {
        val quickIncrements = listOf(500, 1000, 2500, 5000)
        val currentParsed = editStepText.toIntOrNull() ?: 0
        val estCalories = (currentParsed * 0.04).toInt()
        val estDistanceKm = currentParsed * 0.00075

        AlertDialog(
            onDismissRequest = { showEditStepsDialog = false },
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
                    Text("Adjust Today's Steps", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Manually enter or adjust your recorded steps for today:",
                        color = TextGray,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editStepText,
                        onValueChange = {
                            editStepText = it.filter { c -> c.isDigit() }
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Step Count") },
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

                    Text("Quick Adjustments:", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(quickIncrements) { delta ->
                            Box(
                                modifier = Modifier
                                    .background(CardDarkElevated, RoundedCornerShape(10.dp))
                                    .border(1.dp, CardBorderWhite, RoundedCornerShape(10.dp))
                                    .clickable {
                                        val cur = editStepText.toIntOrNull() ?: 0
                                        editStepText = (cur + delta).toString()
                                        errorMessage = ""
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(text = "+$delta", color = NeonTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        item {
                            Box(
                                modifier = Modifier
                                    .background(CardDarkElevated, RoundedCornerShape(10.dp))
                                    .border(1.dp, DangerRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .clickable {
                                        editStepText = "0"
                                        errorMessage = ""
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(text = "Reset (0)", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Stats Preview Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDarkElevated, RoundedCornerShape(12.dp))
                            .border(1.dp, CardBorderWhite.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Est. Calories", color = TextGray, fontSize = 11.sp)
                            Text("$estCalories kcal", color = FlameOrange, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Est. Distance", color = TextGray, fontSize = 11.sp)
                            Text(
                                String.format(Locale.US, "%.2f km", estDistanceKm),
                                color = NeonCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("Goal Progress", color = TextGray, fontSize = 11.sp)
                            val pct = ((currentParsed.toFloat() / stepGoal.toFloat()) * 100).toInt()
                            Text("$pct%", color = NeonTeal, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                        val parsed = editStepText.toIntOrNull()
                        if (parsed == null || parsed < 0) {
                            errorMessage = "Please enter a valid step number"
                        } else {
                            onUpdateSteps?.invoke(parsed)
                            showEditStepsDialog = false
                        }
                    }
                ) {
                    Text("Save Steps", color = NeonTeal, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditStepsDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }
}