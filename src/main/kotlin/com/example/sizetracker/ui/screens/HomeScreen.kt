package com.example.sizetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sizetracker.data.entity.UserProfile
import com.example.sizetracker.data.entity.WeightEntry
import com.example.sizetracker.ui.viewmodel.SizeTrackerViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: SizeTrackerViewModel,
    onAddWeightClick: () -> Unit,
    onAddCalorieClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val latestWeight by viewModel.latestWeightEntry.collectAsState()
    val todayCalories by viewModel.todayTotalCalories.collectAsState()
    val todayWater by viewModel.todayTotalWater.collectAsState()
    val todaySleep by viewModel.todaySleepEntry.collectAsState()
    val recentWeightEntries by viewModel.getFilteredWeightEntries(7).collectAsState(initial = emptyList())

    val dateFormat = SimpleDateFormat("dd MMMM, yyyy", Locale("ru"))
    val today = dateFormat.format(Date())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Default.History, contentDescription = "История")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Date Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Сегодня, $today",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Weight Card
            userProfile?.let { profile ->
                WeightCard(
                    currentWeight = latestWeight?.weight ?: profile.currentWeight,
                    targetWeight = profile.targetWeight,
                    recentEntries = recentWeightEntries
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calories Card
            userProfile?.let { profile ->
                CaloriesCard(
                    currentCalories = todayCalories,
                    targetCalories = profile.dailyCalorieLimit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Water and Sleep Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WaterCard(
                    currentWater = todayWater,
                    targetWater = 2000, // 2 liters default
                    onAddWater = { viewModel.addWaterEntry(250) }, // Add 250ml
                    modifier = Modifier.weight(1f)
                )

                SleepCard(
                    sleepEntry = todaySleep,
                    onAddSleep = { /* Will add dialog later */ },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Button(
                onClick = onAddWeightClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Записать вес", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onAddCalorieClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Добавить калории", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun WeightCard(
    currentWeight: Float,
    targetWeight: Float,
    recentEntries: List<WeightEntry>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Вес",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "%.1f кг".format(currentWeight),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val diff = currentWeight - targetWeight
                    val diffText = if (diff > 0) {
                        "Осталось: %.1f кг".format(diff)
                    } else {
                        "Цель достигнута!"
                    }
                    Text(
                        text = "Цель: %.1f кг".format(targetWeight),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = diffText,
                        fontSize = 14.sp,
                        color = if (diff > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                }

                // Mini chart
                if (recentEntries.size >= 2) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(80.dp)
                    ) {
                        MiniWeightChart(entries = recentEntries)
                    }
                }
            }
        }
    }
}

@Composable
fun MiniWeightChart(entries: List<WeightEntry>) {
    val sortedEntries = entries.sortedBy { it.timestamp }
    val chartEntries = sortedEntries.mapIndexed { index, entry ->
        entryOf(index.toFloat(), entry.weight)
    }

    ProvideChartStyle(m3ChartStyle()) {
        Chart(
            chart = lineChart(),
            model = entryModelOf(chartEntries),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun CaloriesCard(currentCalories: Int, targetCalories: Int) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Калории сегодня",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$currentCalories / $targetCalories ккал",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = (currentCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            val remaining = targetCalories - currentCalories
            Text(
                text = if (remaining > 0) "Осталось: $remaining ккал" else "Лимит превышен на ${-remaining} ккал",
                fontSize = 14.sp,
                color = if (remaining > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun WaterCard(
    currentWater: Int,
    targetWater: Int,
    onAddWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onAddWater
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💧 Вода",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${currentWater} мл",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "из ${targetWater} мл",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = (currentWater.toFloat() / targetWater.toFloat()).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )
        }
    }
}

@Composable
fun SleepCard(
    sleepEntry: com.example.sizetracker.data.entity.SleepEntry?,
    onAddSleep: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onAddSleep
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "😴 Сон",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (sleepEntry != null) {
                Text(
                    text = "%.1f ч".format(sleepEntry.hours),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Text(
                            text = if (index < sleepEntry.quality) "⭐" else "☆",
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                Text(
                    text = "Не записано",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Нажмите для записи",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
