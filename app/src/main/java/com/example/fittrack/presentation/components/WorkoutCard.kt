package com.example.fittrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.PrimaryGreen
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextWhite

@Composable
fun WorkoutCard() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = CardDark,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {

        Text(
            text = "💪 Push Day",
            color = TextWhite,
            fontSize = 22.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Chest • Shoulder • Triceps",
            color = TextGray,
            fontSize = 15.sp
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "45 min workout",
            color = PrimaryGreen,
            fontSize = 16.sp
        )
    }
}