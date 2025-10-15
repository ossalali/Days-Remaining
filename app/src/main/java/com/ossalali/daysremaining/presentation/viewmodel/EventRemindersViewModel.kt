package com.ossalali.daysremaining.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventNotificationScheduler
import com.ossalali.daysremaining.infrastructure.EventNotificationTriggerRepository
import com.ossalali.daysremaining.infrastructure.appLogger
import com.ossalali.daysremaining.model.EventNotificationTrigger
import com.ossalali.daysremaining.model.NotificationTriggerType
import com.ossalali.daysremaining.model.RelativeUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing event reminder triggers.
 *
 * CRITICAL: This ViewModel is activity-scoped and shared across navigation. It implements state
 * tracking to prevent reminder leaks between events.
 *
 * Key design decisions:
 * - Tracks _currentEventId to detect when user switches events
 * - Maintains two separate states: _triggers (saved) and _pending (draft)
 * - Clears state when switching to prevent data leaks
 * - commitWithTriggers() accepts pre-captured state to prevent race conditions
 */
@HiltViewModel
class EventRemindersViewModel
@Inject
constructor(
    @param:ApplicationContext private val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val triggerRepository: EventNotificationTriggerRepository,
    private val scheduler: EventNotificationScheduler,
) : ViewModel() {
  private var _currentEventId: Int? = null
  private val triggers = MutableStateFlow<List<EventNotificationTrigger>>(emptyList())
  private val _pending = MutableStateFlow<List<EventNotificationTrigger>>(emptyList())
  val pending: StateFlow<List<EventNotificationTrigger>> = _pending.asStateFlow()
  private val isLoading = MutableStateFlow(false)

  /** Load reminders for existing event. CRITICAL: Clears state when switching to prevent leaks. */
  fun load(eventId: Int) {
    viewModelScope.launch(ioDispatcher) {
      isLoading.value = true
      try {
        if (_currentEventId != null && _currentEventId != eventId) {
          triggers.value = emptyList()
          _pending.value = emptyList()
        }

        _currentEventId = eventId
        val loadedTriggers = triggerRepository.getEnabledTriggersByEvent(eventId)
        triggers.value = loadedTriggers
        _pending.value = loadedTriggers
      } catch (e: Exception) {
        appLogger()
            .e(tag = TAG, message = "Error loading triggers for event $eventId", throwable = e)
        triggers.value = emptyList()
        _pending.value = emptyList()
      } finally {
        isLoading.value = false
      }
    }
  }

  /** Load reminders for NEW event (id = 0). */
  fun loadForNewEvent() {
    _currentEventId = 0
    triggers.value = emptyList()
    _pending.value = emptyList()
  }

  /** Add relative reminder (e.g., "7 days before"). */
  fun addRelativeTrigger(eventId: Int, unit: RelativeUnit, step: Int) {
    val tempId = (_pending.value.maxOfOrNull { it.id } ?: 0) + 1
    val trigger =
        EventNotificationTrigger(
            id = tempId,
            eventId = eventId,
            type = NotificationTriggerType.RELATIVE,
            unit = unit,
            step = step.coerceAtLeast(1),
            enabled = true,
        )
    _pending.value = _pending.value + trigger
  }

  /** Add completion reminder ("notify when event occurs"). */
  fun addCompletionTrigger(eventId: Int) {
    val tempId = (_pending.value.maxOfOrNull { it.id } ?: 0) + 1
    val trigger =
        EventNotificationTrigger(
            id = tempId,
            eventId = eventId,
            type = NotificationTriggerType.ON_COMPLETION,
            unit = null,
            step = null,
            enabled = true,
        )
    _pending.value = _pending.value + trigger
  }

  /** Remove reminder from pending list. */
  fun removePendingAt(index: Int) {
    if (index < 0) return
    val list = _pending.value.toMutableList()
    if (index >= list.size) return
    list.removeAt(index)
    _pending.value = list
  }

  /**
   * CRITICAL METHOD: Save reminders with pre-captured state. This prevents race conditions when
   * user navigates away quickly.
   *
   * The UI MUST capture pending state BEFORE navigation, then pass it here. If we read
   * _pending.value inside this method, it may already be cleared by navigation to another event.
   *
   * @param eventId The event ID to save triggers for
   * @param triggersToSave Pre-captured list of triggers (captured at button-click time)
   */
  fun commitWithTriggers(eventId: Int, triggersToSave: List<EventNotificationTrigger>) {
    viewModelScope.launch(ioDispatcher) {
      try {
        triggerRepository.deleteTriggersForEvent(eventId)
        val normalized = triggersToSave.map { it.copy(eventId = eventId, id = 0) }
        triggerRepository.upsertTriggers(normalized)

        if (_currentEventId == eventId) {
          triggers.value = triggerRepository.getEnabledTriggersByEvent(eventId)
        }

        scheduler.rescheduleAllForEvent(context, eventId)
      } catch (e: Exception) {
        appLogger()
            .e(tag = TAG, message = "Error committing triggers for event $eventId", throwable = e)
      }
    }
  }

  /** Check if there are unsaved changes. */
  fun hasUnsavedChanges(): Boolean {
    return triggers.value != _pending.value
  }

  companion object {
    private const val TAG = "EventRemindersViewModel"
  }
}
