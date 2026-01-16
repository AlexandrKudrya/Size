package com.example.sizetracker.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object AddWeight : Screen("add_weight")
    object AddCalorie : Screen("add_calorie")
    object History : Screen("history")
}
