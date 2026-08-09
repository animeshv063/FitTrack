package com.example.fittrack.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.data.local.entity.ExerciseEntity
import com.example.fittrack.data.notification.NotificationHelper
import com.example.fittrack.presentation.theme.PrimaryGreen
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextWhite
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel
import kotlinx.coroutines.delay

@Composable
fun WorkoutDetailScreen(
    workoutId: Int,
    viewModel: WorkoutViewModel
) {

    val exercises by viewModel
        .getExercises(workoutId)
        .collectAsState(
            initial = emptyList()
        )

    var name by remember {
        mutableStateOf("")
    }

    var sets by remember {
        mutableStateOf("")
    }

    var reps by remember {
        mutableStateOf("")
    }

    var weight by remember {
        mutableStateOf("")
    }

    var workoutStarted by remember {
        mutableStateOf(false)
    }

    var restTime by remember {
        mutableStateOf(0)
    }

    var timerRunning by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var exerciseToDelete by remember {
        mutableStateOf<ExerciseEntity?>(null)
    }

    val context = LocalContext.current

    val notificationHelper = remember {
        NotificationHelper(context)
    }

    /*
     * REST TIMER
     */

    LaunchedEffect(timerRunning) {

        while (
            timerRunning &&
            restTime > 0
        ) {

            delay(1000)

            restTime--

            if (restTime == 0) {

                timerRunning = false

                notificationHelper
                    .showRestFinishedNotification()
            }
        }
    }

    /*
     * MAIN UI
     */

    LazyColumn(
        modifier = Modifier
            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        item {

            Text(
                text = "Workout",
                color = TextWhite,
                fontSize = 28.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    if (workoutStarted)
                        "Workout in progress"
                    else
                        "Ready to train",
                color =
                    if (workoutStarted)
                        PrimaryGreen
                    else
                        TextGray
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            /*
             * EXERCISE INPUT
             */

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = ""
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Exercise")
                },
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = sets,
                onValueChange = {
                    sets = it.filter { char ->
                        char.isDigit()
                    }
                    errorMessage = ""
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Sets")
                },
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = reps,
                onValueChange = {
                    reps = it.filter { char ->
                        char.isDigit()
                    }
                    errorMessage = ""
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Reps")
                },
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = weight,
                onValueChange = {
                    weight = it.filter { char ->
                        char.isDigit()
                    }
                    errorMessage = ""
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Weight (kg)")
                },
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            if (errorMessage.isNotEmpty()) {

                Text(
                    text = errorMessage,
                    color = androidx.compose.ui.graphics.Color.Red
                )
            }

            Button(
                onClick = {

                    val setsValue =
                        sets.toIntOrNull()

                    val repsValue =
                        reps.toIntOrNull()

                    val weightValue =
                        weight.toIntOrNull()

                    when {

                        name.isBlank() -> {
                            errorMessage =
                                "Enter an exercise name"
                        }

                        setsValue == null ||
                                setsValue <= 0 -> {
                            errorMessage =
                                "Sets must be at least 1"
                        }

                        repsValue == null ||
                                repsValue <= 0 -> {
                            errorMessage =
                                "Reps must be at least 1"
                        }

                        weightValue == null ||
                                weightValue < 0 -> {
                            errorMessage =
                                "Enter a valid weight"
                        }

                        else -> {

                            viewModel.addExercise(
                                ExerciseEntity(
                                    workoutId = workoutId,
                                    name = name.trim(),
                                    sets = setsValue,
                                    reps = repsValue,
                                    weight = weightValue
                                )
                            )

                            name = ""
                            sets = ""
                            reps = ""
                            weight = ""
                            errorMessage = ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Add Exercise")
            }

            /*
             * START / FINISH
             */

            Button(
                onClick = {

                    if (workoutStarted) {

                        viewModel.completeWorkout(
                            workoutId
                        )

                        workoutStarted = false

                    } else {

                        workoutStarted = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        if (workoutStarted)
                            "Finish Workout"
                        else
                            "Start Workout"
                )
            }

            /*
             * REST TIMER
             */

            if (timerRunning) {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "REST",
                            color = TextGray
                        )

                        Text(
                            text =
                                formatTime(restTime),
                            color = PrimaryGreen,
                            fontSize = 36.sp
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Button(
                                onClick = {

                                    restTime += 30
                                }
                            ) {

                                Text("+30")
                            }

                            Button(
                                onClick = {

                                    timerRunning = false
                                    restTime = 0
                                }
                            ) {

                                Text("Skip")
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "Exercises",
                color = TextWhite,
                fontSize = 22.sp
            )
        }

        /*
         * EXERCISES
         */

        items(
            items = exercises,
            key = {
                it.id
            }
        ) { exercise ->

            ExerciseCard(
                exercise = exercise,
                workoutStarted = workoutStarted,
                onDelete = {
                    exerciseToDelete = exercise
                },
                onSetChanged = { completedSets ->

                    viewModel.updateCompletedSets(
                        exercise.id,
                        completedSets
                    )

                    if (
                        completedSets > 0 &&
                        completedSets <= exercise.sets
                    ) {

                        restTime = 90
                        timerRunning = true
                    }
                }
            )
        }
    }

    /*
     * DELETE EXERCISE DIALOG
     */

    exerciseToDelete?.let { exercise ->

        AlertDialog(
            onDismissRequest = {
                exerciseToDelete = null
            },
            title = {
                Text("Delete Exercise?")
            },
            text = {
                Text(
                    "Remove ${exercise.name} from this workout?"
                )
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        viewModel.deleteExercise(
                            exercise
                        )

                        exerciseToDelete = null
                    }
                ) {

                    Text(
                        text = "Delete",
                        color = androidx.compose.ui.graphics.Color.Red
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        exerciseToDelete = null
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
private fun ExerciseCard(
    exercise: ExerciseEntity,
    workoutStarted: Boolean,
    onDelete: () -> Unit,
    onSetChanged: (Int) -> Unit
) {

    var completedSets by remember(
        exercise.id,
        exercise.completedSets
    ) {
        mutableStateOf(
            exercise.completedSets
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = exercise.name,
                        color = TextWhite,
                        fontSize = 20.sp
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "${exercise.sets} sets × ${exercise.reps} reps",
                        color = TextGray
                    )

                    Text(
                        text =
                            "${exercise.weight} kg",
                        color = PrimaryGreen
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Delete,
                        contentDescription =
                            "Delete exercise"
                    )
                }
            }

            if (workoutStarted) {

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                repeat(
                    exercise.sets
                ) { index ->

                    val isCompleted =
                        index < completedSets

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Checkbox(
                            checked = isCompleted,
                            onCheckedChange = {

                                completedSets =
                                    if (it) {

                                        (completedSets + 1)
                                            .coerceAtMost(
                                                exercise.sets
                                            )

                                    } else {

                                        (completedSets - 1)
                                            .coerceAtLeast(0)
                                    }

                                onSetChanged(
                                    completedSets
                                )
                            }
                        )

                        Text(
                            text =
                                "Set ${index + 1} completed",
                            modifier = Modifier
                                .padding(top = 12.dp)
                        )
                    }
                }

                Text(
                    text =
                        "$completedSets / ${exercise.sets} sets completed",
                    color = PrimaryGreen
                )
            }
        }
    }
}


private fun formatTime(
    seconds: Int
): String {

    val minutes =
        seconds / 60

    val remainingSeconds =
        seconds % 60

    return String.format(
        "%02d:%02d",
        minutes,
        remainingSeconds
    )
}