package com.ossalali.daysremaining.presentation.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel =
        hiltViewModel(
            viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "SettingsViewModel"),
    paddingValues: PaddingValues = PaddingValues(),
) {
  // Get notification permission state for Android 13+
  val notificationPermissionState =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
      } else {
        null
      }

  SettingsScreenImpl(
      toggleDarkMode = viewModel::toggleDarkMode,
      toggleAutoArchive = viewModel::toggleAutoArchive,
      toggleCustomDateNotation = viewModel::toggleCustomDateNotation,
      darkModeEnabled = viewModel.darkModeEnabled,
      autoArchiveEnabled = viewModel.autoArchiveEnabled,
      customDateNotation = viewModel.customDateNotation,
      notificationPermissionGranted = notificationPermissionState?.status?.isGranted ?: true,
      onRequestNotificationPermission = { notificationPermissionState?.launchPermissionRequest() },
      shouldShowPermissionRationale =
          notificationPermissionState?.status?.shouldShowRationale ?: false,
      paddingValues = paddingValues,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenImpl(
    toggleDarkMode: (Boolean) -> Unit,
    toggleAutoArchive: (Boolean) -> Unit,
    toggleCustomDateNotation: (Boolean) -> Unit,
    darkModeEnabled: StateFlow<Boolean>,
    autoArchiveEnabled: StateFlow<Boolean>,
    customDateNotation: StateFlow<Boolean>,
    notificationPermissionGranted: Boolean,
    onRequestNotificationPermission: () -> Unit,
    shouldShowPermissionRationale: Boolean,
    paddingValues: PaddingValues = PaddingValues(),
) {
  val darkModeEnabled by darkModeEnabled.collectAsState()
  val autoArchiveEnabled by autoArchiveEnabled.collectAsState()
  val customDateNotation by customDateNotation.collectAsState()
  val context = LocalContext.current

  Surface(
      modifier = Modifier.fillMaxSize().padding(paddingValues),
      color = MaterialTheme.colorScheme.background,
  ) {
    Column(modifier = Modifier.fillMaxSize().padding(Dimensions.default)) {
      // Notification Permission Section (Android 13+)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        NotificationPermissionSection(
            permissionGranted = notificationPermissionGranted,
            onRequestPermission = onRequestNotificationPermission,
            shouldShowRationale = shouldShowPermissionRationale,
            onOpenSettings = {
              val intent =
                  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                  }
              context.startActivity(intent)
            },
        )

        Spacer(modifier = Modifier.height(Dimensions.double))
      }

      SettingItem(
          title = "Dark Mode",
          description = "Enable dark theme",
          checked = darkModeEnabled,
          onCheckedChange = { checked -> toggleDarkMode(checked) },
      )

      SettingItem(
          title = "Automatically archive events",
          description = "Archive events after they've passed",
          checked = autoArchiveEnabled,
          onCheckedChange = { checked -> toggleAutoArchive(checked) },
      )

      SettingItem(
          title = "Custom date notation (year, month, week, days)",
          description =
              "If enabled, show the date notation (year, month, week, days) instead of the days remaining",
          checked = customDateNotation,
          onCheckedChange = { checked -> toggleCustomDateNotation(checked) },
      )
    }
  }
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
  Column(modifier = Modifier.fillMaxWidth().padding(vertical = Dimensions.half)) {
    ListItem(
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = description) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) },
    )
    Spacer(modifier = Modifier.height(Dimensions.half))
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPermissionSection(
    permissionGranted: Boolean,
    onRequestPermission: () -> Unit,
    shouldShowRationale: Boolean,
    onOpenSettings: () -> Unit,
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    ListItem(
        headlineContent = { Text("Notification Permission") },
        supportingContent = {
          Text(
              if (permissionGranted) {
                "Notifications are enabled. You will receive reminders for your events."
              } else {
                "Notifications are disabled. Grant permission to receive event reminders."
              })
        },
        leadingContent = {
          Icon(
              imageVector = Icons.Default.Notifications,
              contentDescription = "Notification Icon",
              tint =
                  if (permissionGranted) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.error,
          )
        },
    )

    if (!permissionGranted) {
      Spacer(modifier = Modifier.height(Dimensions.half))

      if (shouldShowRationale) {
        // User denied permission but didn't select "Don't ask again"
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth().padding(horizontal = Dimensions.default),
        ) {
          Text("Grant Notification Permission")
        }
      } else {
        // User denied permission permanently, need to open settings
        OutlinedButton(
            onClick = onOpenSettings,
            modifier = Modifier.fillMaxWidth().padding(horizontal = Dimensions.default),
        ) {
          Text("Open App Settings")
        }
        Spacer(modifier = Modifier.height(Dimensions.half))
        Text(
            text =
                "Permission was denied. Please enable notifications in app settings to receive reminders.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = Dimensions.default),
        )
      }
    }
  }
}
