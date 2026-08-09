package com.example.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",

    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(value = ["workoutId"])
    ]
)
data class ExerciseEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val workoutId: Int,

    val name: String,

    val sets: Int,

    val reps: Int,

    val weight: Int,

    val completedSets: Int = 0
)