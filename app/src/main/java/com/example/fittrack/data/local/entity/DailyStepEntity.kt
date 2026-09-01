package com.example.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DailyStepEntity
 * Persistent daily step record stored in SQLite Room database.
 * Keyed by date string ("yyyy-MM-dd") to guarantee no step history is ever lost across days or app reboots.
 */
@Entity(tableName = "daily_steps")
data class DailyStepEntity(
    @PrimaryKey
    val date: String, // Format: "yyyy-MM-dd"
    val steps: Int = 0,
    val calories: Int = 0,
    val distanceKm: Double = 0.0,
    val updatedAt: Long = System.currentTimeMillis()
)
