package com.ossalali.daysremaining.presentation.ui.nav3example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.glance.text.Text
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.ossalali.daysremaining.model.EventItem

data object EventsScreen

data class EventDetailsScreen(val eventItem: EventItem)

@Composable
fun NavExample() {
    val backStack = remember { mutableStateListOf<Any>(EventsScreen) }
    NavDisplay(
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                is EventsScreen -> NavEntry(key) { EventsScreen(backStack) }
                is EventDetailsScreen -> NavEntry(key) { EventDetailsScreenV2(key.eventItem) }
                else -> NavEntry(Unit) { Text("Unknown route") }
            }
        })
}
