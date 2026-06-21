package com.example.fittrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.theme.*

@Composable
fun WorkoutCard(
    name: String,
    duration: Int
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                CardDark,
                RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {

        Text(
            text = "💪 $name",
            color = TextWhite,
            fontSize = 22.sp
        )


        Spacer(
            Modifier.height(16.dp)
        )


        Text(
            text = "$duration min workout",
            color = PrimaryGreen,
            fontSize = 16.sp
        )

    }
}