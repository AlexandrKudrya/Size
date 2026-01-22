package com.example.sizetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sizetracker.data.entity.CalorieEntry
import com.example.sizetracker.data.entity.WeightEntry
import com.example.sizetracker.ui.viewmodel.SizeTrackerViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.core.scroll.Scroll
import java.text.SimpleDateFormat
import java.util.*

enum class Period(val label: String, val days: Int?) {
    WEEK("7д", 7),
    MONTH("30д", 30),
    THREE_MONTHS("90д", 90),
    ALL("Всё", null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: SizeTrackerViewModel
) {
    var selectedPeriod by remember { mutableStateOf(Period.WEEK) }
    val scrollState = rememberScrollState()

    val weightEntries by viewModel.getFilteredWeightEntries(selectedPeriod.days).collectAsState(initial = emptyList())
    val calorieEntries by viewModel.getFilteredCalorieEntries(selectedPeriod.days).collectAsState(initial = emptyList())
    val waterEntries by viewModel.allWaterEntries.collectAsState()
    val sleepEntries by viewModel.allSleepEntries.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val weeklyChange by viewModel.getWeeklyWeightChange().collectAsState(initial = 0f)
    val weightTrend by viewModel.getWeightTrend().collectAsState(initial = "stable")
    val avgCalories by viewModel.getAverageCalories(selectedPeriod.days).collectAsState(initial = 0)
    val daysOverLimit by viewModel.getDaysOverCalorieLimit(selectedPeriod.days).collectAsState(initial = 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "📊 Статистика",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Period Filter
        PeriodFilterRow(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = { selectedPeriod = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Weight Section
        WeightStatisticsSection(
            weightEntries = weightEntries,
            targetWeight = userProfile?.targetWeight ?: 0f,
            weeklyChange = weeklyChange,
            trend = weightTrend
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Calories Section
        CalorieStatisticsSection(
            calorieEntries = calorieEntries,
            dailyLimit = userProfile?.dailyCalorieLimit ?: 2000,
            avgCalories = avgCalories,
            daysOverLimit = daysOverLimit
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Water Section
        WaterStatisticsSection(
            waterEntries = waterEntries,
            dailyGoal = 2000
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sleep Section
        SleepStatisticsSection(
            sleepEntries = sleepEntries
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodFilterRow(
    selectedPeriod: Period,
    onPeriodSelected: (Period) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Period.entries.forEach { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = { Text(period.label) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun WeightStatisticsSection(
    weightEntries: List<WeightEntry>,
    targetWeight: Float,
    weeklyChange: Float,
    trend: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Вес",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (weightEntries.isNotEmpty()) {
            // Weight Chart
            WeightLineChart(
                entries = weightEntries,
                targetWeight = targetWeight
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weight Statistics Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Weekly Change Card
                Card(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (weeklyChange < 0) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = if (weeklyChange < 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "%.1f кг".format(kotlin.math.abs(weeklyChange)),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "за неделю",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Trend Card
                Card(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (trend) {
                                "down" -> "📉"
                                "up" -> "📈"
                                else -> "➡️"
                            },
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (trend) {
                                "down" -> "Снижение"
                                "up" -> "Рост"
                                else -> "Стабильно"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "тренд",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет данных за выбранный период",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CalorieStatisticsSection(
    calorieEntries: List<CalorieEntry>,
    dailyLimit: Int,
    avgCalories: Int,
    daysOverLimit: Int
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Калории",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (calorieEntries.isNotEmpty()) {
            // Calorie Chart
            CalorieColumnChart(
                entries = calorieEntries,
                dailyLimit = dailyLimit
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calorie Statistics Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Average Calories Card
                Card(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$avgCalories",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ккал/день",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "в среднем",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Days Over Limit Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (daysOverLimit > 0)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$daysOverLimit",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "дней",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "превышение",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет данных за выбранный период",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun WeightLineChart(
    entries: List<WeightEntry>,
    targetWeight: Float
) {
    if (entries.isEmpty()) return

    val sortedEntries = entries.sortedBy { it.timestamp }

    // Main weight data
    val weightEntries = sortedEntries.mapIndexed { index, entry ->
        entryOf(index.toFloat(), entry.weight)
    }

    // Goal line (horizontal line at target weight)
    val goalEntries = if (targetWeight > 0) {
        sortedEntries.indices.map { index ->
            entryOf(index.toFloat(), targetWeight)
        }
    } else {
        emptyList()
    }

    val scrollState = rememberChartScrollState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            ProvideChartStyle(m3ChartStyle()) {
                Chart(
                    chart = lineChart(),
                    model = if (goalEntries.isNotEmpty()) {
                        entryModelOf(weightEntries, goalEntries)
                    } else {
                        entryModelOf(weightEntries)
                    },
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                    chartScrollState = scrollState,
                    isZoomEnabled = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun CalorieColumnChart(
    entries: List<CalorieEntry>,
    dailyLimit: Int
) {
    if (entries.isEmpty()) return

    // Group by date and sum calories
    val dailyTotals = entries
        .groupBy { it.date }
        .mapValues { (_, dayEntries) -> dayEntries.sumOf { it.calories } }
        .toList()
        .sortedBy { it.first }
        .takeLast(7) // Last 7 days

    val chartEntries = dailyTotals.mapIndexed { index, (_, calories) ->
        entryOf(index.toFloat(), calories.toFloat())
    }

    val scrollState = rememberChartScrollState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            ProvideChartStyle(m3ChartStyle()) {
                Chart(
                    chart = columnChart(),
                    model = entryModelOf(chartEntries),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                    chartScrollState = scrollState,
                    isZoomEnabled = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun WaterStatisticsSection(
    waterEntries: List<com.example.sizetracker.data.entity.WaterEntry>,
    dailyGoal: Int
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "💧 Вода",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (waterEntries.isNotEmpty()) {
            // Group by date and sum
            val dailyTotals = waterEntries
                .groupBy { it.date }
                .mapValues { (_, entries) -> entries.sumOf { it.milliliters } }
                .toList()
                .sortedBy { it.first }
                .takeLast(7)

            // Water Chart
            WaterColumnChart(dailyTotals = dailyTotals, dailyGoal = dailyGoal)

            Spacer(modifier = Modifier.height(16.dp))

            // Statistics
            val avgWater = if (dailyTotals.isNotEmpty()) {
                dailyTotals.map { it.second }.average().toInt()
            } else 0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$avgWater мл",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "в среднем",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (avgWater >= dailyGoal)
                            MaterialTheme.colorScheme.tertiaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (avgWater >= dailyGoal) "✓" else "✗",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (avgWater >= dailyGoal) "Цель" else "Меньше нормы",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет записей о воде",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SleepStatisticsSection(
    sleepEntries: List<com.example.sizetracker.data.entity.SleepEntry>
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "😴 Сон",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (sleepEntries.isNotEmpty()) {
            val recentEntries = sleepEntries
                .sortedBy { it.timestamp }
                .takeLast(7)

            // Sleep Chart
            SleepLineChart(entries = recentEntries)

            Spacer(modifier = Modifier.height(16.dp))

            // Statistics
            val avgHours = recentEntries.map { it.hours }.average().toFloat()
            val avgQuality = recentEntries.map { it.quality }.average().toFloat()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "%.1f ч".format(avgHours),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "в среднем",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row {
                            repeat(5) { index ->
                                Text(
                                    text = if (index < avgQuality.toInt()) "⭐" else "☆",
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Text(
                            text = "качество",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет записей о сне",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun WaterColumnChart(
    dailyTotals: List<Pair<String, Int>>,
    dailyGoal: Int
) {
    val chartEntries = dailyTotals.mapIndexed { index, (_, water) ->
        entryOf(index.toFloat(), water.toFloat())
    }

    val scrollState = rememberChartScrollState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            ProvideChartStyle(m3ChartStyle()) {
                Chart(
                    chart = columnChart(),
                    model = entryModelOf(chartEntries),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                    chartScrollState = scrollState,
                    isZoomEnabled = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun SleepLineChart(
    entries: List<com.example.sizetracker.data.entity.SleepEntry>
) {
    val chartEntries = entries.mapIndexed { index, entry ->
        entryOf(index.toFloat(), entry.hours)
    }

    val scrollState = rememberChartScrollState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            ProvideChartStyle(m3ChartStyle()) {
                Chart(
                    chart = lineChart(),
                    model = entryModelOf(chartEntries),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(),
                    chartScrollState = scrollState,
                    isZoomEnabled = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
