package com.example.fittrack.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.components.ALL_MUSCLE_GROUPS
import com.example.fittrack.presentation.components.GlowingBackground
import com.example.fittrack.presentation.components.MuscleGroupData
import com.example.fittrack.presentation.components.RotatingHumanBodyCard
import com.example.fittrack.presentation.theme.CardBorderActive
import com.example.fittrack.presentation.theme.CardBorderWhite
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.CardDarkElevated
import com.example.fittrack.presentation.theme.DangerRed
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextSilver
import com.example.fittrack.presentation.theme.TextWhite

@Composable
fun AnatomyScreen() {
    var selectedMuscle by remember { mutableStateOf<MuscleGroupData>(ALL_MUSCLE_GROUPS[0]) }

    GlowingBackground {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 30 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Screen Title & Subtitle
                Text(
                    text = "Anatomy & Nutrition",
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "3D Targeted Exercises & Hypertrophy Meal Blueprint",
                    color = TextGray,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive 3D Wireframe Mannequin Component
                RotatingHumanBodyCard(
                    selectedMuscle = selectedMuscle,
                    onMuscleSelect = { selectedMuscle = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Selected Muscle Details & Targeted Exercise Recommendations
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(CardDarkElevated, CircleShape)
                                    .border(1.dp, CardBorderActive.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.FitnessCenter,
                                    contentDescription = "Target",
                                    tint = TextWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = selectedMuscle.name,
                                    color = TextWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = selectedMuscle.anatomicalTerm,
                                    color = TextSilver,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Biomechanical Function:",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = selectedMuscle.functionDescription,
                        color = TextSilver,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Top Targeted Exercises:",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    selectedMuscle.bestExercises.forEachIndexed { index, exercise ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .background(CardDarkElevated, RoundedCornerShape(12.dp))
                                .border(1.dp, CardBorderWhite, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(TextWhite, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = CardDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = exercise,
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Complete Nutrition & Diet Guide Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(CardDarkElevated, CircleShape)
                                .border(1.dp, CardBorderActive.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Restaurant,
                                contentDescription = "Diet",
                                tint = TextWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Nutrition & Macro Blueprint",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // What to Eat (Superfoods)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Eat",
                            tint = TextWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Superfoods & Clean Fuel (What to Eat)",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val foodsToEat = listOf(
                        "Lean Protein: Chicken breast, egg whites, wild salmon, turkey",
                        "Slow Carbs: Rolled oats, sweet potatoes, brown rice, quinoa",
                        "Healthy Fats: Avocados, extra virgin olive oil, almonds, walnuts",
                        "Recovery & Dairy: Whey protein isolate, Greek yogurt, cottage cheese",
                        "Hydration: 3.5L+ Pure Water, Coconut water for electrolytes"
                    )

                    foodsToEat.forEach { food ->
                        Text(
                            text = "• $food",
                            color = TextSilver,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // What to Avoid
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Block,
                            contentDescription = "Avoid",
                            tint = DangerRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Anti-Inflammatory Rules (What to Avoid)",
                            color = DangerRed,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val foodsToAvoid = listOf(
                        "Refined Sugars: High-fructose corn syrup, sugary sodas, energy drinks",
                        "Ultra-Processed Oils: Hydrogenated seed oils, deep-fried fast food",
                        "Excessive Alcohol: Inhibits muscle protein synthesis & recovery",
                        "Empty Carbs: Processed white bread, pastries, candy bars"
                    )

                    foodsToAvoid.forEach { avoid ->
                        Text(
                            text = "✕ $avoid",
                            color = TextSilver,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pre/Post Workout Fueling Rules
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.LocalDining,
                            contentDescription = "Timing",
                            tint = TextWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pre & Post Workout Fueling",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Pre-Workout (60 min before): 30g complex carbs + 25g lean protein (e.g. Oatmeal & egg whites) to maximize glycogen stores.",
                        color = TextSilver,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Post-Workout (Within 45 min): 30-40g fast whey protein + 1 banana for rapid muscle repair and glycogen replenishment.",
                        color = TextSilver,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }
}
