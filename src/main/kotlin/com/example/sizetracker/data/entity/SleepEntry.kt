package com.example.sizetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_entries")
data class SleepEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val hours: Float, // Hours of sleep (can be decimal like 7.5)
    val quality: Int, // Quality rating 1-5 (1=poor, 5=excellent)
    val date: String, // Format: "yyyy-MM-dd"
    val timestamp: Long = System.currentTimeMillis()
)
