package com.example.fittrack.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.data.local.entity.WorkoutEntity
import com.example.fittrack.presentation.theme.CardBorderActive
import com.example.fittrack.presentation.theme.CardBorderWhite
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.NeonCyan
import com.example.fittrack.presentation.theme.NeonTeal
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
    modifier: Modifier = Modifier,
    getStepsForDate: (String) -> Int = { 0 },
    todaySteps: Int = 0
) {
    var calendarMonthOffset by remember { mutableStateOf(0) }
    var selectedDateKey by remember { mutableStateOf<String?>(null) }

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

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val displayDateSdf = remember { SimpleDateFormat("EEEE, MMM d", Locale.getDefault()) }
    val todayKey = remember { sdf.format(Date()) }

    val workoutDateSet = remember(workouts) {
        workouts.filter { it.completed }.map {
            sdf.format(Date(it.date))
        }.toSet()
    }

    val monthSdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    val currentMonthKey = monthSdf.format(calendar.time)

    val streakCount = remember(workouts) {
        calculateCurrentStreak(workouts)
    }

    // Workouts for selected inspected date
    val selectedDayWorkouts = remember(workouts, selectedDateKey) {
        if (selectedDateKey == null) emptyList()
        else workouts.filter { it.completed && sdf.format(Date(it.date)) == selectedDateKey }
    }

    val selectedDaySteps = remember(selectedDateKey, todaySteps) {
        if (selectedDateKey == null) 0
        else if (selectedDateKey == todayKey) todaySteps
        else getStepsForDate(selectedDateKey!!)
    }

    val selectedDayFormattedText = remember(selectedDateKey) {
        if (selectedDateKey == null) ""
        else {
            try {
                val parsed = sdf.parse(selectedDateKey!!)
                if (parsed != null) displayDateSdf.format(parsed) else selectedDateKey!!
            } catch (e: Exception) {
                selectedDateKey!!
            }
        }
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
                        text = "Tap any date to inspect training & steps",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .background(Color(0xFFFBBF24).copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFFFBBF24).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Streak",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$streakCount Days Streak",
                        color = Color(0xFFFBBF24),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Month Selector Header
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
                            val isSelected = selectedDateKey == dayKey

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> NeonCyan.copy(alpha = 0.25f)
                                            isWorkoutDone -> NeonTeal.copy(alpha = 0.14f)
                                            isToday -> Color(0xFF00F2FE).copy(alpha = 0.12f)
                                            else -> CardDark
                                        }
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = when {
                                            isSelected -> NeonCyan
                                            isWorkoutDone -> NeonTeal.copy(alpha = 0.8f)
                                            isToday -> Color(0xFF00F2FE)
                                            else -> CardBorderWhite.copy(alpha = 0.25f)
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedDateKey = if (selectedDateKey == dayKey) null else dayKey
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$dayNum",
                                        color = when {
                                            isSelected -> NeonCyan
                                            isWorkoutDone -> NeonTeal
                                            isToday -> Color(0xFF00F2FE)
                                            else -> TextGray
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = if (isWorkoutDone || isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                                    )

                                    if (isWorkoutDone) {
                                        Box(
                                            modifier = Modifier
                                                .size(3.5.dp)
                                                .background(if (isSelected) NeonCyan else NeonTeal, CircleShape)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Date Training & Steps Details Panel
        AnimatedVisibility(visible = selectedDateKey != null) {
            val totalMins = selectedDayWorkouts.sumOf { it.duration }
            val estCalories = (selectedDaySteps * 0.04).toInt()
            val estDistanceKm = selectedDaySteps * 0.00075
            val isTodaySelected = selectedDateKey == todayKey

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .background(CardDarkElevated, RoundedCornerShape(18.dp))
                    .border(1.dp, if (selectedDayWorkouts.isNotEmpty() || selectedDaySteps > 0) NeonTeal.copy(alpha = 0.45f) else CardBorderWhite, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                // Header Date and Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedDayFormattedText,
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isTodaySelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(NeonCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "TODAY",
                                    color = NeonCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = if (selectedDayWorkouts.isNotEmpty()) "Workout Done" else "Rest Day",
                        color = if (selectedDayWorkouts.isNotEmpty()) NeonTeal else TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Side-by-Side Summary Cards: Time Trained & Steps Recorded
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Time Trained Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(CardDark, RoundedCornerShape(14.dp))
                            .border(1.dp, CardBorderWhite.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Timer,
                                    contentDescription = "Time Trained",
                                    tint = NeonTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Time Trained",
                                    color = TextGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (totalMins > 0) "$totalMins mins" else "0 mins",
                                color = if (totalMins > 0) NeonTeal else TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (selectedDayWorkouts.isNotEmpty()) "${selectedDayWorkouts.size} workout${if (selectedDayWorkouts.size > 1) "s" else ""}" else "Rest day",
                                color = TextSilver,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Steps Recorded Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(CardDark, RoundedCornerShape(14.dp))
                            .border(1.dp, CardBorderWhite.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.DirectionsWalk,
                                    contentDescription = "Steps Logged",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Steps Logged",
                                    color = TextGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format(Locale.US, "%,d", selectedDaySteps),
                                color = NeonCyan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "~$estCalories kcal • ${String.format(Locale.US, "%.2f", estDistanceKm)} km",
                                color = TextSilver,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Workouts List (if any completed)
                if (selectedDayWorkouts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Completed Sessions",
                        color = TextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    selectedDayWorkouts.forEach { w ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .background(CardDark.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.FitnessCenter,
                                    contentDescription = null,
                                    tint = NeonTeal,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = w.name,
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${w.duration} min",
                                    color = TextSilver,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = NeonTeal,
                                    modifier = Modifier.size(13.dp)
                                )
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
