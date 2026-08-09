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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class GlitterParticle(
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

    val transition =
        rememberInfiniteTransition(
            label = "FitTrackBackground"
        )

    /*
     * Main ribbon movement.
     */
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 18000,
                        easing = LinearEasing
                    ),
                repeatMode = RepeatMode.Restart
            ),
        label = "ribbonTime"
    )

    /*
     * Very slow breathing effect.
     */
    val breathing by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 5000,
                        easing = LinearEasing
                    ),
                repeatMode = RepeatMode.Reverse
            ),
        label = "breathing"
    )

    /*
     * Generate particles only once.
     * They don't randomly change every frame.
     */
    val particles =
        remember {

            val random =
                Random(12345)

            List(150) {

                GlitterParticle(
                    x = random.nextFloat(),
                    y = random.nextFloat(),
                    size =
                        random.nextFloat() *
                                2.2f +
                                0.4f,
                    speed =
                        random.nextFloat() *
                                1.2f +
                                0.2f,
                    phase =
                        random.nextFloat() *
                                6.28f,
                    alpha =
                        random.nextFloat() *
                                0.55f +
                                0.1f
                )
            }
        }

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val width = size.width
            val height = size.height

            /*
             * ------------------------------------------------
             * BACKGROUND
             * ------------------------------------------------
             */

            drawRect(
                color =
                    Color(0xFF030307)
            )

            /*
             * ------------------------------------------------
             * LARGE PURPLE ATMOSPHERIC GLOW
             * ------------------------------------------------
             */

            val glowX =
                width *
                        (0.45f +
                                sin(
                                    time *
                                            Math.PI.toFloat() *
                                            2f
                                ) *
                                0.22f)

            val glowY =
                height *
                        (0.43f +
                                cos(
                                    time *
                                            Math.PI.toFloat() *
                                            2f
                                ) *
                                0.18f)

            drawCircle(
                brush =
                    Brush.radialGradient(
                        colors =
                            listOf(
                                Color(
                                    0xFF9D4EDD
                                ).copy(
                                    alpha =
                                        0.13f +
                                                breathing *
                                                0.04f
                                ),

                                Color(
                                    0xFF6A0DAD
                                ).copy(
                                    alpha = 0.07f
                                ),

                                Color.Transparent
                            ),
                        center =
                            Offset(
                                glowX,
                                glowY
                            ),
                        radius =
                            width * 0.62f
                    ),
                radius =
                    width * 0.62f,
                center =
                    Offset(
                        glowX,
                        glowY
                    )
            )

            /*
             * ------------------------------------------------
             * FLOATING GLITTER DUST
             * ------------------------------------------------
             */

            particles.forEach { particle ->

                val particleTime =
                    time *
                            particle.speed *
                            Math.PI.toFloat() *
                            2f +
                            particle.phase

                /*
                 * Slow floating movement.
                 */
                val xMovement =
                    sin(particleTime) *
                            0.035f

                val yMovement =
                    cos(
                        particleTime * 0.7f
                    ) *
                            0.025f

                /*
                 * Slowly wrap particles around
                 * the screen.
                 */
                var x =
                    particle.x +
                            xMovement +
                            time *
                            particle.speed *
                            0.035f

                var y =
                    particle.y +
                            yMovement

                x %= 1f
                y %= 1f

                if (x < 0f) {
                    x += 1f
                }

                if (y < 0f) {
                    y += 1f
                }

                /*
                 * Some particles become brighter
                 * as they move.
                 */
                val sparkle =
                    (
                            sin(
                                particleTime * 2f
                            ) + 1f
                            ) / 2f

                drawCircle(
                    color =
                        Color(0xFFDDB7FF)
                            .copy(
                                alpha =
                                    particle.alpha *
                                            (
                                                    0.45f +
                                                            sparkle *
                                                            0.55f
                                                    )
                            ),
                    radius =
                        particle.size,
                    center =
                        Offset(
                            x * width,
                            y * height
                        )
                )
            }

            /*
             * ------------------------------------------------
             * FLOWING RIBBON
             * ------------------------------------------------
             */

            fun createRibbon(
                offset: Float,
                verticalOffset: Float
            ): Path {

                val path =
                    Path()

                val points = 80

                for (i in 0..points) {

                    val progress =
                        i.toFloat() /
                                points

                    val x =
                        width *
                                (
                                        -0.25f +
                                                progress *
                                                1.5f
                                        )

                    /*
                     * Several sine waves are combined
                     * to create an organic ribbon.
                     */
                    val wave1 =
                        sin(
                            progress *
                                    Math.PI *
                                    2.2 +
                                    time *
                                    Math.PI *
                                    2 +
                                    offset
                        ).toFloat()

                    val wave2 =
                        sin(
                            progress *
                                    Math.PI *
                                    4.1 -
                                    time *
                                    Math.PI *
                                    1.3 +
                                    offset
                        ).toFloat()

                    val wave3 =
                        cos(
                            progress *
                                    Math.PI *
                                    1.4 +
                                    time *
                                    Math.PI *
                                    2.5
                        ).toFloat()

                    val y =
                        height *
                                (
                                        0.48f +
                                                verticalOffset +
                                                wave1 *
                                                0.10f +
                                                wave2 *
                                                0.045f +
                                                wave3 *
                                                0.025f
                                        )

                    if (i == 0) {

                        path.moveTo(
                            x,
                            y
                        )

                    } else {

                        path.lineTo(
                            x,
                            y
                        )
                    }
                }

                return path
            }

            val ribbon =
                createRibbon(
                    offset = 0f,
                    verticalOffset = 0f
                )

            /*
             * OUTER PURPLE AURA
             */
            drawPath(
                path = ribbon,
                color =
                    Color(0xFF6A0DAD)
                        .copy(alpha = 0.055f),
                style =
                    Stroke(
                        width = 90f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
            )

            /*
             * BIG SOFT GLOW
             */
            drawPath(
                path = ribbon,
                color =
                    Color(0xFF8A2BE2)
                        .copy(alpha = 0.10f),
                style =
                    Stroke(
                        width = 55f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
            )

            /*
             * INNER GLOW
             */
            drawPath(
                path = ribbon,
                color =
                    Color(0xFFB14CFF)
                        .copy(alpha = 0.20f),
                style =
                    Stroke(
                        width = 25f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
            )

            /*
             * MAIN RIBBON
             */
            drawPath(
                path = ribbon,
                brush =
                    Brush.linearGradient(
                        colors =
                            listOf(
                                Color(0xFF5A189A)
                                    .copy(alpha = 0.55f),

                                Color(0xFF9D4EDD)
                                    .copy(alpha = 0.85f),

                                Color(0xFFE0AAFF)
                                    .copy(alpha = 0.75f),

                                Color(0xFF7B2CBF)
                                    .copy(alpha = 0.60f),

                                Color(0xFF3C096C)
                                    .copy(alpha = 0.50f)
                            )
                    ),
                style =
                    Stroke(
                        width = 7f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
            )

            /*
             * THIN BRIGHT CORE
             */
            drawPath(
                path = ribbon,
                color =
                    Color(0xFFE8C7FF)
                        .copy(alpha = 0.32f),
                style =
                    Stroke(
                        width = 2f,
                        cap = StrokeCap.Round
                    )
            )

            /*
             * ------------------------------------------------
             * SECOND FAINT RIBBON
             * ------------------------------------------------
             */

            val secondRibbon =
                createRibbon(
                    offset = 2.7f,
                    verticalOffset = 0.14f
                )

            drawPath(
                path = secondRibbon,
                color =
                    Color(0xFF7B2CBF)
                        .copy(alpha = 0.06f),
                style =
                    Stroke(
                        width = 50f,
                        cap = StrokeCap.Round
                    )
            )

            drawPath(
                path = secondRibbon,
                color =
                    Color(0xFF9D4EDD)
                        .copy(alpha = 0.10f),
                style =
                    Stroke(
                        width = 18f,
                        cap = StrokeCap.Round
                    )
            )

            drawPath(
                path = secondRibbon,
                color =
                    Color(0xFFD8B4FE)
                        .copy(alpha = 0.14f),
                style =
                    Stroke(
                        width = 3f,
                        cap = StrokeCap.Round
                    )
            )

            /*
             * ------------------------------------------------
             * FLOATING BRIGHT DUST AROUND RIBBON
             * ------------------------------------------------
             */

            repeat(35) { index ->

                val seed =
                    index * 7.31f

                val orbit =
                    time *
                            (
                                    0.6f +
                                            (index % 5) *
                                            0.12f
                                    )

                val angle =
                    seed +
                            orbit *
                            Math.PI.toFloat() *
                            2f

                val distance =
                    width *
                            (
                                    0.04f +
                                            (index % 7) *
                                            0.018f
                                    )

                val x =
                    glowX +
                            cos(angle) *
                            distance

                val y =
                    glowY +
                            sin(angle) *
                            distance *
                            0.55f

                val sparkle =
                    (
                            sin(
                                angle * 3f
                            ) + 1f
                            ) / 2f

                drawCircle(
                    color =
                        Color(0xFFEAD7FF)
                            .copy(
                                alpha =
                                    0.15f +
                                            sparkle *
                                            0.55f
                            ),
                    radius =
                        1f +
                                sparkle *
                                1.5f,
                    center =
                        Offset(
                            x,
                            y
                        )
                )
            }
        }

        /*
         * Your actual app UI sits above the animation.
         */
        content()
    }
}