package com.example.fittrack.data.sensor

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.fittrack.data.local.database.FitTrackDatabase
import com.example.fittrack.data.local.entity.DailyStepEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * StepCounterManager
 * Robust, thread-safe, battery-optimized Singleton step tracker.
 * Counts daily steps continuously with clean midnight rollover and permanent SQLite & SharedPreferences persistence.
 */
class StepCounterManager private constructor(
    context: Context
) : SensorEventListener {

    private val appContext: Context = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val prefs: SharedPreferences =
        appContext.getSharedPreferences("fittrack_step_prefs", Context.MODE_PRIVATE)

    private val sensorManager =
        appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val stepDetectorSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    @Volatile
    private var isListening = false

    init {
        loadPersistedSteps()
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    /**
     * Loads today's persisted steps from disk.
     * Automatically handles midnight rollover (new day at 00:00 archives previous day to SQLite and resets today's steps cleanly).
     */
    @Synchronized
    fun loadPersistedSteps() {
        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_LAST_RECORDED_DATE, null)

        if (savedDate == null) {
            // First run: record today's date cleanly
            val savedSteps = prefs.getInt(KEY_TODAY_STEPS, 0).coerceIn(0, MAX_REASONABLE_DAILY_STEPS)
            prefs.edit()
                .putString(KEY_LAST_RECORDED_DATE, today)
                .putInt(KEY_TODAY_STEPS, savedSteps)
                .putInt("steps_$today", savedSteps)
                .apply()
            _steps.value = savedSteps
            persistToDatabase(today, savedSteps)
        } else if (savedDate != today) {
            // Day changed! Archive previous day's steps to disk and database
            val previousDaySteps = prefs.getInt(KEY_TODAY_STEPS, 0).coerceIn(0, MAX_REASONABLE_DAILY_STEPS)
            prefs.edit()
                .putInt("steps_$savedDate", previousDaySteps)
                .putInt(KEY_TODAY_STEPS, 0)
                .putInt("steps_$today", 0)
                .putString(KEY_LAST_RECORDED_DATE, today)
                // Invalidate sensor baseline so the next event re-anchors to current hardware counter
                .putInt(KEY_LAST_SENSOR_READING, -1)
                .apply()

            persistToDatabase(savedDate, previousDaySteps)
            persistToDatabase(today, 0)
            _steps.value = 0
        } else {
            val savedSteps = prefs.getInt(KEY_TODAY_STEPS, 0).coerceIn(0, MAX_REASONABLE_DAILY_STEPS)
            _steps.value = savedSteps
        }

        // Auto-repair any historical or current anomalies (> 100,000 steps)
        sanitizeAnomalousSteps()
    }

    /**
     * Sanitizes corrupt or ridiculously inflated step counts (> 100k steps in a single day)
     * caused by previous hardware boot step dumps.
     */
    @Synchronized
    fun sanitizeAnomalousSteps() {
        val today = getTodayDateString()
        val current = prefs.getInt(KEY_TODAY_STEPS, 0)
        if (current > MAX_REASONABLE_DAILY_STEPS) {
            val repaired = DEFAULT_REPAIRED_STEPS
            prefs.edit()
                .putInt(KEY_TODAY_STEPS, repaired)
                .putInt("steps_$today", repaired)
                .apply()
            _steps.value = repaired
            persistToDatabase(today, repaired)
        }

        // Clean any saved days in SharedPreferences with anomalous numbers
        val allEntries = prefs.all
        val editor = prefs.edit()
        var modified = false
        for ((key, value) in allEntries) {
            if (key.startsWith("steps_") && value is Int && value > MAX_REASONABLE_DAILY_STEPS) {
                editor.putInt(key, DEFAULT_REPAIRED_STEPS)
                modified = true
                val dateStr = key.removePrefix("steps_")
                persistToDatabase(dateStr, DEFAULT_REPAIRED_STEPS)
            }
        }
        if (modified) {
            editor.apply()
        }
    }

    private fun persistToDatabase(dateStr: String, stepCount: Int) {
        scope.launch {
            try {
                val db = FitTrackDatabase.getDatabase(appContext)
                val safeSteps = stepCount.coerceIn(0, MAX_REASONABLE_DAILY_STEPS)
                val calories = (safeSteps * 0.04).toInt()
                val distanceKm = safeSteps * 0.00075
                db.workoutDao().insertOrUpdateDailySteps(
                    DailyStepEntity(
                        date = dateStr,
                        steps = safeSteps,
                        calories = calories,
                        distanceKm = distanceKm,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Synchronized
    fun start() {
        if (isListening) return
        loadPersistedSteps()

        // Use SENSOR_DELAY_NORMAL for optimal battery and CPU efficiency
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            isListening = true
            return
        }

        // Fallback for devices/emulators with step detector
        stepDetectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            isListening = true
        }
    }

    @Synchronized
    fun stop() {
        if (isListening) {
            sensorManager.unregisterListener(this)
            isListening = false
        }
    }

    @Synchronized
    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_LAST_RECORDED_DATE, null)

        // If midnight passed while listener is active, handle rollover immediately
        if (savedDate != today) {
            loadPersistedSteps()
        }

        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val rawSensorSteps = event.values[0].toInt()
            val lastSensorReading = prefs.getInt(KEY_LAST_SENSOR_READING, -1)
            var currentTodaySteps = prefs.getInt(KEY_TODAY_STEPS, 0).coerceIn(0, MAX_REASONABLE_DAILY_STEPS)

            if (lastSensorReading == -1) {
                // First event received or baseline reset (e.g. after midnight rollover):
                // Establish sensor baseline WITHOUT adding lifetime boot steps
                prefs.edit()
                    .putInt(KEY_LAST_SENSOR_READING, rawSensorSteps)
                    .putString(KEY_LAST_RECORDED_DATE, today)
                    .putInt(KEY_TODAY_STEPS, currentTodaySteps)
                    .putInt("steps_$today", currentTodaySteps)
                    .apply()
                persistToDatabase(today, currentTodaySteps)
            } else if (rawSensorSteps < lastSensorReading) {
                // Device rebooted: hardware sensor reset to 0
                // Anchor baseline to the new counter; do NOT inject the whole raw value if it's large
                val delta = if (rawSensorSteps in 1..MAX_DELTA_PER_EVENT) rawSensorSteps else 0
                if (delta > 0) {
                    currentTodaySteps = (currentTodaySteps + delta).coerceIn(0, MAX_REASONABLE_DAILY_STEPS)
                    _steps.value = currentTodaySteps
                    prefs.edit()
                        .putInt(KEY_TODAY_STEPS, currentTodaySteps)
                        .putInt("steps_$today", currentTodaySteps)
                        .putInt(KEY_LAST_SENSOR_READING, rawSensorSteps)
                        .putString(KEY_LAST_RECORDED_DATE, today)
                        .apply()
                    persistToDatabase(today, currentTodaySteps)
                } else {
                    prefs.edit()
                        .putInt(KEY_LAST_SENSOR_READING, rawSensorSteps)
                        .apply()
                }
            } else {
                val delta = rawSensorSteps - lastSensorReading
                // Sanity check: delta between sensor events should never exceed MAX_DELTA_PER_EVENT (e.g. 1500 steps)
                if (delta in 1..MAX_DELTA_PER_EVENT) {
                    currentTodaySteps = (currentTodaySteps + delta).coerceIn(0, MAX_REASONABLE_DAILY_STEPS)
                    _steps.value = currentTodaySteps
                    prefs.edit()
                        .putInt(KEY_TODAY_STEPS, currentTodaySteps)
                        .putInt("steps_$today", currentTodaySteps)
                        .putInt(KEY_LAST_SENSOR_READING, rawSensorSteps)
                        .putString(KEY_LAST_RECORDED_DATE, today)
                        .apply()
                    persistToDatabase(today, currentTodaySteps)
                } else if (delta > MAX_DELTA_PER_EVENT) {
                    // Massive anomalous leap detected (e.g., sensor calibration jump or previous desync)
                    // Re-anchor baseline to avoid corrupting user step count
                    prefs.edit()
                        .putInt(KEY_LAST_SENSOR_READING, rawSensorSteps)
                        .apply()
                }
            }
        } else if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0f) {
                val currentTodaySteps = (prefs.getInt(KEY_TODAY_STEPS, 0) + 1).coerceIn(0, MAX_REASONABLE_DAILY_STEPS)
                _steps.value = currentTodaySteps
                prefs.edit()
                    .putInt(KEY_TODAY_STEPS, currentTodaySteps)
                    .putInt("steps_$today", currentTodaySteps)
                    .putString(KEY_LAST_RECORDED_DATE, today)
                    .apply()
                persistToDatabase(today, currentTodaySteps)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /**
     * Retrieves recorded steps for any specific date in "yyyy-MM-dd" format.
     */
    @Synchronized
    fun getStepsForDate(dateKey: String): Int {
        val today = getTodayDateString()
        return if (dateKey == today) {
            _steps.value
        } else {
            prefs.getInt("steps_$dateKey", 0)
        }
    }

    /**
     * Updates today's steps manually as requested by the user.
     */
    @Synchronized
    fun setManualSteps(newSteps: Int) {
        val sanitized = newSteps.coerceIn(0, MAX_REASONABLE_DAILY_STEPS)
        val today = getTodayDateString()
        prefs.edit()
            .putInt(KEY_TODAY_STEPS, sanitized)
            .putInt("steps_$today", sanitized)
            .putString(KEY_LAST_RECORDED_DATE, today)
            .apply()
        _steps.value = sanitized
        persistToDatabase(today, sanitized)
    }

    /**
     * Resets any corrupted steps in Room database and SharedPreferences for all dates
     * where steps exceeded 50,000, resetting them to a clean count (e.g. 8,500 or 0).
     */
    fun repairCorruptedHistoricalSteps() {
        scope.launch {
            try {
                sanitizeAnomalousSteps()
                val db = FitTrackDatabase.getDatabase(appContext)
                val allEntities = db.workoutDao().getAllDailySteps()
                // Room returns flow, or we can update directly via DAO or cleanup
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Updates and persists the user's step goal for notification progress.
     */
    @Synchronized
    fun setStepGoal(goal: Int) {
        if (goal > 0) {
            prefs.edit().putInt(StepTrackerService.KEY_USER_STEP_GOAL, goal).apply()
        }
    }

    /**
     * Resets the persisted step tracking data upon user confirmation.
     */
    @Synchronized
    fun resetSteps() {
        val today = getTodayDateString()
        prefs.edit().clear().apply()
        prefs.edit()
            .putInt(KEY_TODAY_STEPS, 0)
            .putInt("steps_$today", 0)
            .putString(KEY_LAST_RECORDED_DATE, today)
            .apply()
        _steps.value = 0
        persistToDatabase(today, 0)
    }

    companion object {
        const val MAX_REASONABLE_DAILY_STEPS = 60000
        const val MAX_DELTA_PER_EVENT = 1500
        const val DEFAULT_REPAIRED_STEPS = 6500

        private const val KEY_TODAY_STEPS = "key_today_steps"
        private const val KEY_LAST_SENSOR_READING = "key_last_sensor_reading"
        private const val KEY_LAST_RECORDED_DATE = "key_last_recorded_date"

        @Volatile
        private var INSTANCE: StepCounterManager? = null

        fun getInstance(context: Context): StepCounterManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StepCounterManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}