package com.ossalali.daysremaining.presentation.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ossalali.daysremaining.presentation.event.EventViewModel
import com.ossalali.daysremaining.presentation.topbar.options.VertOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
    onStartSearch: () -> Unit,
    onDrawerClick: () -> Unit,
    eventViewModel: EventViewModel
) {
    CenterAlignedTopAppBar(
        title = { Text("Events") },
        navigationIcon = {
            if (eventViewModel.selectedEventIds.isEmpty()) {
                IconButton(onClick = onDrawerClick) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu"
                    )
                }
            }
        },
        actions = {
            if (eventViewModel.selectedEventIds.isEmpty()) {
                IconButton(onClick = onStartSearch) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Start Search"
                    )
                }
            } else {
                MoreVertMenu {
                    when (it) {
                        VertOptions.Archive -> eventViewModel.archiveEvent()
                        VertOptions.Delete -> eventViewModel.deleteEvent()
                    }
                }
            }
        }
    )
}