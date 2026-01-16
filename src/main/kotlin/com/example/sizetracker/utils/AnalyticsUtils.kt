package com.example.sizetracker.utils

import com.example.sizetracker.data.entity.CalorieEntry
import com.example.sizetracker.data.entity.WeightEntry
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

object AnalyticsUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Calculate average weight for a list of entries
     */
    fun calculateAverageWeight(entries: List<WeightEntry>): Float {
        if (entries.isEmpty()) return 0f
        return entries.map { it.weight }.average().toFloat()
    }

    /**
     * Calculate weekly weight loss/gain
     * Returns negative for weight loss, positive for weight gain
     */
    fun calculateWeeklyChange(entries: List<WeightEntry>): Float {
        if (entries.size < 2) return 0f

        val sortedEntries = entries.sortedBy { it.timestamp }
        val now = System.currentTimeMillis()
        val oneWeekAgo = now - (7 * 24 * 60 * 60 * 1000)

        val recentEntries = sortedEntries.filter { it.timestamp >= oneWeekAgo }
        if (recentEntries.size < 2) return 0f

        val firstWeight = recentEntries.first().weight
        val lastWeight = recentEntries.last().weight

        return lastWeight - firstWeight
    }

    /**
     * Get weight trend: "down", "up", or "stable"
     */
    fun getWeightTrend(entries: List<WeightEntry>): String {
        if (entries.size < 2) return "stable"

        val sortedEntries = entries.sortedBy { it.timestamp }.takeLast(7)
        if (sortedEntries.size < 2) return "stable"

        val firstWeight = sortedEntries.first().weight
        val lastWeight = sortedEntries.last().weight
        val diff = lastWeight - firstWeight

        return when {
            abs(diff) < 0.3 -> "stable"
            diff < 0 -> "down"
            else -> "up"
        }
    }

    /**
     * Calculate average daily calories for a period
     */
    fun calculateAverageCalories(entries: List<CalorieEntry>): Int {
        if (entries.isEmpty()) return 0

        val groupedByDate = entries.groupBy { it.date }
        val dailyTotals = groupedByDate.map { (_, dayEntries) ->
            dayEntries.sumOf { it.calories }
        }

        return dailyTotals.average().toInt()
    }

    /**
     * Count days where calorie limit was exceeded
     */
    fun countDaysOverLimit(entries: List<CalorieEntry>, dailyLimit: Int): Int {
        val groupedByDate = entries.groupBy { it.date }
        return groupedByDate.count { (_, dayEntries) ->
            dayEntries.sumOf { it.calories } > dailyLimit
        }
    }

    /**
     * Filter weight entries by period (in days)
     */
    fun filterWeightEntriesByPeriod(entries: List<WeightEntry>, days: Int?): List<WeightEntry> {
        if (days == null) return entries

        val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000)
        return entries.filter { it.timestamp >= cutoffTime }
    }

    /**
     * Filter calorie entries by period (in days)
     */
    fun filterCalorieEntriesByPeriod(entries: List<CalorieEntry>, days: Int?): List<CalorieEntry> {
        if (days == null) return entries

        val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000)
        return entries.filter { it.timestamp >= cutoffTime }
    }

    /**
     * Get daily calorie totals grouped by date
     */
    fun getDailyCalorieTotals(entries: List<CalorieEntry>): Map<String, Int> {
        return entries.groupBy { it.date }
            .mapValues { (_, dayEntries) -> dayEntries.sumOf { it.calories } }
    }

    /**
     * Estimate days to goal based on current trend
     * Returns null if goal is already reached or no trend data
     */
    fun estimateDaysToGoal(
        entries: List<WeightEntry>,
        currentWeight: Float,
        targetWeight: Float
    ): Int? {
        val weeklyChange = calculateWeeklyChange(entries)
        if (weeklyChange >= 0 || currentWeight <= targetWeight) return null

        val remainingWeight = currentWeight - targetWeight
        val weeksNeeded = remainingWeight / abs(weeklyChange)

        return (weeksNeeded * 7).toInt()
    }
}
