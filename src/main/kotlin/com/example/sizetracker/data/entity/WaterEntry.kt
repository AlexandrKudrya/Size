package com.example.sizetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_entries")
data class WaterEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val milliliters: Int, // Amount of water in ml
    val date: String, // Format: "yyyy-MM-dd"
    val timestamp: Long = System.currentTimeMillis()
)
