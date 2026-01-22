package com.example.sizetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.sizetracker.data.database.AppDatabase
import com.example.sizetracker.data.repository.SizeTrackerRepository
import com.example.sizetracker.ui.navigation.NavGraph
import com.example.sizetracker.ui.theme.SizeTrackerTheme
import com.example.sizetracker.ui.viewmodel.SizeTrackerViewModel
import com.example.sizetracker.ui.viewmodel.SizeTrackerViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: SizeTrackerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize database and repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = SizeTrackerRepository(
            userProfileDao = database.userProfileDao(),
            weightEntryDao = database.weightEntryDao(),
            calorieEntryDao = database.calorieEntryDao(),
            waterEntryDao = database.waterEntryDao(),
            sleepEntryDao = database.sleepEntryDao()
        )

        // Initialize ViewModel
        val viewModelFactory = SizeTrackerViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[SizeTrackerViewModel::class.java]

        setContent {
            SizeTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
