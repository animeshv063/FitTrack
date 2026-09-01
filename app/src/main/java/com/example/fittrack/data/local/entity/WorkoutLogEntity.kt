package com.example.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * WorkoutLogEntity
 * Immutable record of a completed workout session.
 * Stores duration, total volume, sets, reps, and timestamps permanently on device.
 */
@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val workoutId: Int = 0,
    val workoutName: String,
    val durationMins: Int,
    val totalVolumeKg: Float = 0f,
    val completedSets: Int = 0,
    val completedReps: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val dateString: String // Format: "yyyy-MM-dd"
)
