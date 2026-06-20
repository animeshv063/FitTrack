package com.example.fittrack.navigation

sealed class Routes(
    val route: String
) {

    object Home : Routes(
        "home"
    )

    object Workout : Routes(
        "workout"
    )

    object Progress : Routes(
        "progress"
    )

    object Profile : Routes(
        "profile"
    )

    object WorkoutDetail : Routes(
        "workout_detail/{id}"
    )
}

