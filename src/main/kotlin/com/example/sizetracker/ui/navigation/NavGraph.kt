package com.example.sizetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sizetracker.ui.screens.*
import com.example.sizetracker.ui.viewmodel.SizeTrackerViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: SizeTrackerViewModel
) {
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isOnboardingCompleted) Screen.Home.route else Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = { currentWeight, targetWeight, height, age, gender ->
                    viewModel.saveUserProfile(
                        currentWeight = currentWeight,
                        targetWeight = targetWeight,
                        height = height,
                        age = age,
                        gender = gender
                    )
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            MainScreen(
                viewModel = viewModel,
                onAddWeightClick = {
                    navController.navigate(Screen.AddWeight.route)
                },
                onAddCalorieClick = {
                    navController.navigate(Screen.AddCalorie.route)
                }
            )
        }

        composable(Screen.AddWeight.route) {
            AddWeightScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AddCalorie.route) {
            AddCalorieScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
