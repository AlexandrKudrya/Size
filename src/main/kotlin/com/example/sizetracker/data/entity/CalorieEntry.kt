package com.example.sizetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calorie_entries")
data class CalorieEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val foodName: String = "", // Название еды
    val calories: Int,
    val proteins: Float = 0f, // Белки в граммах
    val fats: Float = 0f, // Жиры в граммах
    val carbs: Float = 0f, // Углеводы в граммах
    val date: String, // Format: "yyyy-MM-dd"
    val timestamp: Long = System.currentTimeMillis()
)
