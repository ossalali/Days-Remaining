package com.ossalali.daysremaining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import com.ossalali.daysremaining.presentation.ui.MainScreen
import com.ossalali.daysremaining.widget.EventWidget.Companion.EVENT_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        var eventId: Int? = null
        var shouldNavigateToAddEvent = false
        val invalidEventId = -1

        if (intent?.hasExtra(EVENT_ID) == true) {
            val idFromIntent = intent.getIntExtra(EVENT_ID, invalidEventId)
            if (idFromIntent != invalidEventId) {
                eventId = idFromIntent
            }
        }

        if (intent?.action == "com.ossalali.daysremaining.action.ADD_EVENT") {
            shouldNavigateToAddEvent = true
        }

        if (eventId == null && savedInstanceState != null) {
            if (savedInstanceState.containsKey(EVENT_ID)) {
                val idFromBundle = savedInstanceState.getInt(EVENT_ID, invalidEventId)
                if (idFromBundle != invalidEventId) {
                    eventId = idFromBundle
                }
            }
        }

        setContent {
            MyAppTheme {
                MainScreen(
                    eventId = eventId?.toLong(),
                    shouldNavigateToAddEvent = shouldNavigateToAddEvent,
                )
            }
        }
    }
}
