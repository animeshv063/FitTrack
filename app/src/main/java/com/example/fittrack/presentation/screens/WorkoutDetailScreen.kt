package com.example.fittrack.presentation.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fittrack.data.local.entity.ExerciseEntity
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel
import androidx.compose.material3.Checkbox
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import com.example.fittrack.data.notification.NotificationHelper


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

    val context = LocalContext.current


    val notificationHelper = remember {
        NotificationHelper(
            context
        )
    }

    LaunchedEffect(
        timerRunning
    ) {

        while (
            timerRunning &&
            restTime > 0
        ) {

            delay(1000)

            restTime--

        }


        if (restTime == 0 && timerRunning) {

            timerRunning = false

            notificationHelper
                .showRestFinishedNotification()

        }

    }


    Column(
        modifier = Modifier.padding(20.dp)
    ) {


        Text(
            text = "Exercises"
        )


        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = {
                Text("Exercise")
            }
        )


        OutlinedTextField(
            value = sets,
            onValueChange = {
                sets = it
            },
            label = {
                Text("Sets")
            }
        )


        OutlinedTextField(
            value = reps,
            onValueChange = {
                reps = it
            },
            label = {
                Text("Reps")
            }
        )


        OutlinedTextField(
            value = weight,
            onValueChange = {
                weight = it
            },
            label = {
                Text("Weight")
            }
        )


        Button(
            onClick = {

                viewModel.addExercise(
                    ExerciseEntity(
                        workoutId = workoutId,
                        name = name,
                        sets = sets.toInt(),
                        reps = reps.toInt(),
                        weight = weight.toInt()
                    )
                )


                name = ""
                sets = ""
                reps = ""
                weight = ""

            }
        ) {

            Text("Add Exercise")

        }

        Button(
            onClick = {

                workoutStarted =
                    !workoutStarted

            }
        ) {

            Text(
                text =
                    if (workoutStarted)
                        "Finish Workout"
                    else
                        "Start Workout"
            )

        }

        if (timerRunning) {

            Text(
                text = "Rest: $restTime seconds"
            )

        }

        exercises.forEach { exercise ->


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                Column(
                    modifier = Modifier.padding(10.dp)
                ) {

                    Text(
                        exercise.name
                    )

                    Text(
                        "${exercise.sets} x ${exercise.reps}"
                    )

                    Text(
                        "${exercise.weight} kg"
                    )
                    if (workoutStarted) {


                        repeat(
                            exercise.sets
                        ) { index ->


                            var checked by remember {
                                mutableStateOf(false)
                            }


                            Row {


                                Checkbox(
                                    checked = checked,

                                    onCheckedChange = {

                                        checked = it

                                        if (it) {

                                            restTime = 90

                                            timerRunning = true

                                        }

                                    }
                                )


                                Text(
                                    text = "Set ${index + 1} done"
                                )

                            }

                        }

                    }

                }

            }

        }

    }

}