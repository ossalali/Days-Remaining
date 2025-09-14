package com.ossalali.daysremaining.widget

import android.appwidget.AppWidgetManager
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.infrastructure.EventRepository
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.settings.SettingsRepository
import com.ossalali.daysremaining.widget.datastore.WidgetDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WidgetPreferenceScreenViewModel(
    private val eventRepository: EventRepository,
    private val settingsRepo: SettingsRepository,
    private val widgetDataStore: WidgetDataStore,
    val appWidgetId: Int,
    val appWidgetOptions: Bundle?,
) : ViewModel() {

  internal val maxEventsAllowed: Int

  init {
    maxEventsAllowed = getMaxEvents(appWidgetOptions)
  }

  private fun getMaxEvents(options: Bundle?): Int {
    if (options == null) {
      return 14
    }

    val heightThresholdDp = 100

    val sizes =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          options.getParcelableArrayList(
              AppWidgetManager.OPTION_APPWIDGET_SIZES,
              SizeF::class.java,
          )
        } else {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            @Suppress("DEPRECATION")
            options.getParcelableArrayList(AppWidgetManager.OPTION_APPWIDGET_SIZES)
          } else {
            null
          }
        }
    if (!sizes.isNullOrEmpty()) {
      for (size in sizes) {
        if (size.height < heightThresholdDp) {
          return 2
        }
      }

      return 14
    }

    val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
    return if (minHeight < heightThresholdDp) {
      2
    } else {
      8
    }
  }

  fun getEvents(): StateFlow<List<EventItem>> {
    return eventRepository.activeEventsAsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )
        .also { stateFlow ->
          viewModelScope.launch {
            stateFlow.collect { events -> events.forEachIndexed { index, event -> } }
          }
        }
  }

  fun getCustomDateNotationSetting(): Boolean {
    return settingsRepo.customDateNotation
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )
        .value
  }

  private val _selectedEventIds = mutableStateListOf<Int>()
  val selectedEventIds: SnapshotStateList<Int>
    get() = _selectedEventIds

  fun toggleSelection(eventId: Int) {
    if (_selectedEventIds.contains(eventId)) {
      _selectedEventIds.remove(eventId)
    } else {
      if (_selectedEventIds.size < maxEventsAllowed) {
        _selectedEventIds.add(eventId)
      }
    }
  }

  suspend fun saveSelectedEvents() {
    if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
      widgetDataStore.saveSelectedEventIds(appWidgetId, selectedEventIds.toList())
    }
  }
}
