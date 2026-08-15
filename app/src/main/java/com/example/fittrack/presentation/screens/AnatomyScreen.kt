package com.example.fittrack.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Egg
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

data class DietPlanData(
    val title: String,
    val description: String,
    val proteinSources: List<String>,
    val carbSources: List<String>,
    val fatSources: List<String>,
    val preWorkout: String,
    val postWorkout: String,
    val sampleMealPlan: List<Pair<String, String>>,
    val foodsToAvoid: List<String>
)

val DIET_PLANS = mapOf(
    "Vegetarian" to DietPlanData(
        title = "🥗 Global Vegetarian Blueprint",
        description = "High-performance global plant and dairy nutrition standard. Complete essential amino acid profiles (EAAs) tailored for lean muscle growth, strength, and rapid athletic recovery.",
        proteinSources = listOf(
            "Greek Yogurt & Skyr - 15g-20g protein / 100g (Rich in Casein & BCAAs)",
            "Organic Firm Tofu & Tempeh - 15g-20g protein / 100g",
            "Low-Fat Cottage Cheese / Quark - 14g-18g protein / 100g",
            "Seitan (Wheat Gluten) - 75g protein / 100g (Highest Plant Density)",
            "Shelled Edamame & Green Peas - 12g-18g protein / 100g",
            "Black Beans, Chickpeas & Lentils - 20g-25g protein / 100g",
            "Hemp Hearts & Chia Seeds - 10g protein / 3 tbsp (Complete Aminos + Omega-3)",
            "Whey Isolate / Pea & Brown Rice Protein Blend - 25g/scoop"
        ),
        carbSources = listOf(
            "Steel-Cut / Rolled Oats with Cinnamon & Chia",
            "Sprouted Grain Ezekiel & Whole Wheat Sourdough Bread",
            "Quinoa, Brown Rice & Baked Sweet Potatoes",
            "Blueberries, Bananas & Crisp Apples"
        ),
        fatSources = listOf(
            "Fresh Avocados & Cold-Pressed Extra Virgin Olive Oil",
            "Raw Almonds, Walnuts, Pumpkin Seeds & Flaxseeds",
            "Natural Peanut Butter & Almond Butter (No added sugar/palm oil)"
        ),
        preWorkout = "1 Banana with 1 tbsp natural peanut butter + black coffee or oatmeal with almond milk (60 min before training).",
        postWorkout = "1 Scoop Whey or Plant Protein Isolate + 1 baked sweet potato or banana with a handful of raw almonds.",
        sampleMealPlan = listOf(
            "Breakfast" to "High-protein oatmeal with protein powder, chia seeds, sliced bananas, berries and crushed walnuts",
            "Mid-Morning" to "Greek yogurt or Skyr bowl topped with mixed berries, hemp hearts and pumpkin seeds",
            "Lunch" to "Mediterranean warm quinoa bowl with grilled tofu, roasted chickpeas, bell peppers, cucumber and olive oil tahini dressing",
            "Pre-Workout" to "1 Banana + Black coffee / green tea + 1 sprouted grain toast with almond butter",
            "Post-Workout" to "Whey or pea-rice isolate shake with almond milk + 1 medium sweet potato",
            "Dinner" to "High-protein seitan / tempeh & vegetable stir-fry with broccoli, edamame, mushrooms and brown rice"
        ),
        foodsToAvoid = listOf(
            "Ultra-processed mock meats high in sodium and artificial binders",
            "Refined white sugar, pastries, candy and sweetened syrups",
            "Hydrogenated trans fats, palm oils and deep-fried fast food"
        )
    ),
    "Non-Vegetarian" to DietPlanData(
        title = "🍗 Non-Vegetarian Nutrition Blueprint",
        description = "Full-spectrum animal and seafood protein blueprint including poultry, wild seafood, and lean cuts for maximum muscle synthesis.",
        proteinSources = listOf(
            "Grilled Chicken Breast (Skinless) - 31g protein / 100g",
            "Wild Alaskan Salmon & Tuna (Omega-3 EPA/DHA) - 25g protein / 100g",
            "Whole Free-Range Eggs & Egg Whites - 6g protein / egg",
            "Lean Beef / Sirloin / Tenderloin - 28g protein / 100g",
            "Turkey Breast & Lean Ground Turkey - 29g protein / 100g",
            "Shrimp, Prawns & White Cod - 24g protein / 100g"
        ),
        carbSources = listOf(
            "Jasmine Rice, Brown Rice, Sweet Potatoes & Quinoa",
            "Whole Rolled Oats & Whole Grain Sourdough Bread"
        ),
        fatSources = listOf(
            "Fresh Avocados, Extra Virgin Cold-Pressed Olive Oil, Macadamia Nuts"
        ),
        preWorkout = "150g Grilled chicken breast with white jasmine rice & sea salt (90 min before training).",
        postWorkout = "1 Scoop Whey Isolate + 1 Banana or 4 Boiled egg whites + rice cakes.",
        sampleMealPlan = listOf(
            "Breakfast" to "3 Whole eggs + 2 egg whites scrambled with spinach, whole grain sourdough toast & black coffee",
            "Mid-Morning" to "Greek yogurt with mixed berries & pumpkin seeds",
            "Lunch" to "200g Grilled chicken breast + Brown rice + Steamed broccoli with extra virgin olive oil",
            "Pre-Workout" to "Rice cakes with natural peanut butter + 1 Banana",
            "Post-Workout" to "Whey protein isolate shake + 1 Apple",
            "Dinner" to "200g Pan-seared Salmon / Lean beef steak + Baked sweet potato + Grilled asparagus"
        ),
        foodsToAvoid = listOf(
            "Processed deli meats (salami, hot dogs with nitrates)",
            "Deep-fried fast foods in hydrogenated oils",
            "High-fructose corn syrup marinades and sugary BBQ sauces"
        )
    ),
    "Hybrid" to DietPlanData(
        title = "🍳 Hybrid (Eggs & Seafood / Poultry) Blueprint",
        description = "Flexible nutrition plan combining nutrient-dense plant foods with whole eggs, wild seafood, and lean poultry for clean energy.",
        proteinSources = listOf(
            "Whole Free-Range Eggs & Fluffy Egg White Omelettes - 6g protein / egg",
            "Grilled Skinless Chicken Breast - 31g protein / 100g",
            "Wild Salmon, Tuna & White Fish Fillet - 24g protein / 100g",
            "Tofu, Tempeh & Cottage Cheese - 15g-18g protein / 100g",
            "Lentils, Edamame, Chickpeas & Black Beans - 20g-25g protein / 100g",
            "Whey Protein Isolate - 25g/scoop"
        ),
        carbSources = listOf(
            "Whole Grain Sourdough & Multigrain Bread",
            "Rolled Oats, Sweet Potatoes & Brown Rice"
        ),
        fatSources = listOf(
            "Whole Egg Yolks, Raw Almonds, Walnuts, Cold-pressed Olive Oil & Chia Seeds"
        ),
        preWorkout = "2 Boiled eggs or 1 scoop whey protein + 1 banana (60 min before training).",
        postWorkout = "150g Grilled chicken breast / 4 Egg whites + Brown rice & garden salad.",
        sampleMealPlan = listOf(
            "Breakfast" to "3 Egg white & 1 whole egg scramble with spinach, avocado and whole grain toast",
            "Mid-Morning" to "Greek yogurt with raw almonds and sliced orange",
            "Lunch" to "Grilled chicken breast or salmon fillet + brown rice + cucumber-tomato salad",
            "Pre-Workout" to "Oatmeal with sliced banana and natural peanut butter",
            "Post-Workout" to "Whey protein shake + 1 baked sweet potato",
            "Dinner" to "Grilled fish or sautéed tofu with steamed vegetables, quinoa and lentil soup"
        ),
        foodsToAvoid = listOf(
            "Heavily battered and deep-fried dishes",
            "Sugary sodas, energy drinks & refined white flour",
            "Ultra-processed packaged snacks and trans-fats"
        )
    )
)

