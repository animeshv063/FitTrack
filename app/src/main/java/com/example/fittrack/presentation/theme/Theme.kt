package com.example.fittrack.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TextWhite,
    secondary = TextSilver,
    tertiary = CardBorderActive,
    background = BackgroundDark,
    surface = CardDark,
    surfaceVariant = CardDarkElevated,
    onPrimary = BackgroundDark,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun FitTrackTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}