package com.pocketflow.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pocketflow.app.data.AppState
import com.pocketflow.app.navigation.PocketFlowNavGraph
import com.pocketflow.app.ui.theme.PocketFlowTheme

/**
 * Single-activity entry point. Initialises the Room-backed [AppState] before
 * setting Compose content so screens can read state synchronously.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        enableEdgeToEdge()

        // Open / migrate Room database and start observing its flows
        AppState.init(applicationContext)

        setContent {
            PocketFlowTheme {
                PocketFlowNavGraph()
            }
        }
    }

    private companion object {
        const val TAG = "PocketFlowMain"
    }
}
