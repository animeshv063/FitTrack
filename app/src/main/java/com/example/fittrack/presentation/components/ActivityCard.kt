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
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.PrimaryGreen
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextWhite

@Composable
fun ActivityCard() {

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
            text = "Today's Activity",
            color = TextWhite,
            fontSize = 20.sp
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "🚶 8,540 / 10,000 steps",
            color = TextWhite,
            fontSize = 18.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LinearProgressIndicator(
            progress ={ 0.85f },
            color = PrimaryGreen,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "🔥 624 kcal burned",
            color = TextGray,
            fontSize = 15.sp
        )
    }
}

