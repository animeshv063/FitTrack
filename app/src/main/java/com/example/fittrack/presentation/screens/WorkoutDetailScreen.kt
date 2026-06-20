package com.example.fittrack.presentation.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fittrack.data.local.entity.ExerciseEntity
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel


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

                }

            }

        }

    }

}