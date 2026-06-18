package com.example.fittrack.domain.model

data class Workout(
    val id: Int,
    val name: String,
    val exercises: List<Exercise>,
    val duration: Int
)

