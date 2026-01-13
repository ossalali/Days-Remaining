package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import com.ossalali.daysremaining.model.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PendingRemindersHolder @Inject constructor() {
    private val _pendingReminders = MutableStateFlow<Map<Int, List<Reminder>>>(emptyMap())

    fun setPendingReminders(eventId: Int, reminders: List<Reminder>) {
        _pendingReminders.update { it + (eventId to reminders) }
    }

    fun consumePendingReminders(eventId: Int): List<Reminder>? {
        val reminders = _pendingReminders.value[eventId]
        _pendingReminders.update { it - eventId }
        return reminders
    }

    fun observePendingReminders(eventId: Int): Flow<List<Reminder>?> =
        _pendingReminders.map { it[eventId] }
}
