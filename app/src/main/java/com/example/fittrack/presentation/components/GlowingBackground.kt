package com.example.fittrack.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ShadowParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val phase: Float,
    val alpha: Float
)

@Composable
fun GlowingBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "ShadowyBackground")

    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shadowTime"
    )

    val breathing by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shadowBreathing"
    )

    val particles = remember {
        val random = Random(42)
        List(90) {
            ShadowParticle(
                x = random.nextFloat(),
                y = random.nextFloat(),
                size = random.nextFloat() * 2.5f + 0.5f,
                speed = random.nextFloat() * 0.8f + 0.2f,
                phase = random.nextFloat() * 6.28f,
                alpha = random.nextFloat() * 0.4f + 0.1f
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Deep Dusty Obsidian Base Layer
            drawRect(color = Color(0xFF0C0D10))

            // 2. Soft Radial Shadowy White/Silver Core Glow
            val glowX = width * (0.5f + sin(time * Math.PI.toFloat() * 2f) * 0.15f)
            val glowY = height * (0.35f + cos(time * Math.PI.toFloat() * 2f) * 0.12f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE2E8F0).copy(alpha = 0.05f + breathing * 0.03f),
                        Color(0xFF475569).copy(alpha = 0.03f),
                        Color.Transparent
                    ),
                    center = Offset(glowX, glowY),
                    radius = width * 0.75f
                ),
                radius = width * 0.75f,
                center = Offset(glowX, glowY)
            )

            // 3. Bottom Subtle Deep Charcoal Shadow Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E293B).copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.5f, height * 0.85f),
                    radius = width * 0.8f
                ),
                radius = width * 0.8f,
                center = Offset(width * 0.5f, height * 0.85f)
            )

            // 4. Floating Shadowy White Glitter Particles
            particles.forEach { particle ->
                val particleTime = time * particle.speed * Math.PI.toFloat() * 2f + particle.phase
                var px = particle.x + sin(particleTime) * 0.02f + time * particle.speed * 0.02f
                var py = particle.y + cos(particleTime * 0.6f) * 0.015f

                px %= 1f
                py %= 1f
                if (px < 0f) px += 1f
                if (py < 0f) py += 1f

                val sparkle = (sin(particleTime * 2f) + 1f) / 2f

                drawCircle(
                    color = Color(0xFFF8FAFC).copy(
                        alpha = particle.alpha * (0.3f + sparkle * 0.7f)
                    ),
                    radius = particle.size,
                    center = Offset(px * width, py * height)
                )
            }

            // 5. Elegant Shadowy Ribbon Lines
            val ribbonPath = Path().apply {
                val points = 60
                for (i in 0..points) {
                    val progress = i.toFloat() / points
                    val rx = width * (-0.2f + progress * 1.4f)
                    val wave1 = sin(progress * Math.PI * 2.0 + time * Math.PI * 2).toFloat()
                    val wave2 = cos(progress * Math.PI * 3.5 - time * Math.PI * 1.5).toFloat()
                    val ry = height * (0.4f + wave1 * 0.08f + wave2 * 0.03f)

                    if (i == 0) moveTo(rx, ry) else lineTo(rx, ry)
                }
            }

            drawPath(
                path = ribbonPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF334155).copy(alpha = 0.05f),
                        Color(0xFF94A3B8).copy(alpha = 0.15f),
                        Color(0xFFF8FAFC).copy(alpha = 0.25f),
                        Color(0xFF64748B).copy(alpha = 0.10f)
                    )
                ),
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
        }

        content()
    }
}