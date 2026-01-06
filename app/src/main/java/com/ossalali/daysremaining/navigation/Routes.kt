package com.ossalali.daysremaining.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import com.ossalali.daysremaining.presentation.ui.v2.model.EventUiModel
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
data class EventDetailsRoute(val eventId: Int? = null, val eventUiModel: EventUiModel? = null) :
    DaysRoute

@Serializable
data class ReminderRoute(val eventId: Int) : DaysRoute

@Serializable
data object SettingsRoute : DaysRoute

@Serializable
data object DebugRoute : DaysRoute

@Serializable
data object AddEventRoute : DaysRoute
