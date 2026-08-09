package com.example.fittrack.data.repository

import com.example.fittrack.data.local.dao.WorkoutDao
import com.example.fittrack.data.local.entity.ExerciseEntity
import com.example.fittrack.data.local.entity.GoalEntity
import com.example.fittrack.data.local.entity.PersonalRecordEntity
import com.example.fittrack.data.local.entity.UserProfileEntity
import com.example.fittrack.data.local.entity.WaterLogEntity
import com.example.fittrack.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val workoutDao: WorkoutDao
) {

    // -------------------------
    // WORKOUTS
    // -------------------------

    fun getWorkouts(): Flow<List<WorkoutEntity>> {
        return workoutDao.getWorkouts()
    }

    suspend fun getWorkout(id: Int): WorkoutEntity? {
        return workoutDao.getWorkout(id)
    }

    suspend fun insertWorkout(workout: WorkoutEntity): Long {
        return workoutDao.insertWorkout(workout)
    }

    suspend fun updateWorkout(workout: WorkoutEntity) {
        workoutDao.updateWorkout(workout)
    }

    suspend fun deleteWorkout(id: Int) {
        workoutDao.deleteWorkout(id)
    }

    suspend fun setWorkoutCompleted(id: Int, completed: Boolean) {
        workoutDao.setWorkoutCompleted(id, completed)
    }

    suspend fun deleteAllWorkouts() {
        workoutDao.deleteAllWorkouts()
    }


    // -------------------------
    // EXERCISES
    // -------------------------

    fun getExercises(workoutId: Int): Flow<List<ExerciseEntity>> {
        return workoutDao.getExercises(workoutId)
    }

    fun getAllExercises(): Flow<List<ExerciseEntity>> {
        return workoutDao.getAllExercises()
    }

    suspend fun getExercise(id: Int): ExerciseEntity? {
        return workoutDao.getExercise(id)
    }

    suspend fun insertExercise(exercise: ExerciseEntity): Long {
        return workoutDao.insertExercise(exercise)
    }

    suspend fun updateExercise(exercise: ExerciseEntity) {
        workoutDao.updateExercise(exercise)
    }

    suspend fun deleteExercise(exercise: ExerciseEntity) {
        workoutDao.deleteExercise(exercise)
    }

    suspend fun updateCompletedSets(exerciseId: Int, completedSets: Int) {
        workoutDao.updateCompletedSets(exerciseId, completedSets)
    }

    suspend fun deleteExercisesForWorkout(workoutId: Int) {
        workoutDao.deleteExercisesForWorkout(workoutId)
    }

    suspend fun deleteAllExercises() {
        workoutDao.deleteAllExercises()
    }


    // -------------------------
    // GOALS
    // -------------------------

    fun getGoals(): Flow<List<GoalEntity>> {
        return workoutDao.getGoals()
    }

    suspend fun insertGoal(goal: GoalEntity): Long {
        return workoutDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: GoalEntity) {
        workoutDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: GoalEntity) {
        workoutDao.deleteGoal(goal)
    }

    suspend fun deleteGoalById(id: Int) {
        workoutDao.deleteGoalById(id)
    }

    suspend fun deleteAllGoals() {
        workoutDao.deleteAllGoals()
    }


    // -------------------------
    // WATER LOGS
    // -------------------------

    fun getWaterLogs(): Flow<List<WaterLogEntity>> {
        return workoutDao.getWaterLogs()
    }

    suspend fun insertWaterLog(waterLog: WaterLogEntity): Long {
        return workoutDao.insertWaterLog(waterLog)
    }

    suspend fun deleteWaterLog(waterLog: WaterLogEntity) {
        workoutDao.deleteWaterLog(waterLog)
    }

    suspend fun deleteWaterLogById(id: Int) {
        workoutDao.deleteWaterLogById(id)
    }

    suspend fun deleteAllWaterLogs() {
        workoutDao.deleteAllWaterLogs()
    }


    // -------------------------
    // PERSONAL RECORDS (PRs)
    // -------------------------

    fun getPersonalRecords(): Flow<List<PersonalRecordEntity>> {
        return workoutDao.getPersonalRecords()
    }

    suspend fun insertPersonalRecord(pr: PersonalRecordEntity): Long {
        return workoutDao.insertPersonalRecord(pr)
    }

    suspend fun deletePersonalRecord(pr: PersonalRecordEntity) {
        workoutDao.deletePersonalRecord(pr)
    }

    suspend fun deleteAllPersonalRecords() {
        workoutDao.deleteAllPersonalRecords()
    }


    // -------------------------
    // USER PROFILE
    // -------------------------

    fun getUserProfile(): Flow<UserProfileEntity?> {
        return workoutDao.getUserProfile()
    }

    suspend fun getUserProfileOnce(): UserProfileEntity? {
        return workoutDao.getUserProfileOnce()
    }

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        workoutDao.insertOrUpdateProfile(profile)
    }

    suspend fun resetUserProfile() {
        workoutDao.deleteUserProfile()
        workoutDao.insertOrUpdateProfile(UserProfileEntity())
    }


    // -------------------------
    // GLOBAL RESET
    // -------------------------

    suspend fun resetAllData() {
        workoutDao.deleteAllWorkouts()
        workoutDao.deleteAllExercises()
        workoutDao.deleteAllGoals()
        workoutDao.deleteAllWaterLogs()
        workoutDao.deleteAllPersonalRecords()
        workoutDao.deleteUserProfile()
        workoutDao.insertOrUpdateProfile(UserProfileEntity())
    }
}