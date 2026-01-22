package com.example.sizetracker.data.dao

import androidx.room.*
import com.example.sizetracker.data.entity.WaterEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterEntryDao {
    @Query("SELECT * FROM water_entries ORDER BY timestamp DESC")
    fun getAllWaterEntries(): Flow<List<WaterEntry>>

    @Query("SELECT * FROM water_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getWaterEntriesByDate(date: String): Flow<List<WaterEntry>>

    @Query("SELECT SUM(milliliters) FROM water_entries WHERE date = :date")
    fun getTotalWaterByDate(date: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterEntry(entry: WaterEntry)

    @Delete
    suspend fun deleteWaterEntry(entry: WaterEntry)
}
