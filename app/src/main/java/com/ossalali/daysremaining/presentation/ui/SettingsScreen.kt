package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SettingItem(
                title = "Dark Mode",
                description = "Enable dark theme",
                checked = false,  // Should come from viewModel
                onCheckedChange = { /* Implement in viewModel */ }
            )

            SettingItem(
                title = "Notifications",
                description = "Enable notification reminders",
                checked = false,  // Should come from viewModel
                onCheckedChange = { /* Implement in viewModel */ }
            )

            SettingItem(
                title = "Automatically archive events",
                description = "Archive events after they've passed",
                checked = false,  // Should come from viewModel
                onCheckedChange = { /* Implement in viewModel */ }
            )
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        ListItem(
            headlineContent = { Text(text = title) },
            supportingContent = { Text(text = description) },
            trailingContent = {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
} 