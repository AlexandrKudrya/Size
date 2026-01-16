package com.example.sizetracker.data.dao

import androidx.room.*
import com.example.sizetracker.data.entity.CalorieEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface CalorieEntryDao {
    @Query("SELECT * FROM calorie_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getCalorieEntriesByDate(date: String): Flow<List<CalorieEntry>>

    @Query("SELECT SUM(calories) FROM calorie_entries WHERE date = :date")
    fun getTotalCaloriesByDate(date: String): Flow<Int?>

    @Query("SELECT * FROM calorie_entries ORDER BY timestamp DESC")
    fun getAllCalorieEntries(): Flow<List<CalorieEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calorieEntry: CalorieEntry)

    @Update
    suspend fun update(calorieEntry: CalorieEntry)

    @Delete
    suspend fun delete(calorieEntry: CalorieEntry)
}
