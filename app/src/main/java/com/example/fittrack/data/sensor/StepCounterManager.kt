package com.example.fittrack.data.sensor


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class StepCounterManager(
    context: Context
) : SensorEventListener {


    private val sensorManager =
        context.getSystemService(
            Context.SENSOR_SERVICE
        ) as SensorManager


    private val stepSensor =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_STEP_COUNTER
        )


    private val _steps =
        MutableStateFlow(0)


    val steps: StateFlow<Int> =
        _steps


    fun start() {

        sensorManager.registerListener(
            this,
            stepSensor,
            SensorManager.SENSOR_DELAY_UI
        )

    }


    override fun onSensorChanged(
        event: SensorEvent?
    ) {

        event?.let {

            _steps.value =
                it.values[0].toInt()

        }

    }


    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {

    }


}
