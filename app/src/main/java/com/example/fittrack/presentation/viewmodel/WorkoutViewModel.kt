package com.example.fittrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrack.data.local.entity.ExerciseEntity
import com.example.fittrack.data.local.entity.GoalEntity
import com.example.fittrack.data.local.entity.PersonalRecordEntity
import com.example.fittrack.data.local.entity.UserProfileEntity
import com.example.fittrack.data.local.entity.WaterLogEntity
import com.example.fittrack.data.local.entity.WorkoutEntity
import com.example.fittrack.data.repository.WorkoutRepository
import com.example.fittrack.data.sensor.StepCounterManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CustomExerciseDraft(
    val name: String,
    val sets: Int,
    val reps: Int,
    val weight: Int
)

class WorkoutViewModel(
    private val repository: WorkoutRepository,
    private val stepCounterManager: StepCounterManager
) : ViewModel() {

    // -------------------------
    // STEPS & SENSORS
    // -------------------------

    val steps = stepCounterManager.steps

    fun startStepCounter() {
        stepCounterManager.start()
    }

    fun updateSteps(newSteps: Int) {
        stepCounterManager.setManualSteps(newSteps)
    }

    // -------------------------
    // TARGET VOLUME GOAL & CHART SCALE
    // -------------------------

    private val _targetVolumeGoal = MutableStateFlow(25000f)
    val targetVolumeGoal: StateFlow<Float> = _targetVolumeGoal.asStateFlow()

    private val _dailyVolumeScaleTarget = MutableStateFlow(3500f) // Realistic medium-high default
    val dailyVolumeScaleTarget: StateFlow<Float> = _dailyVolumeScaleTarget.asStateFlow()

    fun updateTargetVolumeGoal(newGoal: Float) {
        _targetVolumeGoal.value = newGoal.coerceAtLeast(100f)
    }

    fun updateDailyVolumeScaleTarget(newScale: Float) {
        _dailyVolumeScaleTarget.value = newScale.coerceAtLeast(500f)
    }


    // -------------------------
    // WORKOUTS
    // -------------------------

    val workouts: StateFlow<List<WorkoutEntity>> =
        repository
            .getWorkouts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addWorkout(workout: WorkoutEntity, onCreated: ((Int) -> Unit)? = null) {
        viewModelScope.launch {
            val id = repository.insertWorkout(workout).toInt()
            onCreated?.invoke(id)
        }
    }

    fun updateWorkout(workout: WorkoutEntity) {
        viewModelScope.launch {
            repository.updateWorkout(workout)
        }
    }

    fun deleteWorkout(workout: WorkoutEntity) {
        viewModelScope.launch {
            repository.deleteWorkout(workout.id)
        }
    }

    fun deleteWorkoutById(id: Int) {
        viewModelScope.launch {
            repository.deleteWorkout(id)
        }
    }

    fun completeWorkout(workoutId: Int) {
        viewModelScope.launch {
            repository.setWorkoutCompleted(workoutId, true)
        }
    }

    fun deleteAllWorkouts() {
        viewModelScope.launch {
            repository.deleteAllWorkouts()
        }
    }

    fun addPresetWorkout(presetName: String, durationMin: Int, exerciseList: List<Triple<String, Int, Int>>, onCreated: ((Int) -> Unit)? = null) {
        viewModelScope.launch {
            val workoutId = repository.insertWorkout(
                WorkoutEntity(
                    name = presetName,
                    duration = durationMin,
                    date = System.currentTimeMillis()
                )
            ).toInt()

            exerciseList.forEach { (exName, sets, reps) ->
                repository.insertExercise(
                    ExerciseEntity(
                        workoutId = workoutId,
                        name = exName,
                        sets = sets,
                        reps = reps,
                        weight = when {
                            exName.contains("Squat", ignoreCase = true) -> 60
                            exName.contains("Deadlift", ignoreCase = true) -> 80
                            exName.contains("Bench", ignoreCase = true) -> 50
                            exName.contains("Press", ignoreCase = true) -> 35
                            exName.contains("Curl", ignoreCase = true) -> 15
                            exName.contains("Dip", ignoreCase = true) || exName.contains("Plank", ignoreCase = true) -> 0
                            else -> 25
                        }
                    )
                )
            }

            onCreated?.invoke(workoutId)
        }
    }

    fun addCustomWorkoutWithExercises(
        name: String,
        durationMin: Int,
        exercises: List<CustomExerciseDraft>,
        onCreated: ((Int) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val workoutId = repository.insertWorkout(
                WorkoutEntity(
                    name = name,
                    duration = durationMin,
                    date = System.currentTimeMillis()
                )
            ).toInt()

            exercises.forEach { ex ->
                repository.insertExercise(
                    ExerciseEntity(
                        workoutId = workoutId,
                        name = ex.name,
                        sets = ex.sets,
                        reps = ex.reps,
                        weight = ex.weight
                    )
                )
            }

            onCreated?.invoke(workoutId)
        }
    }

    fun createOrGetDayWorkout(dayTitle: String, routineName: String, durationMin: Int, exercises: List<Triple<String, Int, Int>>, onReady: (Int) -> Unit) {
        viewModelScope.launch {
            val currentList = workouts.value
            val existing = currentList.find { it.name.contains(routineName, ignoreCase = true) && !it.completed }
            if (existing != null) {
                onReady(existing.id)
            } else {
                addPresetWorkout("$dayTitle: $routineName", durationMin, exercises, onReady)
            }
        }
    }


    // -------------------------
    // EXERCISES
    // -------------------------

    val allExercises: StateFlow<List<ExerciseEntity>> =
        repository
            .getAllExercises()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun getExercises(workoutId: Int) = repository.getExercises(workoutId)

    fun addExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.insertExercise(exercise)
        }
    }

    fun updateExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.updateExercise(exercise)
        }
    }

    fun deleteExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.deleteExercise(exercise)
        }
    }

    fun updateCompletedSets(exerciseId: Int, completedSets: Int) {
        viewModelScope.launch {
            repository.updateCompletedSets(exerciseId, completedSets)
        }
    }


    // -------------------------
    // GOALS
    // -------------------------

    val goals: StateFlow<List<GoalEntity>> =
        repository
            .getGoals()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.insertGoal(goal)
        }
    }

    fun updateGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }

    fun completeGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(isCompleted = true))
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    fun deleteGoalById(id: Int) {
        viewModelScope.launch {
            repository.deleteGoalById(id)
        }
    }


    // -------------------------
    // WATER LOGS
    // -------------------------

    val waterLogs: StateFlow<List<WaterLogEntity>> =
        repository
            .getWaterLogs()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addWaterLog(amountMl: Int = 250) {
        viewModelScope.launch {
            repository.insertWaterLog(
                WaterLogEntity(amountMl = amountMl)
            )
        }
    }

    fun deleteWaterLog(log: WaterLogEntity) {
        viewModelScope.launch {
            repository.deleteWaterLog(log)
        }
    }

    fun deleteLastWaterLog() {
        viewModelScope.launch {
            val currentList = waterLogs.value
            if (currentList.isNotEmpty()) {
                repository.deleteWaterLog(currentList.first())
            }
        }
    }

    fun deleteAllWaterLogs() {
        viewModelScope.launch {
            repository.deleteAllWaterLogs()
        }
    }

    fun updateWaterGoal(newGoalMl: Int) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.saveUserProfile(current.copy(waterGoalMl = newGoalMl))
        }
    }

    fun updateStepGoal(newStepGoal: Int) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.saveUserProfile(current.copy(stepGoal = newStepGoal))
        }
    }


    // -------------------------
    // PERSONAL RECORDS (PRs)
    // -------------------------

    val personalRecords: StateFlow<List<PersonalRecordEntity>> =
        repository
            .getPersonalRecords()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addPersonalRecord(exerciseName: String, maxWeightKg: Int) {
        viewModelScope.launch {
            repository.insertPersonalRecord(
                PersonalRecordEntity(
                    exerciseName = exerciseName,
                    maxWeightKg = maxWeightKg
                )
            )
        }
    }

    fun deletePersonalRecord(pr: PersonalRecordEntity) {
        viewModelScope.launch {
            repository.deletePersonalRecord(pr)
        }
    }


    // -------------------------
    // USER PROFILE & DYNAMIC ATHLETE LEVEL
    // -------------------------

    val userProfile: StateFlow<UserProfileEntity?> =
        repository
            .getUserProfile()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UserProfileEntity()
            )

    fun saveUserProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun resetUserProfile() {
        viewModelScope.launch {
            repository.resetUserProfile()
        }
    }

    fun toggleCelebrationAnimations(enabled: Boolean) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.saveUserProfile(current.copy(celebrationAnimationsEnabled = enabled))
        }
    }

    fun getAthleteTitle(completedWorkoutsCount: Int): String {
        return when {
            completedWorkoutsCount >= 50 -> "Master Athlete"
            completedWorkoutsCount >= 30 -> "Pro Lifter"
            completedWorkoutsCount >= 15 -> "Advanced Lifter"
            completedWorkoutsCount >= 5 -> "Intermediate Athlete"
            completedWorkoutsCount >= 1 -> "Novice Lifter"
            else -> "Rookie Athlete"
        }
    }


    // -------------------------
    // TIMEFRAME SELECTION FOR PROGRESS
    // -------------------------

    private val _selectedTimeframe = MutableStateFlow("7 Days")
    val selectedTimeframe: StateFlow<String> = _selectedTimeframe.asStateFlow()

    fun setTimeframe(timeframe: String) {
        _selectedTimeframe.value = timeframe
    }


    // -------------------------
    // RESET EVERYTHING
    // -------------------------

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            stepCounterManager.resetSteps()
        }
    }
}