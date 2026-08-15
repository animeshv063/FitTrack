package com.example.fittrack.data.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundAlarmManager(private val context: Context) {
    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Plays physical alarm ringtone sound + looping vibration when Rest Timer finishes.
     */
    fun playAlarmSound() {
        try {
            stopAlarmSound()
            var uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (uri == null) {
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
            if (uri == null) {
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }
            ringtone = RingtoneManager.getRingtone(context.applicationContext, uri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ringtone?.isLooping = true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ringtone?.audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            }
            ringtone?.play()

            // Trigger physical hardware vibration
            getVibrator()
            val pattern = longArrayOf(0, 800, 400, 800, 400)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: Physical tone synthesizer
            playTone(ToneGenerator.TONE_CDMA_HIGH_L, 800)
        }
    }

    /**
     * Plays an audible physical confirmation beep + haptic pulse when completing a set.
     */
    fun playSetCompletedSound() {
        try {
            playTone(ToneGenerator.TONE_PROP_BEEP, 150)
            vibrateOnce(80)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Plays a triumphant, audible celebratory fanfare sound + pulse when completing a workout or reaching a milestone.
     */
    fun playWorkoutCompletedSound() {
        try {
            // Play physical fanfare tone
            playTone(ToneGenerator.TONE_CDMA_CONFIRM, 450)
            vibrateOnce(350)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Plays a physical beep for countdowns or starting/stopping stopwatch.
     */
    fun playTimerTickBeep() {
        try {
            playTone(ToneGenerator.TONE_PROP_BEEP2, 100)
            vibrateOnce(40)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playTone(toneType: Int, durationMs: Int) {
        try {
            if (toneGenerator == null) {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            }
            toneGenerator?.startTone(toneType, durationMs)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getVibrator(): Vibrator? {
        if (vibrator == null) {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        }
        return vibrator
    }

    private fun vibrateOnce(durationMs: Long) {
        try {
            val v = getVibrator()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v?.vibrate(durationMs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopAlarmSound() {
        try {
            ringtone?.stop()
            ringtone = null
            vibrator?.cancel()
            vibrator = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
