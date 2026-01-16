package com.example.sizetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val weight: Float,
    val date: String, // Format: "yyyy-MM-dd"
    val timestamp: Long = System.currentTimeMillis()
)
