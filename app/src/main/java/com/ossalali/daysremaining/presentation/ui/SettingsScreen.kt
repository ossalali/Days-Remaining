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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions
import com.ossalali.daysremaining.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
) {
    SettingsScreenImpl(
        toggleDarkMode = viewModel::toggleDarkMode,
        toggleNotifications = viewModel::toggleNotifications,
        toggleAutoArchive = viewModel::toggleAutoArchive,
        darkModeEnabled = viewModel.darkModeEnabled,
        notificationsEnabled = viewModel.notificationsEnabled,
        autoArchiveEnabled = viewModel.autoArchiveEnabled,
        paddingValues = paddingValues,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenImpl(
    toggleDarkMode: (Boolean) -> Unit,
    toggleNotifications: (Boolean) -> Unit,
    toggleAutoArchive: (Boolean) -> Unit,
    darkModeEnabled: StateFlow<Boolean>,
    notificationsEnabled: StateFlow<Boolean>,
    autoArchiveEnabled: StateFlow<Boolean>,
    paddingValues: PaddingValues = PaddingValues(),
) {
    val darkModeEnabled by darkModeEnabled.collectAsState()
    val notificationsEnabled by notificationsEnabled.collectAsState()
    val autoArchiveEnabled by autoArchiveEnabled.collectAsState()
    Surface(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(Dimensions.default)) {
            SettingItem(
                title = "Dark Mode",
                description = "Enable dark theme",
                checked = darkModeEnabled,
                onCheckedChange = { checked -> toggleDarkMode(checked) },
            )

            SettingItem(
                title = "Notifications",
                description = "Enable notification reminders",
                checked = notificationsEnabled,
                onCheckedChange = { checked -> toggleNotifications(checked) },
            )

            SettingItem(
                title = "Automatically archive events",
                description = "Archive events after they've passed",
                checked = autoArchiveEnabled,
                onCheckedChange = { checked -> toggleAutoArchive(checked) },
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
