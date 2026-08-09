package com.example.fittrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fittrack.data.local.entity.ExerciseEntity
import com.example.fittrack.data.local.entity.GoalEntity
import com.example.fittrack.data.local.entity.PersonalRecordEntity
import com.example.fittrack.data.local.entity.UserProfileEntity
import com.example.fittrack.data.local.entity.WaterLogEntity
import com.example.fittrack.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // -------------------------
    // WORKOUTS
    // -------------------------

    @Insert
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :id LIMIT 1")
    suspend fun getWorkout(id: Int): WorkoutEntity?

    @Query("UPDATE workouts SET completed = :completed WHERE id = :id")
    suspend fun setWorkoutCompleted(id: Int, completed: Boolean)

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteWorkout(id: Int)

    @Query("DELETE FROM workouts")
    suspend fun deleteAllWorkouts()


    // -------------------------
    // EXERCISES
    // -------------------------

    @Insert
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY id ASC")
    fun getExercises(workoutId: Int): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    suspend fun getExercise(id: Int): ExerciseEntity?

    @Query("SELECT * FROM exercises ORDER BY id DESC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("UPDATE exercises SET completedSets = :completedSets WHERE id = :exerciseId")
    suspend fun updateCompletedSets(exerciseId: Int, completedSets: Int)

    @Query("DELETE FROM exercises WHERE workoutId = :workoutId")
    suspend fun deleteExercisesForWorkout(workoutId: Int)

    @Query("DELETE FROM exercises")
    suspend fun deleteAllExercises()


    // -------------------------
    // GOALS
    // -------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoalById(id: Int)

    @Query("SELECT * FROM goals ORDER BY id DESC")
    fun getGoals(): Flow<List<GoalEntity>>

    @Query("DELETE FROM goals")
    suspend fun deleteAllGoals()


    // -------------------------
    // WATER LOGS
    // -------------------------

    @Insert
    suspend fun insertWaterLog(waterLog: WaterLogEntity): Long

    @Delete
    suspend fun deleteWaterLog(waterLog: WaterLogEntity)

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteWaterLogById(id: Int)

    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getWaterLogs(): Flow<List<WaterLogEntity>>

    @Query("DELETE FROM water_logs")
    suspend fun deleteAllWaterLogs()


    // -------------------------
    // PERSONAL RECORDS (PRs)
    // -------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalRecord(pr: PersonalRecordEntity): Long

    @Delete
    suspend fun deletePersonalRecord(pr: PersonalRecordEntity)

    @Query("SELECT * FROM personal_records ORDER BY date DESC")
    fun getPersonalRecords(): Flow<List<PersonalRecordEntity>>

    @Query("DELETE FROM personal_records")
    suspend fun deleteAllPersonalRecords()


    // -------------------------
    // USER PROFILE
    // -------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Query("DELETE FROM user_profile")
    suspend fun deleteUserProfile()
}