package com.example.fittrack.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.fittrack.presentation.theme.CardBorderActive
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextSilver
import com.example.fittrack.presentation.theme.TextWhite
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiParticle(
    val angle: Double,
    val speed: Float,
    val radius: Float,
    val color: Color,
    val shapeType: Int, // 0 = Circle, 1 = Ribbon, 2 = Sparkle Diamond
    val rotation: Float
)

@Composable
fun GiftBurstDialog(
    goalTitle: String,
    goalTarget: String,
    onDismiss: () -> Unit
) {
    val trophyScale = remember { Animatable(0f) }
    val burstProgress = remember { Animatable(0f) }
    val shockwaveProgress = remember { Animatable(0f) }
    val auraGlowAlpha = remember { Animatable(0f) }

    // 90 High-Energy Multi-Color Celebration Particles
    val particles = remember {
        val colors = listOf(
            Color(0xFF00F2FE), // Neon Cyan
            Color(0xFF00FFA3), // Neon Lime
            Color(0xFFF59E0B), // Radiant Amber
            Color(0xFF10B981), // Emerald
            Color(0xFFA855F7), // Vivid Violet
            Color(0xFFFFFFFF), // Diamond White
            Color(0xFF38BDF8)  // Sky Blue
        )
        List(90) {
            ConfettiParticle(
                angle = Random.nextDouble(0.0, Math.PI * 2),
                speed = Random.nextFloat() * 180f + 70f,
                radius = Random.nextFloat() * 6f + 3f,
                color = colors[Random.nextInt(colors.size)],
                shapeType = Random.nextInt(3),
                rotation = Random.nextFloat() * 360f
            )
        }
    }

    LaunchedEffect(Unit) {
        // High-Energy Spring Pop
        trophyScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    LaunchedEffect(Unit) {
        // Shockwave Expansion
        shockwaveProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        // Confetti Explosion
        burstProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1300, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        // Radiant Aura Glow
        auraGlowAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark, RoundedCornerShape(28.dp))
                .border(1.5.dp, Color(0xFF00FFA3).copy(alpha = 0.8f), RoundedCornerShape(28.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Full Screen Radial Confetti Canvas with Shockwaves
            Canvas(modifier = Modifier.size(320.dp)) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f - 20f)
                val progress = burstProgress.value
                val alpha = (1f - progress).coerceIn(0f, 1f)

                // Expanding Shockwave Rings
                val shockProgress = shockwaveProgress.value
                val shockAlpha = (1f - shockProgress).coerceIn(0f, 1f)
                if (shockProgress < 1f) {
                    drawCircle(
                        color = Color(0xFF00FFA3).copy(alpha = shockAlpha * 0.5f),
                        radius = shockProgress * 150f,
                        center = centerOffset,
                        style = Stroke(width = 3.dp.toPx() * (1f - shockProgress))
                    )
                    drawCircle(
                        color = Color(0xFF00F2FE).copy(alpha = shockAlpha * 0.35f),
                        radius = shockProgress * 120f,
                        center = centerOffset,
                        style = Stroke(width = 2.dp.toPx() * (1f - shockProgress))
                    )
                }

                // 90 Flying Particles with Physics
                particles.forEach { p ->
                    val distance = p.speed * progress
                    val x = centerOffset.x + (cos(p.angle) * distance).toFloat()
                    val y = centerOffset.y + (sin(p.angle) * distance).toFloat() + (progress * progress * 35f) // Subtle gravity drop

                    when (p.shapeType) {
                        0 -> {
                            // Circular glow particle
                            drawCircle(
                                color = p.color.copy(alpha = alpha),
                                radius = p.radius,
                                center = Offset(x, y)
                            )
                        }
                        1 -> {
                            // High-speed confetti ribbon streak
                            drawLine(
                                color = p.color.copy(alpha = alpha),
                                start = Offset(x - 5f, y - 5f),
                                end = Offset(x + 5f, y + 5f),
                                strokeWidth = 3.5f
                            )
                        }
                        else -> {
                            // 4-Point Diamond Sparkle Star
                            drawLine(
                                color = p.color.copy(alpha = alpha),
                                start = Offset(x, y - 6f),
                                end = Offset(x, y + 6f),
                                strokeWidth = 2.5f
                            )
                            drawLine(
                                color = p.color.copy(alpha = alpha),
                                start = Offset(x - 6f, y),
                                end = Offset(x + 6f, y),
                                strokeWidth = 2.5f
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Radiant Glowing Trophy Emblem
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .graphicsLayer {
                            scaleX = trophyScale.value
                            scaleY = trophyScale.value
                        }
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF00FFA3).copy(alpha = 0.4f * auraGlowAlpha.value),
                                        Color(0xFF00F2FE).copy(alpha = 0.2f * auraGlowAlpha.value),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.minDimension * 0.75f
                            )
                        }
                        .background(CardDarkElevated, CircleShape)
                        .border(2.5.dp, Color(0xFF00FFA3), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = Color(0xFFFBBF24), // Brilliant Gold
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "🎉 Goal Achieved!",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = goalTitle,
                    color = Color(0xFF00FFA3),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                if (goalTarget.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Target: $goalTarget",
                        color = TextSilver,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Outstanding performance! This milestone has been unlocked and stored in your Achieved Goals history.",
                    color = TextGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(22.dp))

                PrimaryButton(
                    text = "Continue",
                    onClick = onDismiss
                )
            }
        }
    }
}
