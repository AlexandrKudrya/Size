package com.example.sizetracker.data.repository

import com.example.sizetracker.data.dao.CalorieEntryDao
import com.example.sizetracker.data.dao.UserProfileDao
import com.example.sizetracker.data.dao.WeightEntryDao
import com.example.sizetracker.data.entity.CalorieEntry
import com.example.sizetracker.data.entity.UserProfile
import com.example.sizetracker.data.entity.WeightEntry
import kotlinx.coroutines.flow.Flow

class SizeTrackerRepository(
    private val userProfileDao: UserProfileDao,
    private val weightEntryDao: WeightEntryDao,
    private val calorieEntryDao: CalorieEntryDao
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
}
