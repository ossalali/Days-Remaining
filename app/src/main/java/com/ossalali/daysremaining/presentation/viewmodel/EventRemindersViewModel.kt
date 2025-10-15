package com.ossalali.daysremaining.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.App
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventNotificationTrigger
import com.ossalali.daysremaining.infrastructure.EventNotificationTriggerRepository
import com.ossalali.daysremaining.model.NotificationTriggerType
import com.ossalali.daysremaining.model.RelativeUnit
import com.ossalali.daysremaining.presentation.notification.EventNotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventRemindersViewModel
@Inject
constructor(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val triggerRepository: EventNotificationTriggerRepository,
    private val scheduler: EventNotificationScheduler,
) : ViewModel() {

    private val _triggers = MutableStateFlow<List<EventNotificationTrigger>>(emptyList())
    val triggers: StateFlow<List<EventNotificationTrigger>> = _triggers.asStateFlow()

    private val _pending = MutableStateFlow<List<EventNotificationTrigger>>(emptyList())
    val pending: StateFlow<List<EventNotificationTrigger>> = _pending.asStateFlow()

    fun load(eventId: Int) {
        viewModelScope.launch(ioDispatcher) {
            _triggers.value = triggerRepository.getEnabledTriggersByEvent(eventId)
            _pending.value = _triggers.value
        }
    }

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

    fun removeTrigger(eventId: Int, triggerId: Int) {
        _pending.value = _pending.value.filterNot { it.id == triggerId }
    }

    fun updateRelativeTriggerStep(eventId: Int, triggerId: Int, newStep: Int) {
        val target = _pending.value.find { it.id == triggerId }
        if (target != null && target.type == NotificationTriggerType.RELATIVE) {
            val updated = target.copy(step = newStep.coerceAtLeast(1))
            _pending.value = _pending.value.map { if (it.id == triggerId) updated else it }
        }
    }

    fun commit(eventId: Int) {
        viewModelScope.launch(ioDispatcher) {
            // Replace all triggers for this event with pending list
            triggerRepository.deleteTriggersForEvent(eventId)
            val normalized = _pending.value.map { it.copy(eventId = eventId) }
            triggerRepository.upsertTriggers(normalized)
            _triggers.value = triggerRepository.getEnabledTriggersByEvent(eventId)
            scheduler.rescheduleAllForEvent(App.getInstance().applicationContext, eventId)
        }
    }

    fun removePendingAt(index: Int) {
        if (index < 0) return
        val list = _pending.value.toMutableList()
        if (index >= list.size) return
        list.removeAt(index)
        _pending.value = list
    }

    fun updatePendingRelativeStep(index: Int, newStep: Int) {
        val list = _pending.value.toMutableList()
        if (index < 0 || index >= list.size) return
        val tr = list[index]
        if (tr.type != NotificationTriggerType.RELATIVE) return
        list[index] = tr.copy(step = newStep.coerceAtLeast(1))
        _pending.value = list
    }

    fun clearPending() {
        _pending.value = emptyList()
    }
}


