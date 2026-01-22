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
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import androidx.compose.ui.graphics.toArgb
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

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

    // Calculate Y-axis range: min - 3kg to max + 3kg
    val weights = sortedEntries.map { it.weight }
    val minWeight = weights.minOrNull() ?: 0f
    val maxWeight = weights.maxOrNull() ?: 0f
    val yMin = max(0f, minWeight - 3f)
    val yMax = maxWeight + 3f

    // Actual weight data
    val weightEntries = sortedEntries.mapIndexed { index, entry ->
        entryOf(index.toFloat(), entry.weight)
    }

    // Goal line (horizontal)
    val goalEntries = sortedEntries.indices.map { index ->
        entryOf(index.toFloat(), targetWeight)
    }

    // Trend line (simple linear regression)
    val trendEntries = if (sortedEntries.size >= 2) {
        val xValues = sortedEntries.indices.map { it.toFloat() }
        val yValues = weights
        val n = xValues.size
        val sumX = xValues.sum()
        val sumY = yValues.sum()
        val sumXY = xValues.zip(yValues).sumOf { (x, y) -> x * y.toDouble() }
        val sumX2 = xValues.sumOf { (it * it).toDouble() }

        val slope = ((n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)).toFloat()
        val intercept = ((sumY - slope * sumX) / n).toFloat()

        sortedEntries.indices.map { index ->
            entryOf(index.toFloat(), intercept + slope * index)
        }
    } else {
        emptyList()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = Color(0xFF2196F3), label = "Вес")
                if (targetWeight > 0) {
                    LegendItem(color = Color(0xFF4CAF50), label = "Цель", dashed = true)
                }
                if (trendEntries.isNotEmpty()) {
                    LegendItem(color = Color(0xFFFFC107), label = "Тренд", dashed = true)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ProvideChartStyle(m3ChartStyle()) {
                val model = if (targetWeight > 0 && trendEntries.isNotEmpty()) {
                    entryModelOf(weightEntries, goalEntries, trendEntries)
                } else if (targetWeight > 0) {
                    entryModelOf(weightEntries, goalEntries)
                } else {
                    entryModelOf(weightEntries)
                }

                Chart(
                    chart = lineChart(
                        lines = buildList {
                            // Main weight line (solid blue)
                            add(
                                LineChart.LineSpec(
                                    lineColor = Color(0xFF2196F3).toArgb(),
                                    lineThickness = 3.dp
                                )
                            )
                            // Goal line (dashed green)
                            if (targetWeight > 0) {
                                add(
                                    LineChart.LineSpec(
                                        lineColor = Color(0xFF4CAF50).toArgb(),
                                        lineThickness = 2.dp,
                                        lineBackgroundShader = null
                                    )
                                )
                            }
                            // Trend line (dashed yellow)
                            if (trendEntries.isNotEmpty()) {
                                add(
                                    LineChart.LineSpec(
                                        lineColor = Color(0xFFFFC107).toArgb(),
                                        lineThickness = 2.dp,
                                        lineBackgroundShader = null
                                    )
                                )
                            }
                        }
                    ),
                    model = model,
                    startAxis = rememberStartAxis(
                        valueFormatter = { value, _ -> "%.1f".format(value) }
                    ),
                    bottomAxis = rememberBottomAxis(),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String,
    dashed: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = if (dashed) 16.dp else 12.dp, height = 3.dp)
                .then(
                    if (dashed) {
                        Modifier
                    } else {
                        Modifier
                    }
                )
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(color = color)
            }
        }
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Info text about goal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Цель: $dailyLimit ккал/день",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "•",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val avgCalories = if (dailyTotals.isNotEmpty()) {
                    dailyTotals.map { it.second }.average().toInt()
                } else 0
                val avgColor = when {
                    avgCalories > dailyLimit + 200 -> MaterialTheme.colorScheme.error
                    avgCalories < dailyLimit - 200 -> Color(0xFFFFA726)
                    else -> MaterialTheme.colorScheme.tertiary
                }
                Text(
                    text = "Среднее: $avgCalories",
                    fontSize = 12.sp,
                    color = avgColor,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                ProvideChartStyle(m3ChartStyle()) {
                    Chart(
                        chart = columnChart(),
                        model = entryModelOf(chartEntries),
                        startAxis = rememberStartAxis(
                            valueFormatter = { value, _ -> "${value.toInt()}" }
                        ),
                        bottomAxis = rememberBottomAxis(),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Goal line overlay
                if (dailyLimit > 0) {
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                    ) {
                        val maxCalories = max(
                            dailyTotals.maxOfOrNull { it.second }?.toFloat() ?: dailyLimit.toFloat(),
                            dailyLimit.toFloat() * 1.2f
                        )
                        val yPosition = size.height * (1f - dailyLimit / maxCalories)

                        drawLine(
                            color = Color(0xFF9E9E9E),
                            start = androidx.compose.ui.geometry.Offset(0f, yPosition),
                            end = androidx.compose.ui.geometry.Offset(size.width, yPosition),
                            strokeWidth = 4f,
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                floatArrayOf(10f, 10f)
                            )
                        )
                    }
                }
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Info text about goal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Цель: $dailyGoal мл/день",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "•",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val avgWater = if (dailyTotals.isNotEmpty()) {
                    dailyTotals.map { it.second }.average().toInt()
                } else 0
                val avgColor = when {
                    avgWater >= dailyGoal -> MaterialTheme.colorScheme.tertiary
                    avgWater >= dailyGoal * 0.75 -> Color(0xFFFFA726)
                    else -> MaterialTheme.colorScheme.error
                }
                Text(
                    text = "Среднее: $avgWater мл",
                    fontSize = 12.sp,
                    color = avgColor,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                ProvideChartStyle(m3ChartStyle()) {
                    Chart(
                        chart = columnChart(),
                        model = entryModelOf(chartEntries),
                        startAxis = rememberStartAxis(
                            valueFormatter = { value, _ -> "${value.toInt()}" }
                        ),
                        bottomAxis = rememberBottomAxis(),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Goal line overlay
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
                ) {
                    val maxWater = max(
                        dailyTotals.maxOfOrNull { it.second }?.toFloat() ?: dailyGoal.toFloat(),
                        dailyGoal.toFloat() * 1.2f
                    )
                    val yPosition = size.height * (1f - dailyGoal / maxWater)

                    drawLine(
                        color = Color(0xFF9E9E9E),
                        start = androidx.compose.ui.geometry.Offset(0f, yPosition),
                        end = androidx.compose.ui.geometry.Offset(size.width, yPosition),
                        strokeWidth = 4f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(10f, 10f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SleepLineChart(
    entries: List<com.example.sizetracker.data.entity.SleepEntry>
) {
    if (entries.isEmpty()) return

    val sleepGoal = 8f // 8 hours ideal sleep

    // Sleep hours data
    val sleepEntries = entries.mapIndexed { index, entry ->
        entryOf(index.toFloat(), entry.hours)
    }

    // Goal line
    val goalEntries = entries.indices.map { index ->
        entryOf(index.toFloat(), sleepGoal)
    }

    // Color representation based on average quality
    val avgQuality = entries.map { it.quality }.average()
    val lineColor = when {
        avgQuality >= 4 -> Color(0xFF66BB6A) // Green - good quality
        avgQuality >= 3 -> Color(0xFFFFA726) // Orange - medium quality
        else -> Color(0xFFEF5350) // Red - poor quality
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(
                    color = lineColor,
                    label = "Сон (${String.format("%.1f★", avgQuality)})"
                )
                LegendItem(color = Color(0xFF9E9E9E), label = "Цель (8ч)", dashed = true)
            }

            Spacer(modifier = Modifier.height(8.dp))

            ProvideChartStyle(m3ChartStyle()) {
                Chart(
                    chart = lineChart(
                        lines = listOf(
                            // Sleep hours line
                            LineChart.LineSpec(
                                lineColor = lineColor.toArgb(),
                                lineThickness = 3.dp
                            ),
                            // Goal line
                            LineChart.LineSpec(
                                lineColor = Color(0xFF9E9E9E).toArgb(),
                                lineThickness = 2.dp,
                                lineBackgroundShader = null
                            )
                        )
                    ),
                    model = entryModelOf(sleepEntries, goalEntries),
                    startAxis = rememberStartAxis(
                        valueFormatter = { value, _ -> "%.1fч".format(value) }
                    ),
                    bottomAxis = rememberBottomAxis(),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
