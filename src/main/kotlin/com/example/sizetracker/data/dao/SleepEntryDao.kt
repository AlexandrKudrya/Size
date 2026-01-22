package com.example.sizetracker.data.dao

import androidx.room.*
import com.example.sizetracker.data.entity.SleepEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepEntryDao {
    @Query("SELECT * FROM sleep_entries ORDER BY timestamp DESC")
    fun getAllSleepEntries(): Flow<List<SleepEntry>>

    @Query("SELECT * FROM sleep_entries WHERE date = :date ORDER BY timestamp DESC LIMIT 1")
    fun getSleepEntryByDate(date: String): Flow<SleepEntry?>

    @Query("SELECT AVG(hours) FROM sleep_entries WHERE date >= :startDate")
    fun getAverageSleepHours(startDate: String): Flow<Float?>

    @Query("SELECT AVG(quality) FROM sleep_entries WHERE date >= :startDate")
    fun getAverageSleepQuality(startDate: String): Flow<Float?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepEntry(entry: SleepEntry)

    @Delete
    suspend fun deleteSleepEntry(entry: SleepEntry)
}
