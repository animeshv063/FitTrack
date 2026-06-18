package com.example.fittrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fittrack.navigation.AppNavigation
import com.example.fittrack.presentation.theme.FitTrackTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {

            FitTrackTheme {

                AppNavigation()

            }
        }
    }
}