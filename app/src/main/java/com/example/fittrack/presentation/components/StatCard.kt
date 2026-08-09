package com.example.fittrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.theme.CardBorderActive
import com.example.fittrack.presentation.theme.CardBorderWhite
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextWhite

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    vectorIcon: ImageVector? = null,
    icon: String? = null
) {
    Column(
        modifier = modifier
            .background(color = CardDark, shape = RoundedCornerShape(20.dp))
            .border(1.dp, CardBorderWhite, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        if (vectorIcon != null) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(CardDarkElevated, CircleShape)
                    .border(1.dp, CardBorderActive.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = vectorIcon,
                    contentDescription = title,
                    tint = TextWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        } else if (icon != null) {
            Text(text = icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(6.dp))
        }

        Text(
            text = value,
            color = TextWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            color = TextGray,
            fontSize = 12.sp
        )
    }
}