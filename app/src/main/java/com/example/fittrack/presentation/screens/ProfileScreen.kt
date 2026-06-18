package com.example.fittrack.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.theme.BackgroundDark
import com.example.fittrack.presentation.theme.TextWhite

@Composable
fun ProfileScreen() {

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

    }
}

