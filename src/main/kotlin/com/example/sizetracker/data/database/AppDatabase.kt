package com.example.sizetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sizetracker.data.dao.CalorieEntryDao
import com.example.sizetracker.data.dao.UserProfileDao
import com.example.sizetracker.data.dao.WeightEntryDao
import com.example.sizetracker.data.entity.CalorieEntry
import com.example.sizetracker.data.entity.UserProfile
import com.example.sizetracker.data.entity.WeightEntry

@Database(
    entities = [UserProfile::class, WeightEntry::class, CalorieEntry::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun weightEntryDao(): WeightEntryDao
    abstract fun calorieEntryDao(): CalorieEntryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to calorie_entries table
                database.execSQL("ALTER TABLE calorie_entries ADD COLUMN foodName TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE calorie_entries ADD COLUMN proteins REAL NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE calorie_entries ADD COLUMN fats REAL NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE calorie_entries ADD COLUMN carbs REAL NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "size_tracker_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
