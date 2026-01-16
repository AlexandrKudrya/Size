package com.example.sizetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val currentWeight: Float,
    val targetWeight: Float,
    val height: Int,
    val age: Int,
    val gender: String, // "male" or "female"
    val dailyCalorieLimit: Int
)
