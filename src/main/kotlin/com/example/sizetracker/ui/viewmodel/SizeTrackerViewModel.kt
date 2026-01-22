package com.example.sizetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sizetracker.data.entity.CalorieEntry
import com.example.sizetracker.data.entity.SleepEntry
import com.example.sizetracker.data.entity.UserProfile
import com.example.sizetracker.data.entity.WaterEntry
import com.example.sizetracker.data.entity.WeightEntry
import com.example.sizetracker.data.repository.SizeTrackerRepository
import com.example.sizetracker.utils.AnalyticsUtils
import com.example.sizetracker.utils.CalorieCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SizeTrackerViewModel(
    private val repository: SizeTrackerRepository
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // User Profile
    val userProfile: StateFlow<UserProfile?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Latest Weight Entry
    val latestWeightEntry: StateFlow<WeightEntry?> = repository.getLatestWeightEntry()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Recent Weight Entries
    val recentWeightEntries: StateFlow<List<WeightEntry>> = repository.getRecentWeightEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Weight Entries
    val allWeightEntries: StateFlow<List<WeightEntry>> = repository.getAllWeightEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's calorie entries
    val todayCalorieEntries: StateFlow<List<CalorieEntry>> =
        repository.getCalorieEntriesByDate(getTodayDate())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's total calories
    val todayTotalCalories: StateFlow<Int> =
        repository.getTotalCaloriesByDate(getTodayDate())
            .map { it ?: 0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // All Calorie Entries
    val allCalorieEntries: StateFlow<List<CalorieEntry>> = repository.getAllCalorieEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's water entries
    val todayWaterEntries: StateFlow<List<WaterEntry>> =
        repository.getWaterEntriesByDate(getTodayDate())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's total water
    val todayTotalWater: StateFlow<Int> =
        repository.getTotalWaterByDate(getTodayDate())
            .map { it ?: 0 }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // All Water Entries
    val allWaterEntries: StateFlow<List<WaterEntry>> = repository.getAllWaterEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's sleep entry
    val todaySleepEntry: StateFlow<SleepEntry?> =
        repository.getSleepEntryByDate(getTodayDate())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All Sleep Entries
    val allSleepEntries: StateFlow<List<SleepEntry>> = repository.getAllSleepEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Check if onboarding is completed
    val isOnboardingCompleted: StateFlow<Boolean> = userProfile
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Save user profile from onboarding
    fun saveUserProfile(
        currentWeight: Float,
        targetWeight: Float,
        height: Int,
        age: Int,
        gender: String
    ) {
        viewModelScope.launch {
            val dailyCalories = CalorieCalculator.calculateDailyCalories(
                weight = currentWeight,
                height = height,
                age = age,
                gender = gender
            )

            val profile = UserProfile(
                currentWeight = currentWeight,
                targetWeight = targetWeight,
                height = height,
                age = age,
                gender = gender,
                dailyCalorieLimit = dailyCalories
            )

            repository.insertUserProfile(profile)

            // Also save initial weight entry
            val weightEntry = WeightEntry(
                weight = currentWeight,
                date = getTodayDate()
            )
            repository.insertWeightEntry(weightEntry)
        }
    }

    // Add weight entry
    fun addWeightEntry(weight: Float, date: String = getTodayDate()) {
        viewModelScope.launch {
            val entry = WeightEntry(
                weight = weight,
                date = date
            )
            repository.insertWeightEntry(entry)
        }
    }

    // Delete weight entry
    fun deleteWeightEntry(weightEntry: WeightEntry) {
        viewModelScope.launch {
            repository.deleteWeightEntry(weightEntry)
        }
    }

    // Add calorie entry
    fun addCalorieEntry(
        foodName: String = "",
        calories: Int,
        proteins: Float = 0f,
        fats: Float = 0f,
        carbs: Float = 0f,
        date: String = getTodayDate()
    ) {
        viewModelScope.launch {
            val entry = CalorieEntry(
                foodName = foodName,
                calories = calories,
                proteins = proteins,
                fats = fats,
                carbs = carbs,
                date = date
            )
            repository.insertCalorieEntry(entry)
        }
    }

    // Delete calorie entry
    fun deleteCalorieEntry(calorieEntry: CalorieEntry) {
        viewModelScope.launch {
            repository.deleteCalorieEntry(calorieEntry)
        }
    }

    // Get calories for a specific date
    fun getCaloriesForDate(date: String): Flow<Int> {
        return repository.getTotalCaloriesByDate(date)
            .map { it ?: 0 }
    }

    // Add water entry
    fun addWaterEntry(milliliters: Int, date: String = getTodayDate()) {
        viewModelScope.launch {
            val entry = WaterEntry(
                milliliters = milliliters,
                date = date
            )
            repository.insertWaterEntry(entry)
        }
    }

    // Delete water entry
    fun deleteWaterEntry(waterEntry: WaterEntry) {
        viewModelScope.launch {
            repository.deleteWaterEntry(waterEntry)
        }
    }

    // Get water for a specific date
    fun getWaterForDate(date: String): Flow<Int> {
        return repository.getTotalWaterByDate(date)
            .map { it ?: 0 }
    }

    // Add sleep entry
    fun addSleepEntry(hours: Float, quality: Int, date: String = getTodayDate()) {
        viewModelScope.launch {
            val entry = SleepEntry(
                hours = hours,
                quality = quality,
                date = date
            )
            repository.insertSleepEntry(entry)
        }
    }

    // Delete sleep entry
    fun deleteSleepEntry(sleepEntry: SleepEntry) {
        viewModelScope.launch {
            repository.deleteSleepEntry(sleepEntry)
        }
    }

    // Get average sleep hours
    fun getAverageSleepHours(startDate: String): Flow<Float> {
        return repository.getAverageSleepHours(startDate)
            .map { it ?: 0f }
    }

    // Get average sleep quality
    fun getAverageSleepQuality(startDate: String): Flow<Float> {
        return repository.getAverageSleepQuality(startDate)
            .map { it ?: 0f }
    }

    // Analytics methods
    fun getFilteredWeightEntries(periodDays: Int?): Flow<List<WeightEntry>> {
        return allWeightEntries.map { entries ->
            AnalyticsUtils.filterWeightEntriesByPeriod(entries, periodDays)
        }
    }

    fun getFilteredCalorieEntries(periodDays: Int?): Flow<List<CalorieEntry>> {
        return allCalorieEntries.map { entries ->
            AnalyticsUtils.filterCalorieEntriesByPeriod(entries, periodDays)
        }
    }

    fun getAverageWeight(periodDays: Int?): Flow<Float> {
        return getFilteredWeightEntries(periodDays).map { entries ->
            AnalyticsUtils.calculateAverageWeight(entries)
        }
    }

    fun getWeeklyWeightChange(): Flow<Float> {
        return allWeightEntries.map { entries ->
            AnalyticsUtils.calculateWeeklyChange(entries)
        }
    }

    fun getWeightTrend(): Flow<String> {
        return allWeightEntries.map { entries ->
            AnalyticsUtils.getWeightTrend(entries)
        }
    }

    fun getAverageCalories(periodDays: Int?): Flow<Int> {
        return getFilteredCalorieEntries(periodDays).map { entries ->
            AnalyticsUtils.calculateAverageCalories(entries)
        }
    }

    fun getDaysOverCalorieLimit(periodDays: Int?): Flow<Int> {
        return combine(
            getFilteredCalorieEntries(periodDays),
            userProfile
        ) { entries, profile ->
            profile?.let {
                AnalyticsUtils.countDaysOverLimit(entries, it.dailyCalorieLimit)
            } ?: 0
        }
    }

    fun getDailyCalorieTotals(periodDays: Int?): Flow<Map<String, Int>> {
        return getFilteredCalorieEntries(periodDays).map { entries ->
            AnalyticsUtils.getDailyCalorieTotals(entries)
        }
    }

    fun getEstimatedDaysToGoal(): Flow<Int?> {
        return combine(
            allWeightEntries,
            latestWeightEntry,
            userProfile
        ) { entries, latest, profile ->
            if (latest != null && profile != null) {
                AnalyticsUtils.estimateDaysToGoal(
                    entries,
                    latest.weight,
                    profile.targetWeight
                )
            } else null
        }
    }

    private fun getTodayDate(): String {
        return dateFormat.format(Date())
    }
}
