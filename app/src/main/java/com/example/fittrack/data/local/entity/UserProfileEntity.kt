package com.example.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "Athlete",
    val gender: String = "Male",
    val profileImageUri: String? = null,
    val age: Int = 0,
    val weightKg: Float = 0f,
    val heightCm: Float = 0f,
    val stepGoal: Int = 10000,
    val waterGoalMl: Int = 3000,
    val calorieGoal: Int = 2500
)
