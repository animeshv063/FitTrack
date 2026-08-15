package com.example.fittrack.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.theme.CardBorderActive
import com.example.fittrack.presentation.theme.CardBorderWhite
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextSilver
import com.example.fittrack.presentation.theme.TextWhite
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import java.util.Locale

data class BarChartEntry(
    val label: String,
    val value: Float
)

@Composable
fun VolumeBarChart(
    entries: List<BarChartEntry>,
    modifier: Modifier = Modifier,
    dailyCapacityScale: Float = 3500f,
    onOpenScaleDialog: (() -> Unit)? = null
) {
    var animateChart by remember { mutableStateOf(false) }

    LaunchedEffect(entries) {
        animateChart = true
    }

    val animProgress by animateFloatAsState(
        targetValue = if (animateChart) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "barAnimation"
    )

    // Compute chart ceiling: uses user-configured medium-high capacity baseline, or automatically expands if session exceeds it
    val maxValue = maxOf(dailyCapacityScale, entries.maxOfOrNull { it.value } ?: 0f).coerceAtLeast(500f)

    Column(
        modifier = modifier
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Training Volume",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Daily load (Max scale: ${String.format(Locale.US, "%,.0f", maxValue)} kg)",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .background(Color(0xFF00F2FE).copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF00F2FE).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable { onOpenScaleDialog?.invoke() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = "Adjust Chart Scale",
                        tint = Color(0xFF00F2FE),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Scale",
                        color = Color(0xFF00F2FE),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Canvas Bars with Electric Neon Gradients
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barCount = entries.size.coerceAtLeast(1)
            val barSpacing = 14.dp.toPx()
            val totalSpacing = barSpacing * (barCount + 1)
            val barWidth = ((canvasWidth - totalSpacing) / barCount).coerceAtLeast(10.dp.toPx())

            // Subtle Grid lines
            val gridLines = 3
            for (i in 0..gridLines) {
                val y = canvasHeight * (i.toFloat() / gridLines)
                drawLine(
                    color = Color(0x1800F2FE),
                    start = Offset(0f, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            entries.forEachIndexed { index, entry ->
                val x = barSpacing + index * (barWidth + barSpacing)
                val isZero = entry.value <= 0f
                val barVal = if (isZero) 3.dp.toPx() else (entry.value / maxValue) * (canvasHeight * 0.85f)
                val currentHeight = barVal * animProgress
                val y = canvasHeight - currentHeight

                val isHighest = entry.value == maxValue && entry.value > 0f

                val barBrush = when {
                    isHighest -> Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF00FFA3),
                            Color(0xFF00F2FE)
                        )
                    )
                    isZero -> Brush.verticalGradient(
                        colors = listOf(
                            Color(0x3300F2FE),
                            Color(0x1000F2FE)
                        )
                    )
                    else -> Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF00F2FE),
                            Color(0xFF1E1B4B)
                        )
                    )
                }

                drawRoundRect(
                    brush = barBrush,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, currentHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Days of week and volume labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            entries.forEach { entry ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (entry.value > 0f) "${entry.value.toInt()}kg" else "0 kg",
                        color = if (entry.value > 0f) Color(0xFF00FFA3) else TextGray,
                        fontSize = 10.sp,
                        fontWeight = if (entry.value > 0f) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = entry.label,
                        color = TextSilver,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun RadialProgressDonut(
    progress: Float,
    centerTitle: String,
    centerSubtitle: String,
    modifier: Modifier = Modifier
) {
    val animProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "donutAnimation"
    )

    Box(
        modifier = modifier.size(175.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(175.dp)) {
            val strokeWidth = 15.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val radius = diameter / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Background Track Ring
            drawCircle(
                color = Color(0x1A00F2FE),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Radiant Multi-Color Progress Arc
            val sweepAngle = animProgress * 360f
            if (sweepAngle > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF00FFA3), // Neon Lime
                            Color(0xFF00F2FE), // Electric Cyan
                            Color(0xFFA855F7), // Electric Purple
                            Color(0xFFFF6B00), // Flame Orange
                            Color(0xFF00FFA3)
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerTitle,
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = centerSubtitle,
                color = Color(0xFF00F2FE),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MuscleBreakdownChart(
    breakdown: Map<String, Float>,
    modifier: Modifier = Modifier
) {
    val total = breakdown.values.sum()
    val hasData = total > 0f

    val muscleColorPalette = mapOf(
        "Chest" to Color(0xFF00F2FE),       // Electric Cyan
        "Back" to Color(0xFFA855F7),        // Electric Purple
        "Legs" to Color(0xFF00FFA3),        // Neon Mint
        "Shoulders" to Color(0xFFFF6B00),   // Flame Orange
        "Arms" to Color(0xFFFBBF24),        // Golden Amber
        "Core" to Color(0xFFEC4899),        // Hot Pink
        "Cardio" to Color(0xFF38BDF8)       // Sky Blue
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(24.dp))
            .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "Muscle Group Split",
            color = TextWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!hasData) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add exercises to view your muscle group split",
                    color = TextGray,
                    fontSize = 13.sp
                )
            }
        } else {
            breakdown.forEach { (muscle, volume) ->
                val percentage = if (total > 0f) ((volume / total) * 100).toInt() else 0
                val animatedWidth by animateFloatAsState(
                    targetValue = if (total > 0f) volume / total else 0f,
                    animationSpec = tween(durationMillis = 1000),
                    label = "barWidth"
                )

                val accentColor = muscleColorPalette[muscle] ?: Color(0xFF00F2FE)

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(accentColor, RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = muscle, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(text = "$percentage%", color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(CardDarkElevated, RoundedCornerShape(4.dp))
                    ) {
                        if (animatedWidth > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedWidth)
                                    .height(8.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                accentColor,
                                                accentColor.copy(alpha = 0.6f)
                                            )
                                        ),
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
