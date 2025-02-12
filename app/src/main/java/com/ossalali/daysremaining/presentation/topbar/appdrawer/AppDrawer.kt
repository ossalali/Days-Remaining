package com.ossalali.daysremaining.presentation.topbar.appdrawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ossalali.daysremaining.BuildConfig
import com.ossalali.daysremaining.model.DrawerItem
import com.ossalali.daysremaining.presentation.topbar.options.AppDrawerOptions

@Composable
fun AppDrawer(
    currentScreen: String,
    onScreenSelected: (String) -> Unit,
) {
    val drawerItems = mutableListOf(
        DrawerItem(AppDrawerOptions.Home.name, "Home", Icons.Filled.Home),
        DrawerItem(AppDrawerOptions.Archive.name, "Archive", Icons.Filled.Archive),
        DrawerItem(AppDrawerOptions.Settings.name, "Settings", Icons.Filled.Settings)
    )

    if (BuildConfig.DEBUG) {
        drawerItems.add(
            DrawerItem(
                AppDrawerOptions.DEBUG.name,
                "Debug",
                Icons.Filled.AdminPanelSettings
            )
        )
    }

    ModalDrawerSheet {
        drawerItems.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentScreen == item.id,
                onClick = { onScreenSelected(item.id) }
            )
        }
    }
}