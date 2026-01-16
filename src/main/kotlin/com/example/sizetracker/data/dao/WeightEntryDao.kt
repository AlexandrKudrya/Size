package com.example.sizetracker.data.dao

import androidx.room.*
import com.example.sizetracker.data.entity.WeightEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightEntryDao {
    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC")
    fun getAllWeightEntries(): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentWeightEntries(limit: Int = 5): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries WHERE date = :date LIMIT 1")
    fun getWeightEntryByDate(date: String): Flow<WeightEntry?>

    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC LIMIT 1")
    fun getLatestWeightEntry(): Flow<WeightEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weightEntry: WeightEntry)

    @Update
    suspend fun update(weightEntry: WeightEntry)

    @Delete
    suspend fun delete(weightEntry: WeightEntry)
}
