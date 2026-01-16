package com.example.sizetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sizetracker.data.entity.WeightEntry
import com.example.sizetracker.ui.viewmodel.SizeTrackerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: SizeTrackerViewModel,
    onBackClick: () -> Unit
) {
    val allWeightEntries by viewModel.allWeightEntries.collectAsState()
    val allCalorieEntries by viewModel.allCalorieEntries.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("История") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Вес") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Калории") }
                )
            }

            when (selectedTab) {
                0 -> WeightHistoryTab(
                    entries = allWeightEntries,
                    onDelete = { viewModel.deleteWeightEntry(it) }
                )
                1 -> CalorieHistoryTab(
                    entries = allCalorieEntries,
                    viewModel = viewModel,
                    onDelete = { viewModel.deleteCalorieEntry(it) }
                )
            }
        }
    }
}

@Composable
fun WeightHistoryTab(
    entries: List<WeightEntry>,
    onDelete: (WeightEntry) -> Unit
) {
    if (entries.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет записей о весе",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(entries) { entry ->
                WeightEntryItem(
                    entry = entry,
                    onDelete = { onDelete(entry) }
                )
            }
        }
    }
}

@Composable
fun CalorieHistoryTab(
    entries: List<com.example.sizetracker.data.entity.CalorieEntry>,
    viewModel: SizeTrackerViewModel,
    onDelete: (com.example.sizetracker.data.entity.CalorieEntry) -> Unit
) {
    if (entries.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет записей о калориях",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        // Group entries by date
        val groupedEntries = entries.groupBy { it.date }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            groupedEntries.forEach { (date, dateEntries) ->
                item {
                    CalorieDateGroup(
                        date = date,
                        entries = dateEntries,
                        viewModel = viewModel,
                        onDelete = onDelete
                    )
                }
            }
        }
    }
}

@Composable
fun CalorieDateGroup(
    date: String,
    entries: List<com.example.sizetracker.data.entity.CalorieEntry>,
    viewModel: SizeTrackerViewModel,
    onDelete: (com.example.sizetracker.data.entity.CalorieEntry) -> Unit
) {
    val totalForDate by viewModel.getCaloriesForDate(date).collectAsState(initial = 0)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = date,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$totalForDate ккал",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            entries.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${entry.calories} ккал",
                            fontSize = 14.sp
                        )
                        Text(
                            text = SimpleDateFormat("HH:mm", Locale.getDefault())
                                .format(Date(entry.timestamp)),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onDelete(entry) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
