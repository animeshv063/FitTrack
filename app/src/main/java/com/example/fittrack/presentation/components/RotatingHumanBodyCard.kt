package com.example.fittrack.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.theme.CardBorderActive
import com.example.fittrack.presentation.theme.CardBorderWhite
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextSilver
import com.example.fittrack.presentation.theme.TextWhite
import kotlin.math.cos
import kotlin.math.sin

data class Point3D(val x: Float, val y: Float, val z: Float)

data class MuscleGroupData(
    val id: String,
    val name: String,
    val anatomicalTerm: String,
    val anchor3D: Point3D,
    val bestExercises: List<String>,
    val functionDescription: String
)

val ALL_MUSCLE_GROUPS = listOf(
    MuscleGroupData("chest", "Chest", "Pectoralis Major", Point3D(0f, -0.9f, 0.28f), listOf("Barbell Bench Press", "Incline Dumbbell Flyes", "Cable Crossovers", "Push-Ups"), "Horizontal adduction & shoulder flexion"),
    MuscleGroupData("shoulders", "Shoulders", "Deltoids (Ant/Med/Post)", Point3D(0.55f, -1.25f, 0f), listOf("Overhead Shoulder Press", "Lateral Raises", "Face Pulls", "Front Dumbbell Raises"), "Arm abduction, elevation & rotation"),
    MuscleGroupData("biceps", "Biceps", "Biceps Brachii", Point3D(-0.75f, -0.65f, 0.15f), listOf("Barbell Bicep Curls", "Hammer Curls", "Incline Dumbbell Curls", "Preacher Curls"), "Elbow flexion & forearm supination"),
    MuscleGroupData("triceps", "Triceps", "Triceps Brachii", Point3D(0.75f, -0.65f, -0.15f), listOf("Tricep Rope Pushdowns", "Skull Crushers", "Close-Grip Bench Press", "Dips"), "Elbow extension & arm straightening"),
    MuscleGroupData("forearms", "Forearms", "Brachioradialis", Point3D(-0.85f, -0.15f, 0.2f), listOf("Wrist Curls", "Reverse Barbell Curls", "Farmer's Carries"), "Grip strength & wrist control"),
    MuscleGroupData("abs", "Abs & Core", "Rectus Abdominis", Point3D(0f, -0.3f, 0.32f), listOf("Hanging Leg Raises", "Cable Ab Crunches", "Planks", "Ab Wheel Rollouts"), "Spinal flexion & core stabilization"),
    MuscleGroupData("obliques", "Obliques", "External Obliques", Point3D(0.35f, -0.35f, 0.25f), listOf("Russian Twists", "Side Planks", "Woodchoppers"), "Spinal rotation & lateral flexion"),
    MuscleGroupData("traps", "Trapezius", "Upper Trapezius", Point3D(0f, -1.3f, -0.2f), listOf("Heavy Barbell Shrugs", "Dumbbell Shrugs", "Upright Rows"), "Scapular elevation & neck support"),
    MuscleGroupData("lats", "Lats", "Latissimus Dorsi", Point3D(0.42f, -0.75f, -0.28f), listOf("Lat Pulldowns", "Weighted Pull-Ups", "Bent-Over Barbell Rows"), "Shoulder extension & adduction (V-taper)"),
    MuscleGroupData("lower_back", "Lower Back", "Erector Spinae", Point3D(0f, -0.2f, -0.25f), listOf("Conventional Deadlifts", "Hyperextensions", "Good Mornings"), "Spinal extension & posture stability"),
    MuscleGroupData("glutes", "Glutes", "Gluteus Maximus", Point3D(0.3f, 0.1f, -0.28f), listOf("Barbell Hip Thrusts", "Romanian Deadlifts", "Bulgarian Split Squats"), "Hip extension & pelvic drive"),
    MuscleGroupData("quads", "Quadriceps", "Rectus Femoris & Vastus", Point3D(-0.36f, 0.45f, 0.22f), listOf("Barbell Back Squats", "Leg Press", "Leg Extensions", "Lunges"), "Knee extension & leg driving power"),
    MuscleGroupData("hamstrings", "Hamstrings", "Biceps Femoris", Point3D(0.35f, 0.45f, -0.22f), listOf("Romanian Deadlifts", "Lying Leg Curls", "Nordic Hamstring Curls"), "Knee flexion & hip extension"),
    MuscleGroupData("calves", "Calves", "Gastrocnemius & Soleus", Point3D(-0.35f, 1.2f, 0.15f), listOf("Standing Calf Raises", "Seated Calf Raises", "Donkey Calf Raises"), "Ankle plantarflexion & sprint power")
)

