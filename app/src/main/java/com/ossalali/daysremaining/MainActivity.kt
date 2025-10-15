package com.ossalali.daysremaining

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ossalali.daysremaining.presentation.ui.MainScreen
import com.ossalali.daysremaining.widget.EventWidget.Companion.EVENT_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
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
        // Request notification permission on Android 13+ when app starts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          val notificationPermissionState =
              rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

          LaunchedEffect(Unit) {
            if (!notificationPermissionState.status.isGranted) {
              // Request permission when app starts
              notificationPermissionState.launchPermissionRequest()
            }
          }
        }

        Box(modifier = Modifier.fillMaxSize()) {
          MainScreen(
              eventId = eventId?.toLong(),
              shouldNavigateToAddEvent = shouldNavigateToAddEvent,
          )
        }
      }
    }
  }
}
