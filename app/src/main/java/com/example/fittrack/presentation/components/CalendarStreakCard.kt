package com.example.fittrack.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.data.local.entity.WorkoutEntity
import com.example.fittrack.presentation.theme.CardBorderActive
import com.example.fittrack.presentation.theme.CardBorderWhite
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextSilver
import com.example.fittrack.presentation.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarStreakCard(
    workouts: List<WorkoutEntity>,
    modifier: Modifier = Modifier
) {
    var calendarMonthOffset by remember { mutableStateOf(0) }

    val calendar = remember(calendarMonthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, calendarMonthOffset)
        }
    }

    val currentMonthName = remember(calendar) {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
    }

    val daysInMonth = remember(calendar) {
        calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val firstDayOfWeek = remember(calendar) {
        val tempCal = calendar.clone() as Calendar
        tempCal.set(Calendar.DAY_OF_MONTH, 1)
        var day = tempCal.get(Calendar.DAY_OF_WEEK) - 2
        if (day < 0) day += 7
        day
    }

    val todayCal = Calendar.getInstance()
    val isCurrentMonthDisplayed = calendarMonthOffset == 0

    val workoutDateSet = remember(workouts) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        workouts.filter { it.completed }.map {
            sdf.format(Date(it.date))
        }.toSet()
    }

    val monthSdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    val currentMonthKey = monthSdf.format(calendar.time)

    val streakCount = remember(workouts) {
        calculateCurrentStreak(workouts)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(24.dp))
            .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        // Header Row with Streak Badge & Proper Spacing
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
                        .border(1.dp, CardBorderActive.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DateRange,
                        contentDescription = "Calendar Streak",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Activity Streak",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Monthly Consistency",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .background(CardDarkElevated, RoundedCornerShape(14.dp))
                    .border(1.dp, CardBorderActive, RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Streak",
                        tint = TextWhite,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$streakCount Days Streak",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Month Selector Header (Allows Navigating Prev & Next freely)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { calendarMonthOffset-- },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDescription = "Prev Month",
                    tint = TextWhite
                )
            }

            AnimatedContent(
                targetState = currentMonthName,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "MonthTitle"
            ) { title ->
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = { if (calendarMonthOffset < 12) calendarMonthOffset++ },
                enabled = calendarMonthOffset < 12,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    tint = if (calendarMonthOffset < 12) TextWhite else TextGray.copy(alpha = 0.3f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Day of Week Headers (M, T, W, T, F, S, S)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(
                    text = day,
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Fully Responsive Calendar Days Grid
        val totalGridItems = firstDayOfWeek + daysInMonth
        val gridRows = (totalGridItems + 6) / 7

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (row in 0 until gridRows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (col in 0 until 7) {
                        val index = row * 7 + col
                        if (index < firstDayOfWeek || index >= totalGridItems) {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            val dayNum = index - firstDayOfWeek + 1
                            val dayKey = String.format(Locale.US, "%s-%02d", currentMonthKey, dayNum)
                            val isWorkoutDone = workoutDateSet.contains(dayKey)
                            val isToday = isCurrentMonthDisplayed && dayNum == todayCal.get(Calendar.DAY_OF_MONTH)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isWorkoutDone -> CardDarkElevated
                                            isToday -> CardDarkElevated
                                            else -> CardDark
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = when {
                                            isWorkoutDone -> TextWhite
                                            isToday -> CardBorderActive
                                            else -> CardBorderWhite.copy(alpha = 0.3f)
                                        },
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$dayNum",
                                        color = when {
                                            isWorkoutDone -> TextWhite
                                            isToday -> TextWhite
                                            else -> TextGray
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = if (isWorkoutDone || isToday) FontWeight.Bold else FontWeight.Normal
                                    )

                                    if (isWorkoutDone) {
                                        Box(
                                            modifier = Modifier
                                                .size(3.5.dp)
                                                .background(TextWhite, CircleShape)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun calculateCurrentStreak(workouts: List<WorkoutEntity>): Int {
    val completedWorkouts = workouts.filter { it.completed }
    if (completedWorkouts.isEmpty()) return 0

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dates = completedWorkouts.map {
        sdf.format(Date(it.date))
    }.distinct().sortedDescending()

    if (dates.isEmpty()) return 0

    val todayKey = sdf.format(Date())
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, -1)
    val yesterdayKey = sdf.format(cal.time)

    if (!dates.contains(todayKey) && !dates.contains(yesterdayKey)) {
        return 0
    }

    var streak = 0
    val checkCal = Calendar.getInstance()

    if (!dates.contains(todayKey)) {
        checkCal.add(Calendar.DAY_OF_YEAR, -1)
    }

    while (true) {
        val key = sdf.format(checkCal.time)
        if (dates.contains(key)) {
            streak++
            checkCal.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            break
        }
    }

    return streak
}
