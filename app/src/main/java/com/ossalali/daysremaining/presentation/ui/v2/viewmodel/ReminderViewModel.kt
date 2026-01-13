package com.ossalali.daysremaining.presentation.ui.v2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.infrastructure.ReminderRepository
import com.ossalali.daysremaining.model.Reminder
import com.ossalali.daysremaining.presentation.ui.v2.eventdetails.PendingRemindersHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val pendingRemindersHolder: PendingRemindersHolder
) :
    ViewModel() {

    private val _state = MutableStateFlow<ReminderState>(ReminderState.Empty)
    val state: StateFlow<ReminderState> = _state.asStateFlow()

    fun load(eventId: Int) {
        viewModelScope.launch {
            val remindersForEvent = reminderRepository.getRemindersForEvent(eventId)
            _state.value = ReminderState.Loaded(reminders = remindersForEvent)
        }
    }

    fun replaceReminders(reminders: ImmutableList<Reminder>, eventId: Int) {
        viewModelScope.launch { reminderRepository.replace(reminders, eventId) }
    }

    fun setReminders(eventId: Int, reminders: ImmutableList<Reminder>) {
        pendingRemindersHolder.setPendingReminders(eventId, reminders)
    }
    // TODO: check how to use the observer to receive the reminders from ReminderScreen to the EventDetails2 when in AddMode

    fun getReminderObserver(eventId: Int): Flow<List<Reminder>?> =
        pendingRemindersHolder.observePendingReminders(eventId)

    sealed interface ReminderState {
        data object Empty : ReminderState

        data class Loaded(val reminders: ImmutableList<Reminder> = persistentListOf()) :
            ReminderState
    }
}
