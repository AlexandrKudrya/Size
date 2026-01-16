package com.example.sizetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sizetracker.data.dao.CalorieEntryDao
import com.example.sizetracker.data.dao.UserProfileDao
import com.example.sizetracker.data.dao.WeightEntryDao
import com.example.sizetracker.data.entity.CalorieEntry
import com.example.sizetracker.data.entity.UserProfile
import com.example.sizetracker.data.entity.WeightEntry

@Database(
    entities = [UserProfile::class, WeightEntry::class, CalorieEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun weightEntryDao(): WeightEntryDao
    abstract fun calorieEntryDao(): CalorieEntryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "size_tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
