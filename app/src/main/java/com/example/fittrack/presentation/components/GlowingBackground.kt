package com.example.fittrack.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

object BackgroundAnimationManager {
    private val _isGlowEnabled = MutableStateFlow(true)
    val isGlowEnabled = _isGlowEnabled.asStateFlow()

    fun setGlowEnabled(enabled: Boolean) {
        _isGlowEnabled.value = enabled
    }
}

private data class CyberNode(
    val initialX: Float,
    val initialY: Float,
    val speedX: Float,
    val speedY: Float,
    val radius: Float,
    val colorIndex: Int,
    val phase: Float
)

@Composable
fun GlowingBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isAnimationEnabled by BackgroundAnimationManager.isGlowEnabled.collectAsState()

    val transition = rememberInfiniteTransition(label = "CyberPlasmaBackground")

    // Dynamic 8-second breathing pulse
    val energyPulse by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "energyPulse"
    )

    // Flowing progression loop (16 seconds)
    val timeFlow by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "timeFlow"
    )

    // Living Cyber Constellation Nodes
    val cyberNodes = remember {
        val random = Random(42)
        List(28) {
            CyberNode(
                initialX = random.nextFloat(),
                initialY = random.nextFloat(),
                speedX = (random.nextFloat() - 0.5f) * 0.4f,
                speedY = (random.nextFloat() - 0.5f) * 0.35f,
                radius = random.nextFloat() * 2.5f + 1.8f,
                colorIndex = random.nextInt(4),
                phase = random.nextFloat() * 6.28f
            )
        }
    }

    val palette = listOf(
        Color(0xFF00FFA3), // Neon Mint
        Color(0xFF00F2FE), // Electric Cyan
        Color(0xFFA855F7), // Cyber Violet
        Color(0xFFFF7A00)  // Solar Flame
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. AMOLED True Black Canvas
            drawRect(color = Color(0xFF000000))

            if (isAnimationEnabled) {
                // 2. Liquid Plasma Glowing Mesh Orbs (Rich, Vibrant & Shifting)
                val t = timeFlow * 6.28318f

                // Orb 1: Top-Right Cyan & Teal Energy Nebula
                val orb1X = width * (0.72f + sin(t * 0.7f) * 0.12f)
                val orb1Y = height * (0.18f + cos(t * 0.5f) * 0.08f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00F2FE).copy(alpha = 0.16f + energyPulse * 0.08f),
                            Color(0xFF00FFA3).copy(alpha = 0.08f + energyPulse * 0.04f),
                            Color.Transparent
                        ),
                        center = Offset(orb1X, orb1Y),
                        radius = width * 0.85f
                    ),
                    radius = width * 0.85f,
                    center = Offset(orb1X, orb1Y)
                )

                // Orb 2: Bottom-Left Cyber Violet & Magenta Core
                val orb2X = width * (0.25f - cos(t * 0.6f) * 0.10f)
                val orb2Y = height * (0.80f + sin(t * 0.8f) * 0.07f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFA855F7).copy(alpha = 0.18f + (1f - energyPulse) * 0.07f),
                            Color(0xFF00F2FE).copy(alpha = 0.07f),
                            Color.Transparent
                        ),
                        center = Offset(orb2X, orb2Y),
                        radius = width * 0.90f
                    ),
                    radius = width * 0.90f,
                    center = Offset(orb2X, orb2Y)
                )

                // Orb 3: Center Pulsing Solar/Teal Core
                val orb3X = width * (0.50f + sin(t * 1.2f) * 0.08f)
                val orb3Y = height * (0.48f - cos(t * 0.9f) * 0.06f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00FFA3).copy(alpha = 0.10f + energyPulse * 0.06f),
                            Color(0xFFFF7A00).copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        center = Offset(orb3X, orb3Y),
                        radius = width * 0.70f
                    ),
                    radius = width * 0.70f,
                    center = Offset(orb3X, orb3Y)
                )

                // 3. Cyber Sub-Surface Perspective Horizon Grid
                val gridAlpha = 0.035f + energyPulse * 0.02f
                val gridLines = 7
                for (i in 0..gridLines) {
                    val lineProgress = i.toFloat() / gridLines
                    val lineY = height * (0.60f + lineProgress * 0.38f)
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xFF00FFA3).copy(alpha = gridAlpha * (lineProgress + 0.3f)),
                                Color(0xFF00F2FE).copy(alpha = gridAlpha * (lineProgress + 0.3f)),
                                Color.Transparent
                            )
                        ),
                        start = Offset(0f, lineY),
                        end = Offset(width, lineY),
                        strokeWidth = 1.2f
                    )
                }

                // 4. Harmonic Glowing Bio-Waves
                val wave1 = Path()
                val wave2 = Path()
                val wavePoints = 40
                for (i in 0..wavePoints) {
                    val progress = i.toFloat() / wavePoints
                    val px = width * progress
                    val py1 = height * (0.35f + sin(progress * 5.0f + t) * 0.04f + cos(progress * 2.5f - t * 0.7f) * 0.02f)
                    val py2 = height * (0.62f + cos(progress * 4.0f - t * 1.1f) * 0.045f + sin(progress * 3.0f + t * 0.5f) * 0.025f)

                    if (i == 0) {
                        wave1.moveTo(px, py1)
                        wave2.moveTo(px, py2)
                    } else {
                        wave1.lineTo(px, py1)
                        wave2.lineTo(px, py2)
                    }
                }

                drawPath(
                    path = wave1,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF00F2FE).copy(alpha = 0.14f + energyPulse * 0.08f),
                            Color(0xFFA855F7).copy(alpha = 0.12f + energyPulse * 0.06f),
                            Color.Transparent
                        )
                    ),
                    style = Stroke(width = 2.2f, cap = StrokeCap.Round)
                )

                drawPath(
                    path = wave2,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF00FFA3).copy(alpha = 0.15f + (1f - energyPulse) * 0.07f),
                            Color(0xFF00F2FE).copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    ),
                    style = Stroke(width = 1.8f, cap = StrokeCap.Round)
                )

                // 5. Living Constellation Nodes & Dynamic Proximity Energy Lasers
                val currentPositions = cyberNodes.map { node ->
                    var nx = node.initialX + sin(t * 0.8f + node.phase) * 0.08f + node.speedX * (timeFlow * 2f)
                    var ny = node.initialY + cos(t * 0.6f + node.phase) * 0.08f + node.speedY * (timeFlow * 2f)

                    nx = (nx % 1f + 1f) % 1f
                    ny = (ny % 1f + 1f) % 1f

                    Offset(nx * width, ny * height)
                }

                // Connect nearby nodes with glowing laser lines (Constellation lattice)
                val maxConnectDist = width * 0.26f
                for (i in currentPositions.indices) {
                    for (j in (i + 1) until currentPositions.size) {
                        val p1 = currentPositions[i]
                        val p2 = currentPositions[j]
                        val dx = p1.x - p2.x
                        val dy = p1.y - p2.y
                        val dist = sqrt(dx * dx + dy * dy)

                        if (dist < maxConnectDist) {
                            val lineAlpha = (1f - (dist / maxConnectDist)) * 0.28f * (0.6f + energyPulse * 0.4f)
                            val nodeColor = palette[cyberNodes[i].colorIndex % palette.size]

                            drawLine(
                                color = nodeColor.copy(alpha = lineAlpha),
                                start = p1,
                                end = p2,
                                strokeWidth = 1.0f
                            )
                        }
                    }
                }

                // Draw glowing node points
                currentPositions.forEachIndexed { index, pos ->
                    val node = cyberNodes[index]
                    val nodeColor = palette[node.colorIndex % palette.size]
                    val shimmer = (sin(t * 2.0f + node.phase) + 1f) / 2f

                    // Outer Halo
                    drawCircle(
                        color = nodeColor.copy(alpha = 0.22f + shimmer * 0.25f),
                        radius = node.radius * 2.4f,
                        center = pos
                    )
                    // Bright Core
                    drawCircle(
                        color = nodeColor.copy(alpha = 0.75f + shimmer * 0.25f),
                        radius = node.radius,
                        center = pos
                    )
                }
            }
        }

        content()
    }
}