package com.example.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val targetValue: Double,
    val currentValue: Double = 0.0,
    val unit: String = "",
    val category: String = "Fitness",
    val isCompleted: Boolean = false
)
