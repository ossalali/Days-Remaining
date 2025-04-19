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
import com.ossalali.daysremaining.presentation.mainscreen.Destinations

@Composable
fun AppDrawer(
    currentScreen: String,
    onScreenSelected: (String) -> Unit,
) {
    val drawerItems = mutableListOf(
        DrawerItem(Destinations.EVENT_LIST, "Home", Icons.Filled.Home),
        DrawerItem(Destinations.ARCHIVE, "Archive", Icons.Filled.Archive),
        DrawerItem(Destinations.SETTINGS, "Settings", Icons.Filled.Settings)
    )

    if (BuildConfig.DEBUG) {
        drawerItems.add(
            DrawerItem(
                Destinations.DEBUG,
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