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
 * Robust, persistent step tracker that counts daily steps continuously without resetting on app close.
 * Saves accumulated steps and sensor baselines in persistent storage.
 */
class StepCounterManager(
    private val context: Context
) : SensorEventListener {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("fittrack_step_prefs", Context.MODE_PRIVATE)

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val stepDetectorSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private var isListening = false

    init {
        loadPersistedSteps()
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * Loads today's persisted steps from disk.
     * Automatically handles midnight rollover (new day resets today's steps to 0 while saving history).
     */
    fun loadPersistedSteps() {
        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_LAST_RECORDED_DATE, today) ?: today

        if (savedDate != today) {
            // Day changed! Store yesterday's steps and reset today's accumulator
            val previousDaySteps = prefs.getInt(KEY_TODAY_STEPS, 0)
            prefs.edit()
                .putInt("steps_$savedDate", previousDaySteps)
                .putInt(KEY_TODAY_STEPS, 0)
                .putString(KEY_LAST_RECORDED_DATE, today)
                .putInt(KEY_LAST_SENSOR_READING, -1)
                .apply()
            _steps.value = 0
        } else {
            val savedSteps = prefs.getInt(KEY_TODAY_STEPS, 0)
            _steps.value = savedSteps
        }
    }

    fun start() {
        if (isListening) return
        loadPersistedSteps()

        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            isListening = true
            return
        }

        // Fallback for devices/emulators with step detector
        stepDetectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            isListening = true
        }
    }

    fun stop() {
        if (isListening) {
            sensorManager.unregisterListener(this)
            isListening = false
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_LAST_RECORDED_DATE, today) ?: today

        // If day changed while app is running, reset for new day
        if (savedDate != today) {
            loadPersistedSteps()
        }

        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val rawSensorSteps = event.values[0].toInt()
            val lastSensorReading = prefs.getInt(KEY_LAST_SENSOR_READING, -1)
            var currentTodaySteps = prefs.getInt(KEY_TODAY_STEPS, 0)

            if (lastSensorReading == -1) {
                // First event received for this session/day
                prefs.edit()
                    .putInt(KEY_LAST_SENSOR_READING, rawSensorSteps)
                    .putString(KEY_LAST_RECORDED_DATE, today)
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
                    .putString(KEY_LAST_RECORDED_DATE, today)
                    .apply()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /**
     * Updates today's steps manually as requested by the user.
     */
    fun setManualSteps(newSteps: Int) {
        val sanitized = newSteps.coerceAtLeast(0)
        val today = getTodayDateString()
        prefs.edit()
            .putInt(KEY_TODAY_STEPS, sanitized)
            .putInt(KEY_LAST_SENSOR_READING, -1)
            .putString(KEY_LAST_RECORDED_DATE, today)
            .apply()
        _steps.value = sanitized
    }

    /**
     * Resets the persisted step tracking data upon user confirmation.
     */
    fun resetSteps() {
        val today = getTodayDateString()
        prefs.edit()
            .putInt(KEY_TODAY_STEPS, 0)
            .putInt(KEY_LAST_SENSOR_READING, -1)
            .putString(KEY_LAST_RECORDED_DATE, today)
            .apply()
        _steps.value = 0
    }

    companion object {
        private const val KEY_TODAY_STEPS = "key_today_steps"
        private const val KEY_LAST_SENSOR_READING = "key_last_sensor_reading"
        private const val KEY_LAST_RECORDED_DATE = "key_last_recorded_date"
    }
}