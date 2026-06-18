package com.example.fittrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.navigation.Routes
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.PrimaryGreen
import com.example.fittrack.presentation.theme.TextGray

@Composable
fun BottomNavBar(
    navController: NavController
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "🏠 Home",
            color = PrimaryGreen,
            modifier = Modifier.clickable {

                navController.navigate(
                    Routes.Home.route
                ) {
                    launchSingleTop = true
                }

            }
        )

        Text(
            text = "💪 Workout",
            color = TextGray,
            modifier = Modifier.clickable {

                navController.navigate(
                    Routes.Workout.route
                ) {
                    launchSingleTop = true
                }

            }
        )

        Text(
            text = "📊 Progress",
            color = TextGray,
            modifier = Modifier.clickable {

                navController.navigate(
                    Routes.Progress.route
                ) {
                    launchSingleTop = true
                }

            }
        )

        Text(
            text = "👤 Profile",
            color = TextGray,
            modifier = Modifier.clickable {

                navController.navigate(
                    Routes.Profile.route
                ) {
                    launchSingleTop = true
                }

            }
        )
    }
}