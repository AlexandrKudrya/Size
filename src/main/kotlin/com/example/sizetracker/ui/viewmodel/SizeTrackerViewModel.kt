package com.example.sizetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sizetracker.data.entity.CalorieEntry
import com.example.sizetracker.data.entity.UserProfile
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
