package com.example.sizetracker.data.repository

import com.example.sizetracker.data.dao.CalorieEntryDao
import com.example.sizetracker.data.dao.SleepEntryDao
import com.example.sizetracker.data.dao.UserProfileDao
import com.example.sizetracker.data.dao.WaterEntryDao
import com.example.sizetracker.data.dao.WeightEntryDao
import com.example.sizetracker.data.entity.CalorieEntry
import com.example.sizetracker.data.entity.SleepEntry
import com.example.sizetracker.data.entity.UserProfile
import com.example.sizetracker.data.entity.WaterEntry
import com.example.sizetracker.data.entity.WeightEntry
import kotlinx.coroutines.flow.Flow

class SizeTrackerRepository(
    private val userProfileDao: UserProfileDao,
    private val weightEntryDao: WeightEntryDao,
    private val calorieEntryDao: CalorieEntryDao,
    private val waterEntryDao: WaterEntryDao,
    private val sleepEntryDao: SleepEntryDao
) {
    // User Profile operations
    fun getUserProfile(): Flow<UserProfile?> = userProfileDao.getUserProfile()

    suspend fun insertUserProfile(userProfile: UserProfile) {
        userProfileDao.insert(userProfile)
    }

    suspend fun updateUserProfile(userProfile: UserProfile) {
        userProfileDao.update(userProfile)
    }

    // Weight Entry operations
    fun getAllWeightEntries(): Flow<List<WeightEntry>> = weightEntryDao.getAllWeightEntries()

    fun getRecentWeightEntries(limit: Int = 5): Flow<List<WeightEntry>> =
        weightEntryDao.getRecentWeightEntries(limit)

    fun getLatestWeightEntry(): Flow<WeightEntry?> = weightEntryDao.getLatestWeightEntry()

    suspend fun insertWeightEntry(weightEntry: WeightEntry) {
        weightEntryDao.insert(weightEntry)
    }

    suspend fun updateWeightEntry(weightEntry: WeightEntry) {
        weightEntryDao.update(weightEntry)
    }

    suspend fun deleteWeightEntry(weightEntry: WeightEntry) {
        weightEntryDao.delete(weightEntry)
    }

    // Calorie Entry operations
    fun getCalorieEntriesByDate(date: String): Flow<List<CalorieEntry>> =
        calorieEntryDao.getCalorieEntriesByDate(date)

    fun getTotalCaloriesByDate(date: String): Flow<Int?> =
        calorieEntryDao.getTotalCaloriesByDate(date)

    fun getAllCalorieEntries(): Flow<List<CalorieEntry>> = calorieEntryDao.getAllCalorieEntries()

    suspend fun insertCalorieEntry(calorieEntry: CalorieEntry) {
        calorieEntryDao.insert(calorieEntry)
    }

    suspend fun updateCalorieEntry(calorieEntry: CalorieEntry) {
        calorieEntryDao.update(calorieEntry)
    }

    suspend fun deleteCalorieEntry(calorieEntry: CalorieEntry) {
        calorieEntryDao.delete(calorieEntry)
    }

    // Water Entry operations
    fun getAllWaterEntries(): Flow<List<WaterEntry>> = waterEntryDao.getAllWaterEntries()

    fun getWaterEntriesByDate(date: String): Flow<List<WaterEntry>> =
        waterEntryDao.getWaterEntriesByDate(date)

    fun getTotalWaterByDate(date: String): Flow<Int?> =
        waterEntryDao.getTotalWaterByDate(date)

    suspend fun insertWaterEntry(waterEntry: WaterEntry) {
        waterEntryDao.insertWaterEntry(waterEntry)
    }

    suspend fun deleteWaterEntry(waterEntry: WaterEntry) {
        waterEntryDao.deleteWaterEntry(waterEntry)
    }

    // Sleep Entry operations
    fun getAllSleepEntries(): Flow<List<SleepEntry>> = sleepEntryDao.getAllSleepEntries()

    fun getSleepEntryByDate(date: String): Flow<SleepEntry?> =
        sleepEntryDao.getSleepEntryByDate(date)

    fun getAverageSleepHours(startDate: String): Flow<Float?> =
        sleepEntryDao.getAverageSleepHours(startDate)

    fun getAverageSleepQuality(startDate: String): Flow<Float?> =
        sleepEntryDao.getAverageSleepQuality(startDate)

    suspend fun insertSleepEntry(sleepEntry: SleepEntry) {
        sleepEntryDao.insertSleepEntry(sleepEntry)
    }

    suspend fun deleteSleepEntry(sleepEntry: SleepEntry) {
        sleepEntryDao.deleteSleepEntry(sleepEntry)
    }
}
