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


    private var baseSteps =
        -1


    fun start() {

        stepSensor?.let {

            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )

        }

    }


    override fun onSensorChanged(
        event: SensorEvent?
    ) {

        event?.let {


            val totalSteps =
                it.values[0].toInt()


            if (baseSteps == -1) {

                baseSteps = totalSteps

            }


            _steps.value =
                totalSteps - baseSteps

        }

    }


    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {


    }


}