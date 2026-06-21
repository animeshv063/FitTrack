package com.example.fittrack.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.theme.BackgroundDark
import com.example.fittrack.presentation.theme.TextWhite
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    viewModel: WorkoutViewModel
) {

    val steps by viewModel
        .steps
        .collectAsState()

    LaunchedEffect(
        Unit
    ) {

        viewModel.startStepCounter()

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {

        Text(
            text = "Profile",
            color = TextWhite,
            fontSize = 28.sp
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Text(
            text = "Steps Today: $steps",
            color = TextWhite,
            fontSize = 20.sp
        )

    }
}