@Composable
fun AnatomyScreen() {
    var selectedMuscle by remember { mutableStateOf<MuscleGroupData>(ALL_MUSCLE_GROUPS[0]) }
    var selectedDietTab by remember { mutableStateOf("Vegetarian") }

    val currentDiet = DIET_PLANS[selectedDietTab] ?: DIET_PLANS["Vegetarian"]!!

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
                // Header
                Text(
                    text = "ANATOMY & NUTRITION",
                    color = TextGray,
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Hypertrophy & Fueling",
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 3D Body Mannequin Card
                RotatingHumanBodyCard(
                    selectedMuscle = selectedMuscle,
                    onMuscleSelect = { selectedMuscle = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                val muscleColor = when (selectedMuscle.id) {
                    "chest" -> Color(0xFF00F2FE)       // Electric Cyan
                    "back" -> Color(0xFFA855F7)        // Electric Purple
                    "legs" -> Color(0xFF00FFA3)        // Neon Mint
                    "shoulders" -> Color(0xFFFF6B00)   // Flame Orange
                    "arms" -> Color(0xFFFBBF24)        // Golden Amber
                    else -> Color(0xFFEC4899)          // Hot Pink
                }

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
                                    .border(1.dp, muscleColor.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.FitnessCenter,
                                    contentDescription = "Target",
                                    tint = muscleColor,
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
                                    color = muscleColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
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
                        text = "Targeted Hypertrophy Exercises:",
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
                                    .size(22.dp)
                                    .background(muscleColor.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = muscleColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = exercise,
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Global Athlete Nutrition Blueprint Section
                Text(
                    text = "ATHLETIC NUTRITION & FUELING",
                    color = TextGray,
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Clean Fueling Blueprints",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tailored macronutrient distributions for optimal muscle protein synthesis and endurance recovery.",
                    color = TextGray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Diet Selector Chips
                val dietTabsList = listOf(
                    Triple("Vegetarian", "Vegetarian", Icons.Rounded.Spa),
                    Triple("Non-Vegetarian", "Non-Vegetarian", Icons.Rounded.Restaurant),
                    Triple("Hybrid", "Hybrid / Flexitarian", Icons.Rounded.Egg)
                )

                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dietTabsList.size) { idx ->
                        val (dietKey, label, icon) = dietTabsList[idx]
                        val isSelected = selectedDietTab == dietKey
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) Color(0xFF00FFA3).copy(alpha = 0.15f) else CardDark,
                                    RoundedCornerShape(14.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF00FFA3) else CardBorderWhite,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedDietTab = dietKey }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFF00FFA3) else TextSilver,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    color = if (isSelected) Color(0xFF00FFA3) else TextSilver,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detailed Diet Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderWhite, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Text(
                        text = currentDiet.title,
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentDiet.description,
                        color = TextSilver,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // High Protein Clean Sources
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Proteins",
                            tint = TextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "High-Protein Superfoods",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    currentDiet.proteinSources.forEach { protein ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .background(CardDarkElevated, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "•", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = protein, color = TextSilver, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pre & Post Workout Fueling
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.LocalDining,
                            contentDescription = "Fueling",
                            tint = TextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pre & Post Workout Fueling",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDarkElevated, RoundedCornerShape(12.dp))
                            .border(1.dp, CardBorderWhite, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(text = "⚡ Pre-Workout (60-90m before):", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = currentDiet.preWorkout, color = TextSilver, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp, bottom = 8.dp))

                        Text(text = "💪 Post-Workout (Within 45m):", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = currentDiet.postWorkout, color = TextSilver, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sample Daily Meal Plan
                    Text(
                        text = "Sample Daily Meal Blueprint:",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    currentDiet.sampleMealPlan.forEach { (mealName, mealContent) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .background(CardDarkElevated, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(text = mealName, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = mealContent, color = TextSilver, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Foods to Avoid
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Block,
                            contentDescription = "Avoid",
                            tint = DangerRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Foods & Habits to Minimize",
                            color = DangerRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    currentDiet.foodsToAvoid.forEach { avoid ->
                        Text(
                            text = "✕ $avoid",
                            color = TextSilver,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(110.dp))
            }
        }
    }
}
