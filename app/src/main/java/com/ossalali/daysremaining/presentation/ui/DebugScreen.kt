package com.ossalali.daysremaining.presentation.ui

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ossalali.daysremaining.businesslogic.debug.AddDebugEventsUseCase
import com.ossalali.daysremaining.infrastructure.EventNotificationReceiver
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.DebugScreenViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DebugScreen(
    debugScreenViewModel: DebugScreenViewModel =
        hiltViewModel(LocalViewModelStoreOwner.current!!, "DebugScreenViewModel"),
    paddingValues: PaddingValues = PaddingValues(),
    onClose: () -> Unit,
) {
  var numberOfEvents by rememberSaveable { mutableIntStateOf(0) }
  var textFieldValue by rememberSaveable { mutableStateOf("") }
  val textFieldFocusRequester = remember { FocusRequester() }
  val context = LocalContext.current

  // Permission state for notifications (Android 13+)
  val notificationPermissionState =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
      } else {
        null
      }

  Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
    Column {
      OutlinedTextField(
          value = textFieldValue,
          onValueChange = {
            textFieldValue = it
            numberOfEvents = it.toIntOrNull() ?: 0
          },
          label = { Text("Add Events") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier =
              Modifier.fillMaxWidth()
                  .padding(horizontal = Dimensions.default)
                  .focusRequester(textFieldFocusRequester),
      )
      Spacer(Modifier.height(Dimensions.default))
      FloatingActionButton(
          modifier = Modifier.fillMaxWidth().padding(horizontal = Dimensions.default),
          onClick = {
            AddDebugEventsUseCase(debugScreenViewModel::insertEvents)(numberOfEvents)
            onClose()
          },
      ) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Events")
      }

      Spacer(Modifier.height(Dimensions.double))

      // Test Notification Button
      Button(
          modifier = Modifier.fillMaxWidth().padding(horizontal = Dimensions.default),
          onClick = {
            // Check and request permission if needed (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              if (notificationPermissionState?.status?.isGranted == true) {
                EventNotificationReceiver.showTestNotification(context)
              } else {
                notificationPermissionState?.launchPermissionRequest()
              }
            } else {
              // Permission not needed for older versions
              EventNotificationReceiver.showTestNotification(context)
            }
          },
      ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Send Test Notification")
        Spacer(Modifier.height(Dimensions.half))
        Text("Send Test Notification")
      }
    }
  }
}
