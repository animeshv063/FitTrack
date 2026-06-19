package com.example.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "workouts"
)
data class WorkoutEntity(

    @PrimaryKey(
        autoGenerate = true
    )
    val id: Int = 0,

    val name: String,

    val duration: Int,

    val date: Long
)

