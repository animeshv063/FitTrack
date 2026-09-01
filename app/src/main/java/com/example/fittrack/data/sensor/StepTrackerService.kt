package com.example.fittrack.data.sensor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.fittrack.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

/**
 * StepTrackerService
 * Background foreground service that ensures hardware step counting remains continuously active
 * even when the user exits the app, with intelligent throttling to prevent notification spam.
 */
class StepTrackerService : Service() {

    private lateinit var stepCounterManager: StepCounterManager
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var notificationJob: Job? = null

    private var lastNotifiedSteps: Int = -1
    private var lastNotificationTimeMs: Long = 0L

    override fun onCreate() {
        super.onCreate()
        stepCounterManager = StepCounterManager.getInstance(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val initialSteps = stepCounterManager.steps.value
        lastNotifiedSteps = initialSteps
        lastNotificationTimeMs = System.currentTimeMillis()

        val notification = createNotification(initialSteps)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        stepCounterManager.start()

        // Dynamically update notification text with intelligent throttling
        notificationJob?.cancel()
        notificationJob = serviceScope.launch {
            stepCounterManager.steps.collect { currentSteps ->
                updateNotification(currentSteps)
            }
        }

        return START_STICKY
    }

    /**
     * Updates notification only when significant step progress (>= 500 steps)
     * or sufficient time (>= 5 minutes) has elapsed, preventing frequent notification churn.
     */
    private fun updateNotification(steps: Int, force: Boolean = false) {
        val now = System.currentTimeMillis()
        val stepDiff = abs(steps - lastNotifiedSteps)
        val timeDiff = now - lastNotificationTimeMs

        if (!force && lastNotifiedSteps != -1) {
            val hasEnoughSteps = stepDiff >= STEP_NOTIFICATION_THRESHOLD
            val hasEnoughTime = timeDiff >= TIME_NOTIFICATION_THRESHOLD_MS && stepDiff > 0
            if (!hasEnoughSteps && !hasEnoughTime) {
                return
            }
        }

        lastNotifiedSteps = steps
        lastNotificationTimeMs = now

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(NOTIFICATION_ID, createNotification(steps))
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationJob?.cancel()
        stepCounterManager.stop()
        lastNotifiedSteps = -1
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Step Tracking Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors daily steps continuously in background"
                setShowBadge(false)
                setSound(null, null)
                enableVibration(false)
                enableLights(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(currentSteps: Int): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calories = (currentSteps * 0.04).toInt()
        val distanceKm = currentSteps * 0.00075
        val stepText = String.format(
            Locale.US,
            "%,d steps • %d kcal • %.2f km",
            currentSteps,
            calories,
            distanceKm
        )

        val prefs = getSharedPreferences("fittrack_step_prefs", Context.MODE_PRIVATE)
        val stepGoal = prefs.getInt(KEY_USER_STEP_GOAL, 10000)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FitTrack Pedometer")
            .setContentText(stepText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setShowWhen(false)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (stepGoal > 0) {
            builder.setProgress(stepGoal, currentSteps.coerceAtMost(stepGoal), false)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        }

        return builder.build()
    }

    companion object {
        private const val CHANNEL_ID = "fittrack_step_tracking_channel"
        private const val NOTIFICATION_ID = 1001
        private const val STEP_NOTIFICATION_THRESHOLD = 500
        private const val TIME_NOTIFICATION_THRESHOLD_MS = 300_000L // 5 minutes
        const val KEY_USER_STEP_GOAL = "key_user_step_goal"

        fun startService(context: Context) {
            val intent = Intent(context, StepTrackerService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun stopService(context: Context) {
            try {
                val intent = Intent(context, StepTrackerService::class.java)
                context.stopService(intent)
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                manager?.cancel(NOTIFICATION_ID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

