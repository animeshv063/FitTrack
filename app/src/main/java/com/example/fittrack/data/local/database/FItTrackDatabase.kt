package com.example.fittrack.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fittrack.data.local.dao.WorkoutDao
import com.example.fittrack.data.local.entity.ExerciseEntity
import com.example.fittrack.data.local.entity.WorkoutEntity

@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class FitTrackDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao

    companion object {

        @Volatile
        private var INSTANCE: FitTrackDatabase? = null

        fun getDatabase(
            context: Context
        ): FitTrackDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        FitTrackDatabase::class.java,
                        "fittrack_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                INSTANCE = instance

                instance
            }
        }
    }
}