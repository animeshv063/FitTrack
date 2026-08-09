package com.example.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)
