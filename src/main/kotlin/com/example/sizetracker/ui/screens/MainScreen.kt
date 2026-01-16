package com.example.sizetracker.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.sizetracker.ui.viewmodel.SizeTrackerViewModel

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Главная")
    object Statistics : BottomNavItem("statistics", Icons.Default.BarChart, "Статистика")
    object History : BottomNavItem("history", Icons.Default.History, "История")
}

@Composable
fun MainScreen(
    viewModel: SizeTrackerViewModel,
    onAddWeightClick: () -> Unit,
    onAddCalorieClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Statistics,
                    BottomNavItem.History
                )

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedTab == item,
                        onClick = { selectedTab = item }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            BottomNavItem.Home -> {
                HomeScreen(
                    viewModel = viewModel,
                    onAddWeightClick = onAddWeightClick,
                    onAddCalorieClick = onAddCalorieClick,
                    onHistoryClick = { selectedTab = BottomNavItem.History }
                )
            }
            BottomNavItem.Statistics -> {
                StatisticsScreen(
                    viewModel = viewModel
                )
            }
            BottomNavItem.History -> {
                HistoryScreen(
                    viewModel = viewModel,
                    onBackClick = { selectedTab = BottomNavItem.Home }
                )
            }
        }
    }
}
