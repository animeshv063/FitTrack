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
import java.util.Locale

data class BarChartEntry(
    val label: String,
    val value: Float
)

@Composable
fun VolumeBarChart(
    entries: List<BarChartEntry>,
    modifier: Modifier = Modifier
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

    val maxValue = (entries.maxOfOrNull { it.value } ?: 100f).coerceAtLeast(10f)

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
            Column {
                Text(
                    text = "Training Volume",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Daily total load in kg",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .background(CardDarkElevated, RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorderActive, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "kg",
                    color = TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Canvas Bars
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

            // Grid lines
            val gridLines = 3
            for (i in 0..gridLines) {
                val y = canvasHeight * (i.toFloat() / gridLines)
                drawLine(
                    color = Color(0x15FFFFFF),
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
                    isHighest -> Brush.verticalGradient(colors = listOf(Color(0xFFFFFFFF), Color(0xFF9EA3AE)))
                    isZero -> Brush.verticalGradient(colors = listOf(Color(0x33FFFFFF), Color(0x15FFFFFF)))
                    else -> Brush.verticalGradient(colors = listOf(Color(0xFF8E95A5), Color(0xFF2F333D)))
                }

                drawRoundRect(
                    brush = barBrush,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, currentHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Days of week and volume labels (Defaulting to 0 kg when 0)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            entries.forEach { entry ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (entry.value > 0f) "${entry.value.toInt()}kg" else "0 kg",
                        color = if (entry.value > 0f) TextWhite else TextGray,
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
        modifier = modifier.size(170.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(170.dp)) {
            val strokeWidth = 14.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val radius = diameter / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Background Ring
            drawCircle(
                color = Color(0x1FFFFFFF),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Progress Arc
            val sweepAngle = animProgress * 360f
            if (sweepAngle > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF8E95A5),
                            Color(0xFFFFFFFF),
                            Color(0xFFD1D5DB),
                            Color(0xFF8E95A5)
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
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = centerSubtitle,
                color = TextGray,
                fontSize = 12.sp
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

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = muscle, color = TextSilver, fontSize = 14.sp)
                        Text(text = "$percentage%", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color(0x1FFFFFFF), RoundedCornerShape(4.dp))
                    ) {
                        if (animatedWidth > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedWidth)
                                    .height(8.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(Color(0xFFFFFFFF), Color(0xFF8E95A5))
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
