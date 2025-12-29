package com.ossalali.daysremaining.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Central definition of all navigation destinations in the app. Each route is a serializable NavKey
 * for use with Navigation 3.
 */
@Immutable
sealed interface DaysRoute : NavKey

@Serializable
data object EventListRoute : DaysRoute

@Serializable
data class EventDetailsRoute(val eventId: Int, val isAddMode: Boolean = false) : DaysRoute

@Serializable
data object SettingsRoute : DaysRoute

@Serializable
data object DebugRoute : DaysRoute

@Serializable
data object AddEventRoute : DaysRoute
