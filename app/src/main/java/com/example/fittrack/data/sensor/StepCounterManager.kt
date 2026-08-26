package com.example.fittrack.data.sensor

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * StepCounterManager
 * Robust, thread-safe, battery-optimized Singleton step tracker.
 * Counts daily steps continuously with clean midnight rollover and isolated historical archives.
 */
class StepCounterManager private constructor(
    context: Context
) : SensorEventListener {

    private val appContext: Context = context.applicationContext

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
     * Automatically handles midnight rollover (new day at 00:00 resets today's steps to 0 while archiving previous day).
     */
    @Synchronized
    fun loadPersistedSteps() {
        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_LAST_RECORDED_DATE, null)

        if (savedDate == null) {
            // First run or initial state: record today's date cleanly
            val savedSteps = prefs.getInt(KEY_TODAY_STEPS, 0)
            prefs.edit()
                .putString(KEY_LAST_RECORDED_DATE, today)
                .putInt(KEY_TODAY_STEPS, savedSteps)
                .putInt("steps_$today", savedSteps)
                .apply()
            _steps.value = savedSteps
        } else if (savedDate != today) {
            // Day changed at 00:00! Archive previous day's steps and reset today's accumulator to 0
            val previousDaySteps = prefs.getInt(KEY_TODAY_STEPS, 0)
            prefs.edit()
                .putInt("steps_$savedDate", previousDaySteps)
                .putInt(KEY_TODAY_STEPS, 0)
                .putInt("steps_$today", 0)
                .putString(KEY_LAST_RECORDED_DATE, today)
                .putInt(KEY_LAST_SENSOR_READING, -1)
                .apply()
            _steps.value = 0
        } else {
            val savedSteps = prefs.getInt(KEY_TODAY_STEPS, 0)
            _steps.value = savedSteps
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

        // If day changed while app is running (00:00 midnight rollover), reset for new day
        if (savedDate != today) {
            loadPersistedSteps()
        }

        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val rawSensorSteps = event.values[0].toInt()
            val lastSensorReading = prefs.getInt(KEY_LAST_SENSOR_READING, -1)
            var currentTodaySteps = prefs.getInt(KEY_TODAY_STEPS, 0)

            if (lastSensorReading == -1) {
                // First event received for this session/day: establish sensor baseline
                prefs.edit()
                    .putInt(KEY_LAST_SENSOR_READING, rawSensorSteps)
                    .putString(KEY_LAST_RECORDED_DATE, today)
                    .putInt(KEY_TODAY_STEPS, currentTodaySteps)
                    .putInt("steps_$today", currentTodaySteps)
                    .apply()
            } else if (rawSensorSteps < lastSensorReading) {
                // Device rebooted: sensor counter reset to 0
                prefs.edit()
                    .putInt(KEY_LAST_SENSOR_READING, rawSensorSteps)
                    .apply()
            } else {
                val delta = rawSensorSteps - lastSensorReading
                if (delta > 0) {
                    currentTodaySteps += delta
                    _steps.value = currentTodaySteps
                    prefs.edit()
                        .putInt(KEY_TODAY_STEPS, currentTodaySteps)
                        .putInt("steps_$today", currentTodaySteps)
                        .putInt(KEY_LAST_SENSOR_READING, rawSensorSteps)
                        .putString(KEY_LAST_RECORDED_DATE, today)
                        .apply()
                }
            }
        } else if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0f) {
                val currentTodaySteps = prefs.getInt(KEY_TODAY_STEPS, 0) + 1
                _steps.value = currentTodaySteps
                prefs.edit()
                    .putInt(KEY_TODAY_STEPS, currentTodaySteps)
                    .putInt("steps_$today", currentTodaySteps)
                    .putString(KEY_LAST_RECORDED_DATE, today)
                    .apply()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /**
     * Retrieves recorded steps for any specific date in "yyyy-MM-dd" format.
     */
    @Synchronized
    fun getStepsForDate(dateKey: String): Int {
        loadPersistedSteps()
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
        val sanitized = newSteps.coerceAtLeast(0)
        val today = getTodayDateString()
        prefs.edit()
            .putInt(KEY_TODAY_STEPS, sanitized)
            .putInt("steps_$today", sanitized)
            .putInt(KEY_LAST_SENSOR_READING, -1)
            .putString(KEY_LAST_RECORDED_DATE, today)
            .apply()
        _steps.value = sanitized
    }

    /**
     * Resets the persisted step tracking data upon user confirmation.
     */
    @Synchronized
    fun resetSteps() {
        val today = getTodayDateString()
        prefs.edit()
            .putInt(KEY_TODAY_STEPS, 0)
            .putInt("steps_$today", 0)
            .putInt(KEY_LAST_SENSOR_READING, -1)
            .putString(KEY_LAST_RECORDED_DATE, today)
            .apply()
        _steps.value = 0
    }

    companion object {
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