package com.example.fittrack.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fittrack.presentation.components.BottomNavBar
import com.example.fittrack.presentation.screens.AnatomyScreen
import com.example.fittrack.presentation.screens.HomeScreen
import com.example.fittrack.presentation.screens.ProfileScreen
import com.example.fittrack.presentation.screens.ProgressScreen
import com.example.fittrack.presentation.screens.WorkoutDetailScreen
import com.example.fittrack.presentation.screens.WorkoutScreen
import com.example.fittrack.presentation.theme.BackgroundDark
import com.example.fittrack.presentation.viewmodel.WorkoutViewModel

@Composable
fun AppNavigation(
    workoutViewModel: WorkoutViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = {
            BottomNavBar(
                navController = navController
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundDark)
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.Home.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { 300 }) },
                exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -300 }) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -300 }) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { 300 }) }
            ) {
                composable(route = Routes.Home.route) {
                    HomeScreen(
                        navController = navController,
                        viewModel = workoutViewModel
                    )
                }

                composable(route = Routes.Workout.route) {
                    WorkoutScreen(
                        viewModel = workoutViewModel,
                        navController = navController
                    )
                }

                composable(route = Routes.Progress.route) {
                    ProgressScreen(
                        viewModel = workoutViewModel
                    )
                }

                composable(route = Routes.Anatomy.route) {
                    AnatomyScreen()
                }

                composable(route = Routes.Profile.route) {
                    ProfileScreen(
                        viewModel = workoutViewModel
                    )
                }

                composable(
                    route = Routes.WorkoutDetail.route,
                    arguments = listOf(
                        navArgument("id") {
                            type = NavType.IntType
                        }
                    )
                ) { backStackEntry ->
                    val workoutId = backStackEntry.arguments?.getInt("id") ?: 0
                    WorkoutDetailScreen(
                        workoutId = workoutId,
                        viewModel = workoutViewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}