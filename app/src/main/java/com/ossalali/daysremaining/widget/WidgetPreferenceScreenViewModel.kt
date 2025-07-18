package com.ossalali.daysremaining.widget

import android.appwidget.AppWidgetManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SizeF
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.widget.datastore.WidgetDataStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class WidgetPreferenceScreenViewModel @AssistedInject constructor(
    private val eventRepo: EventRepo,
    private val widgetDataStore: WidgetDataStore,
    @Assisted val appWidgetId: Int,
    @Assisted val appWidgetOptions: Bundle?
) : ViewModel() {

    internal val maxEventsAllowed: Int

    init {
        Log.d("ViewModel", "appWidgetId: $appWidgetId, options: $appWidgetOptions")
        maxEventsAllowed = getMaxEvents(appWidgetOptions)
        Log.d("ViewModel", "Max events allowed: $maxEventsAllowed")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getMaxEvents(options: Bundle?): Int {
        if (options == null) {
            Log.d("ViewModel", "AppWidgetOptions is null, defaulting to 8 events.")
            return 8
        }

        val heightThresholdDp = 100

        val sizes = options.getParcelableArrayList(
            AppWidgetManager.OPTION_APPWIDGET_SIZES,
            SizeF::class.java
        )
        if (!sizes.isNullOrEmpty()) {
            Log.d("ViewModel", "Using OPTION_APPWIDGET_SIZES. Sizes: $sizes")
            for (size in sizes) {
                if (size.height < heightThresholdDp) {
                    Log.d(
                        "ViewModel",
                        "Small widget detected by OPTION_APPWIDGET_SIZES (height: ${size.height}dp), maxEvents = 2"
                    )
                    return 2
                }
            }
            Log.d(
                "ViewModel",
                "Widget not considered small by OPTION_APPWIDGET_SIZES, maxEvents = 8"
            )
            return 8
        } else {
            Log.d(
                "ViewModel",
                "OPTION_APPWIDGET_SIZES is null or empty, falling back to MIN_HEIGHT."
            )
        }

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
        Log.d("ViewModel", "getEvents() called, setting up StateFlow")
        return eventRepo.activeEventsAsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        ).also { stateFlow ->
            viewModelScope.launch {
                stateFlow.collect { events ->
                    Log.d("ViewModel", "Events collected: ${events.size} events")
                    events.forEachIndexed { index, event ->
                        Log.d(
                            "ViewModel",
                            "Event $index: ${event.title} (id=${event.id}, archived=${event.isArchived})"
                        )
                    }
                }
            }
        }
    }

    private val _selectedEventIds = mutableStateListOf<Int>()
    val selectedEventIds: SnapshotStateList<Int> get() = _selectedEventIds

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
            }
        }
    }

    suspend fun saveSelectedEvents() {
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            widgetDataStore.saveSelectedEventIds(appWidgetId, selectedEventIds.toList())
            Log.d(
                "ViewModel",
                "Saved selected event IDs for widget $appWidgetId: $selectedEventIds"
            )
        } else {
            Log.w("ViewModel", "Cannot save selected events, appWidgetId is invalid.")
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(appWidgetId: Int, appWidgetOptions: Bundle?): WidgetPreferenceScreenViewModel
    }
}