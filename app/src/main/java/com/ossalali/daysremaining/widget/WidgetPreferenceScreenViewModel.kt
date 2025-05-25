package com.ossalali.daysremaining.widget

import android.appwidget.AppWidgetManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SizeF
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.widget.datastore.WidgetDataStore // Added import
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch // Added import

@HiltViewModel(assisted = true)
class WidgetPreferenceScreenViewModel @AssistedInject constructor(
    private val eventRepo: EventRepo,
    private val widgetDataStore: WidgetDataStore, // Injected WidgetDataStore
    @Assisted val appWidgetId: Int,
    @Assisted val appWidgetOptions: Bundle?
) : ViewModel() {

    internal val maxEventsAllowed: Int // Changed to internal for testing

    init {
        Log.d("ViewModel", "appWidgetId: $appWidgetId, options: $appWidgetOptions")
        maxEventsAllowed = getMaxEvents(appWidgetOptions)
        Log.d("ViewModel", "Max events allowed: $maxEventsAllowed")
    }

    private fun getMaxEvents(options: Bundle?): Int {
        if (options == null) {
            Log.d("ViewModel", "AppWidgetOptions is null, defaulting to 8 events.")
            return 8 // Default if no options available
        }

        // Threshold in dp for determining small widget
        val heightThresholdDp = 100 

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val sizes = options.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES, SizeF::class.java)
            if (sizes != null && sizes.isNotEmpty()) {
                Log.d("ViewModel", "Using OPTION_APPWIDGET_SIZES. Sizes: $sizes")
                for (size in sizes) {
                    // Assuming size.height is in dp as per documentation for SizeF for widgets
                    if (size.height < heightThresholdDp) {
                        Log.d("ViewModel", "Small widget detected by OPTION_APPWIDGET_SIZES (height: ${size.height}dp), maxEvents = 2")
                        return 2
                    }
                }
                Log.d("ViewModel", "Widget not considered small by OPTION_APPWIDGET_SIZES, maxEvents = 8")
                return 8
            } else {
                Log.d("ViewModel", "OPTION_APPWIDGET_SIZES is null or empty, falling back to MIN_HEIGHT.")
            }
        }

        // Fallback for older APIs or if SIZES API didn't provide data
        val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
        Log.d("ViewModel", "Using OPTION_APPWIDGET_MIN_HEIGHT: $minHeight dp")
        return if (minHeight < heightThresholdDp) {
            Log.d("ViewModel", "Small widget detected by MIN_HEIGHT ($minHeight dp), maxEvents = 2")
            2
        } else {
            Log.d("ViewModel", "Widget not considered small by MIN_HEIGHT ($minHeight dp), maxEvents = 8")
            8
        }
    }


    fun getEvents(): StateFlow<List<EventItem>> {
        return eventRepo.allEventsAsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    private val _selectedEventIds = mutableStateListOf<Int>()
    val selectedEventIds: List<Int> get() = _selectedEventIds

    fun toggleSelection(eventId: Int) {
        if (_selectedEventIds.contains(eventId)) {
            _selectedEventIds.remove(eventId)
            Log.d("ViewModel", "Event $eventId deselected.")
        } else {
            if (_selectedEventIds.size < maxEventsAllowed) {
                _selectedEventIds.add(eventId)
                Log.d("ViewModel", "Event $eventId selected. Current selection size: ${_selectedEventIds.size}")
            } else {
                Log.d("ViewModel", "Cannot select Event $eventId. Maximum number of events ($maxEventsAllowed) already selected.")
                // Optionally, provide feedback to the user here (e.g., via a StateFlow to the UI)
            }
        }
    }

    fun saveSelectedEvents() {
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            viewModelScope.launch {
                widgetDataStore.saveSelectedEventIds(appWidgetId, selectedEventIds.toList())
                Log.d("ViewModel", "Saved selected event IDs for widget $appWidgetId: $selectedEventIds")
                // TODO: Add logic to trigger widget update after saving (e.g. send broadcast)
                // TODO: Add logic to finish activity after saving
            }
        } else {
            Log.w("ViewModel", "Cannot save selected events, appWidgetId is invalid.")
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(appWidgetId: Int, appWidgetOptions: Bundle?): WidgetPreferenceScreenViewModel
    }
}