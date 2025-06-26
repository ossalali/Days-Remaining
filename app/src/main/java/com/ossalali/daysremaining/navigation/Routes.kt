package com.ossalali.daysremaining.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Central definition of all navigation destinations in the app.
 * Each route is a serializable NavKey for use with Navigation 3.
 */
sealed interface DaysRoute : NavKey

@Serializable
data object EventListRoute : DaysRoute

@Serializable
data class EventDetailsRoute(val eventId: Int) : DaysRoute

@Serializable
data object SettingsRoute : DaysRoute

@Serializable
data object DebugRoute : DaysRoute

@Serializable
data object AddEventRoute : DaysRoute 