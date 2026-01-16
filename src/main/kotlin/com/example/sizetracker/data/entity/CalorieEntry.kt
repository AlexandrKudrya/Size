package com.example.sizetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calorie_entries")
data class CalorieEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val calories: Int,
    val date: String, // Format: "yyyy-MM-dd"
    val timestamp: Long = System.currentTimeMillis()
)
