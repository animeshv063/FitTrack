package com.example.fittrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.theme.*

@Composable
fun ActivityCard(
    steps: Int,
    calories: Int
) {

    val progress =
        (steps / 10000f)
            .coerceAtMost(1f)


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
            "Today's Activity",
            color = TextWhite,
            fontSize = 20.sp
        )


        Spacer(
            Modifier.height(16.dp)
        )


        Text(
            "🚶 $steps / 10000 steps",
            color = TextWhite,
            fontSize = 18.sp
        )


        Spacer(
            Modifier.height(12.dp)
        )


        LinearProgressIndicator(
            progress = { progress },
            color = PrimaryGreen,
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(
            Modifier.height(12.dp)
        )


        Text(
            "🔥 $calories kcal burned",
            color = TextGray
        )

    }
}