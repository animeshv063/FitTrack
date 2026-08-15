package com.example.fittrack.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.R
import com.example.fittrack.presentation.theme.TextWhite

@Composable
fun FitTrackLogo(
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    showText: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Icon Image Container - Fully Visible & Uncropped
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.22f))
                .border(
                    width = 1.dp,
                    color = Color(0xFF00FFA3).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(size * 0.22f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "FitTrack App Icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(size)
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // FIT in Neon Teal
                Text(
                    text = "FIT",
                    color = Color(0xFF00FFA3),
                    fontSize = (size.value * 0.48f).coerceAtLeast(18f).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                // TRACK in Crisp White
                Text(
                    text = "TRACK",
                    color = TextWhite,
                    fontSize = (size.value * 0.48f).coerceAtLeast(18f).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
fun FitTrackFullLogoCard(
    modifier: Modifier = Modifier,
    size: Dp = 92.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, Color(0xFF00FFA3).copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "FitTrack Full Icon",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(size)
        )
    }
}
