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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.theme.CardBorderWhite
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.FlameOrange
import com.example.fittrack.presentation.theme.NeonCyan
import com.example.fittrack.presentation.theme.NeonTeal
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextWhite

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    vectorIcon: ImageVector? = null,
    icon: String? = null,
    accentTint: Color? = null
) {
    val effectiveTint = accentTint ?: when (vectorIcon) {
        Icons.Rounded.LocalFireDepartment -> FlameOrange
        Icons.Rounded.FitnessCenter -> Color(0xFFA855F7) // Electric Purple
        Icons.AutoMirrored.Rounded.DirectionsWalk -> NeonTeal
        Icons.Rounded.WaterDrop -> Color(0xFF38BDF8) // Sky Blue
        else -> NeonCyan
    }

    Column(
        modifier = modifier
            .background(color = CardDark, shape = RoundedCornerShape(20.dp))
            .border(1.dp, CardBorderWhite, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        if (vectorIcon != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(CardDarkElevated, CircleShape)
                    .border(1.dp, effectiveTint.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = vectorIcon,
                    contentDescription = title,
                    tint = effectiveTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        } else if (icon != null) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = value,
            color = TextWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            color = TextGray,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}