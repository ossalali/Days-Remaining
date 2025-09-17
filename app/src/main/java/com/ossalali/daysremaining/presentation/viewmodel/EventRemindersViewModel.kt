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

    fun load(eventId: Int) {
        viewModelScope.launch(ioDispatcher) {
            _triggers.value = triggerRepository.getEnabledTriggersByEvent(eventId)
        }
    }

    fun addRelativeTrigger(eventId: Int, unit: RelativeUnit, step: Int) {
        val trigger =
            EventNotificationTrigger(
                eventId = eventId,
                type = NotificationTriggerType.RELATIVE,
                unit = unit,
                step = step.coerceAtLeast(1),
                enabled = true,
            )
        viewModelScope.launch(ioDispatcher) {
            triggerRepository.upsertTriggers(listOf(trigger))
            _triggers.value = triggerRepository.getEnabledTriggersByEvent(eventId)
            scheduler.rescheduleAllForEvent(App.getInstance().applicationContext, eventId)
        }
    }

    fun addCompletionTrigger(eventId: Int) {
        val trigger =
            EventNotificationTrigger(
                eventId = eventId,
                type = NotificationTriggerType.ON_COMPLETION,
                unit = null,
                step = null,
                enabled = true,
            )
        viewModelScope.launch(ioDispatcher) {
            triggerRepository.upsertTriggers(listOf(trigger))
            _triggers.value = triggerRepository.getEnabledTriggersByEvent(eventId)
            scheduler.rescheduleAllForEvent(App.getInstance().applicationContext, eventId)
        }
    }

    fun removeTrigger(eventId: Int, triggerId: Int) {
        viewModelScope.launch(ioDispatcher) {
            triggerRepository.deleteTriggerById(triggerId)
            _triggers.value = triggerRepository.getEnabledTriggersByEvent(eventId)
            scheduler.rescheduleAllForEvent(App.getInstance().applicationContext, eventId)
        }
    }
}


