package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.navigation.SettingsRoute
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import com.ossalali.daysremaining.presentation.viewmodel.SettingsViewModel

fun EntryProviderScope<NavKey>.settingsScreen(
    backStack: NavBackStack<NavKey>,
) {
    entry<SettingsRoute> {
        val settingsViewModel = viewModel<SettingsViewModel>(LocalViewModelStoreOwner.current!!)
        val darkModeEnabled =
            settingsViewModel.darkModeEnabled.collectAsStateWithLifecycle()
        val notificationEnabled =
            settingsViewModel.notificationsEnabled.collectAsStateWithLifecycle()
        val autoArchiveEnabled =
            settingsViewModel.autoArchiveEnabled.collectAsStateWithLifecycle()
        val customNotationEnabled =
            settingsViewModel.customDateNotation.collectAsStateWithLifecycle()
        SettingsScreen(
            toggleDarkMode = settingsViewModel::toggleDarkMode,
            toggleNotifications = settingsViewModel::toggleNotifications,
            toggleAutoArchive = settingsViewModel::toggleAutoArchive,
            toggleCustomDateNotation = settingsViewModel::toggleCustomDateNotation,
            darkModeEnabled = darkModeEnabled.value,
            notificationsEnabled = notificationEnabled.value,
            autoArchiveEnabled = autoArchiveEnabled.value,
            customDateNotation = customNotationEnabled.value,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    toggleDarkMode: (Boolean) -> Unit = {},
    toggleNotifications: (Boolean) -> Unit = {},
    toggleAutoArchive: (Boolean) -> Unit = {},
    toggleCustomDateNotation: (Boolean) -> Unit = {},
    darkModeEnabled: Boolean = false,
    notificationsEnabled: Boolean = false,
    autoArchiveEnabled: Boolean = false,
    customDateNotation: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingSize.default)
    ) {
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

        SettingItem(
            title = "Custom date notation (year, month, week, days)",
            description = "If enabled, show the date notation (year, month, week, days) instead of the days remaining",
            checked = customDateNotation,
            onCheckedChange = { checked -> toggleCustomDateNotation(checked) },
        )
    }
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = PaddingSize.half)
    ) {
        ListItem(
            headlineContent = { Text(text = title) },
            supportingContent = { Text(text = description) },
            trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) },
        )
        Spacer(modifier = Modifier.height(PaddingSize.half))
    }
}

@PreviewLightDark
@Composable
fun SettingsScreenPreview() {
    MyAppTheme {
        Surface {
            SettingsScreen()
        }
    }
}