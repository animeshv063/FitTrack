package com.example.fittrack.data.notification


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat


class NotificationHelper(
    private val context: Context
) {


    private val notificationManager =
        context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager


    fun showRestFinishedNotification() {


        val channel =
            NotificationChannel(
                "rest_channel",
                "Rest Timer",
                NotificationManager.IMPORTANCE_HIGH
            )


        notificationManager.createNotificationChannel(
            channel
        )


        val notification =
            NotificationCompat.Builder(
                context,
                "rest_channel"
            )
                .setContentTitle(
                    "Rest Finished 💪"
                )
                .setContentText(
                    "Time for your next set"
                )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .build()


        notificationManager.notify(
            1,
            notification
        )


    }

}

