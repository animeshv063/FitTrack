# ⚡ FitTrack — Modern Fitness, Workout & Pedometer Tracker

<div align="center">

![FitTrack Banner](https://img.shields.io/badge/FitTrack-AMOLED%20Fitness-00FFA3?style=for-the-badge&logo=android&logoColor=black)
![Android](https://img.shields.io/badge/Platform-Android%2014%2B-3DDC84?style=for-the-badge&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-Jetpack%20Compose-7F52FF?style=for-the-badge&logo=kotlin)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-00F2FE?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)

<p align="center">
  <b>A state-of-the-art Android fitness companion built with Jetpack Compose, Room SQLite database, and real-time hardware pedometer tracking.</b>
</p>

</div>

---

## 🌟 Highlights & Key Features

### 🏋️‍♂️ Dynamic Training & Workout Logging
- **Custom Workout Routines**: Create, edit, and organize workouts with targeted exercises, reps, sets, and rest intervals.
- **Interactive Stopwatch & Rest Timer**: Integrated countdown timers and sound alerts upon set completion.
- **Session History & Volume Tracking**: Detailed session logs tracking total volume (kg), completed sets, and session duration.
- **Personal Records (PRs)**: Auto-detected PR milestones for every exercise.

### 🚶 Intelligent Hardware Pedometer & Activity Streak
- **Battery-Efficient Step Tracking**: Leverages device hardware step sensors (`Sensor.TYPE_STEP_COUNTER` and `Sensor.TYPE_STEP_DETECTOR`) with optimized background service (`StepTrackerService`).
- **Midnight Rollover & Persistence**: Seamless date rollover at midnight (`00:00`) with persistent SQLite history.
- **Anomaly Detection & Calibration**: Built-in sanity checks preventing false spikes or reboot jumps, with tap-to-calibrate controls.
- **Activity Streak Heatmap**: Interactive visual calendar heatmap displaying daily workouts and steps logged for any day.

### 💧 Smart Hydration Logger
- **Quick Logging**: Log water intake in presets (`250ml`, `500ml`, `750ml`, `1000ml`) or custom quantities.
- **Progress Tracking**: Dynamic hydration progress bar and daily goal customization.

### 🎨 Premium AMOLED UI / UX
- **Deep Black AMOLED Aesthetic**: High-contrast, battery-friendly dark theme tailored for OLED displays.
- **Ambient Aurora Waves**: Toggleable ambient glowing background animation.
- **Smooth Animations**: Jetpack Compose transitions, celebration confetti, and audio feedback.

### 🔕 Non-Intrusive Notifications
- **Strict 12-Hour Rate Limit**: Background notifications are strictly restricted to **at most twice a day** (once every 12 hours) with zero notification spam.
- **Silent & Low-Priority**: Notification channel runs silently with `setOnlyAlertOnce(true)`.

---

## 🛠️ Architecture & Tech Stack

```
FitTrack/
├── app/
│   └── src/main/java/com/example/fittrack/
│       ├── data/
│       │   ├── local/
│       │   │   ├── database/     # Room Database instance & migrations
│       │   │   ├── dao/          # WorkoutDao with reactive Flow queries
│       │   │   └── entity/       # Room entities (Workouts, DailySteps, Goals, UserProfile, etc.)
│       │   ├── repository/       # Offline-first repository pattern
│       │   └── sensor/           # StepCounterManager & StepTrackerService
│       ├── presentation/
│       │   ├── components/       # Reusable Compose cards, buttons, dialogs
│       │   ├── navigation/       # Jetpack Compose Navigation routes
│       │   ├── screens/          # Home, Workout, Analytics, Profile screens
│       │   ├── theme/            # AMOLED color tokens, typography, shapes
│       │   └── viewmodel/        # WorkoutViewModel & Factory
│       └── MainActivity.kt       # Application entry point & permission flow
```

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (100% declarative UI)
- **Language**: Kotlin with Coroutines & StateFlow
- **Database**: [AndroidX Room](https://developer.android.com/training/data-storage/room) (SQLite) with auto-migration support
- **Sensors**: Android Hardware Sensor Manager & Foreground Health Service
- **Audio / Haptics**: Custom `SoundAlarmManager` with low-latency completion sounds

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Android Studio Hedgehog / Iguana / Jellyfish or newer
- **JDK**: Version 17+
- **Android SDK**: Min SDK `26` (Android 8.0), Target SDK `34` (Android 14)
- **Device / Emulator**: Physical device recommended for hardware step sensor testing

### Installation & Build

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/FitTrack.git
   cd FitTrack
   ```

2. **Open in Android Studio**:
   - Open Android Studio -> Select **Open** -> Choose the `FitTrack` directory.
   - Wait for Gradle sync to finish.

3. **Build the Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on Device or Emulator**:
   ```bash
   ./gradlew installDebug
   ```

---

## 🔒 Permissions & Privacy

FitTrack is designed with privacy as a fundamental principle. All user data, workouts, step counts, and profile metrics are stored **100% locally on your device** in SQLite:

- `ACTIVITY_RECOGNITION`: Required to read hardware step counts.
- `POST_NOTIFICATIONS`: Required for Android 13+ background tracking notifications.
- `FOREGROUND_SERVICE` & `FOREGROUND_SERVICE_HEALTH`: Enables continuous background pedometer tracking.

No accounts, cloud servers, or third-party telemetry are required.

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
