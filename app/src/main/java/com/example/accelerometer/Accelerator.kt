package com.example.accelerometer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accelerator_table")

data class Accelerator(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long  // Add this line
)