@Composable
fun RotatingHumanBodyCard(
    selectedMuscle: MuscleGroupData,
    onMuscleSelect: (MuscleGroupData) -> Unit,
    modifier: Modifier = Modifier
) {
    var isAutoRotating by remember { mutableStateOf(true) }
    var manualAngleOffset by remember { mutableStateOf(0f) }

    val transition = rememberInfiniteTransition(label = "3DHumanBody")
    val animatedAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "yRotation"
    )

    val currentAngle = if (isAutoRotating) animatedAngle else manualAngleOffset

    // 3D Skeleton Wireframe Nodes
    val head = Point3D(0f, -1.6f, 0f)
    val neck = Point3D(0f, -1.35f, 0f)
    val shoulderL = Point3D(-0.55f, -1.25f, 0f)
    val shoulderR = Point3D(0.55f, -1.25f, 0f)
    val chestMid = Point3D(0f, -0.9f, 0.28f)
    val spineMid = Point3D(0f, -0.6f, -0.05f)
    val hipL = Point3D(-0.35f, 0.0f, 0f)
    val hipR = Point3D(0.35f, 0.0f, 0f)

    val elbowL = Point3D(-0.75f, -0.65f, 0.1f)
    val elbowR = Point3D(0.75f, -0.65f, 0.1f)
    val wristL = Point3D(-0.85f, -0.15f, 0.2f)
    val wristR = Point3D(0.85f, -0.15f, 0.2f)

    val kneeL = Point3D(-0.38f, 0.75f, 0.05f)
    val kneeR = Point3D(0.38f, 0.75f, 0.05f)
    val ankleL = Point3D(-0.35f, 1.5f, 0f)
    val ankleR = Point3D(0.35f, 1.5f, 0f)

    val ribL = Point3D(-0.42f, -0.8f, 0.18f)
    val ribR = Point3D(0.42f, -0.8f, 0.18f)
    val absLow = Point3D(0f, -0.25f, 0.28f)
    val quadL = Point3D(-0.36f, 0.45f, 0.22f)
    val quadR = Point3D(0.36f, 0.45f, 0.22f)
    val calfL = Point3D(-0.35f, 1.2f, 0.15f)
    val calfR = Point3D(0.35f, 1.2f, 0.15f)

    val bones = listOf(
        head to neck, neck to shoulderL, neck to shoulderR,
        shoulderL to elbowL, elbowL to wristL,
        shoulderR to elbowR, elbowR to wristR,
        shoulderL to chestMid, shoulderR to chestMid,
        chestMid to spineMid, spineMid to hipL, spineMid to hipR,
        shoulderL to ribL, shoulderR to ribR,
        ribL to absLow, ribR to absLow, absLow to hipL, absLow to hipR,
        hipL to kneeL, kneeL to ankleL, hipR to kneeR, kneeR to ankleR,
        hipL to quadL, quadL to kneeL, hipR to quadR, quadR to kneeR,
        kneeL to calfL, calfL to ankleL, kneeR to calfR, calfR to ankleR
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(24.dp))
            .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(CardDarkElevated, CircleShape)
                    .border(1.dp, CardBorderActive.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccessibilityNew,
                    contentDescription = "Anatomy",
                    tint = TextWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "3D Anatomical Body Mannequin",
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap any muscle to highlight targeted exercises",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal Segmented View Mode Tabs (ALL CAPS & Unified Dark Theme)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDarkElevated, RoundedCornerShape(16.dp))
                .border(1.dp, CardBorderWhite, RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val modes = listOf(
                "360° AUTO" to (isAutoRotating),
                "FRONT VIEW" to (!isAutoRotating && manualAngleOffset == 0f),
                "BACK VIEW" to (!isAutoRotating && manualAngleOffset == 180f)
            )

            modes.forEach { (label, isSelected) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) CardDark else CardDarkElevated,
                            RoundedCornerShape(12.dp)
                        )
                        .then(
                            if (isSelected) Modifier.border(1.dp, CardBorderActive, RoundedCornerShape(12.dp))
                            else Modifier
                        )
                        .clickable {
                            when (label) {
                                "360° AUTO" -> {
                                    isAutoRotating = true
                                }
                                "FRONT VIEW" -> {
                                    isAutoRotating = false
                                    manualAngleOffset = 0f
                                }
                                "BACK VIEW" -> {
                                    isAutoRotating = false
                                    manualAngleOffset = 180f
                                }
                            }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) TextWhite else TextSilver,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3D Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .background(CardDarkElevated, RoundedCornerShape(20.dp))
                .border(1.dp, CardBorderWhite.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(290.dp)) {
                val width = size.width
                val height = size.height
                val centerX = width / 2f
                val centerY = height / 2f + 10.dp.toPx()
                val scale = height * 0.22f
                val rad = Math.toRadians(currentAngle.toDouble()).toFloat()

                val cosA = cos(rad)
                val sinA = sin(rad)
                val cameraDist = 4.0f

                fun project(p: Point3D): Offset {
                    val rx = p.x * cosA + p.z * sinA
                    val rz = -p.x * sinA + p.z * cosA
                    val ry = p.y
                    val perspective = cameraDist / (cameraDist - rz * 0.3f)
                    return Offset(centerX + rx * scale * perspective, centerY + ry * scale * perspective)
                }

                // Head Circle
                val headOffset = project(head)
                drawCircle(
                    color = Color(0xFFF8FAFC),
                    radius = scale * 0.16f,
                    center = headOffset,
                    style = Stroke(width = 2.dp.toPx())
                )

                // 3D Skeleton Bones
                bones.forEach { (p1, p2) ->
                    val o1 = project(p1)
                    val o2 = project(p2)

                    val rzAvg = (-p1.x * sinA + p1.z * cosA + -p2.x * sinA + p2.z * cosA) / 2f
                    val alpha = ((rzAvg + 1.2f) / 2.4f).coerceIn(0.25f, 0.95f)

                    drawLine(
                        color = Color(0xFFF5F6F8).copy(alpha = alpha),
                        start = o1,
                        end = o2,
                        strokeWidth = 2.2.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    drawCircle(
                        color = Color(0xFFFFFFFF).copy(alpha = alpha),
                        radius = 2.5.dp.toPx(),
                        center = o1
                    )
                    drawCircle(
                        color = Color(0xFFFFFFFF).copy(alpha = alpha),
                        radius = 2.5.dp.toPx(),
                        center = o2
                    )
                }

                // Highlight Selected Muscle Anchor Node
                val selProj = project(selectedMuscle.anchor3D)
                drawCircle(
                    color = Color(0xFFFFFFFF),
                    radius = 8.dp.toPx(),
                    center = selProj
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFFFFF).copy(alpha = 0.6f), Color.Transparent),
                        center = selProj,
                        radius = 20.dp.toPx()
                    ),
                    radius = 20.dp.toPx(),
                    center = selProj
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 14 Muscle Selector Chips
        Text(text = "Select Muscle Group (14 Anatomical Areas):", color = TextGray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ALL_MUSCLE_GROUPS) { muscle ->
                val isSelected = muscle.id == selectedMuscle.id
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) TextWhite else CardDarkElevated,
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) CardBorderActive else CardBorderWhite,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onMuscleSelect(muscle) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = muscle.name,
                        color = if (isSelected) CardDark else TextSilver,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
