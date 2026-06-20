package com.example.fittrack.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fittrack.presentation.components.BottomNavBar
import com.example.fittrack.presentation.screens.HomeScreen
import com.example.fittrack.presentation.screens.WorkoutScreen
import com.example.fittrack.presentation.screens.ProgressScreen
import com.example.fittrack.presentation.screens.ProfileScreen
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.fittrack.presentation.screens.WorkoutDetailScreen

@Composable
fun AppNavigation(
    workoutViewModel: WorkoutViewModel
) {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier.padding(innerPadding)
        ) {

            NavHost(
                navController = navController,
                startDestination = Routes.Home.route
            ) {

                composable(
                    route = Routes.Home.route
                ) {
                    HomeScreen()
                }

                composable(
                    route = Routes.Workout.route
                ) {
                    WorkoutScreen(
                        viewModel = workoutViewModel,
                        navController = navController

                    )
                }

                composable(
                    route = Routes.Progress.route
                ) {
                    ProgressScreen()
                }

                composable(
                    route = Routes.Profile.route
                ) {
                    ProfileScreen()
                }
                composable(
                    route = Routes.WorkoutDetail.route,

                    arguments = listOf(
                        navArgument("id") {
                            type = NavType.IntType
                        }
                    )

                ) { backStackEntry ->


                    val workoutId =
                        backStackEntry
                            .arguments
                            ?.getInt("id")
                            ?: 0


                    WorkoutDetailScreen(
                        workoutId = workoutId,
                        viewModel = workoutViewModel
                    )

                }
            }
        }
    }
}