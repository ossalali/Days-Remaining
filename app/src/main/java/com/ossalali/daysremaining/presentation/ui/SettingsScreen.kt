package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  viewModel: SettingsViewModel = hiltViewModel(),
  paddingValues: PaddingValues = PaddingValues(),
) {
    val notificationEnabled = viewModel.notificationsEnabled.collectAsState().value
    val autoArchiveEnabled = viewModel.autoArchiveEnabled.collectAsState().value
    Surface(
      modifier = Modifier.fillMaxSize().padding(paddingValues),
      color = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(Dimensions.default)) {
            SettingItemDropDown(
              title = "Theme",
              description = "Set the App Theme",
              checked = false, // Should come from viewModel
              onCheckedChange = { /* Implement in viewModel */ },
            )

            SettingItemBoolean(
              title = "Notifications",
              description = "Enable notification reminders",
              checked = notificationEnabled,
              onCheckedChange = { viewModel.toggleNotifications() },
            )

            SettingItemBoolean(
              title = "Automatically archive events",
              description = "Archive events after they've passed",
              checked = autoArchiveEnabled,
              onCheckedChange = { viewModel.toggleAutoArchive() },
            )
        }
    }
}

@Composable
fun SettingItemBoolean(
  title: String,
  description: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        ListItem(
          headlineContent = { Text(text = title) },
          supportingContent = { Text(text = description) },
          trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) },
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun SettingItemDropDown(
  title: String,
  description: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        ListItem(
          headlineContent = { Text(text = title) },
          supportingContent = { Text(text = description) },
          trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) },
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
