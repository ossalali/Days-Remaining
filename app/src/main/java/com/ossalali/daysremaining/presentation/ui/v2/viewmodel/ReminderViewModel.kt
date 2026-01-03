package com.ossalali.daysremaining.presentation.ui.v2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.infrastructure.ReminderRepository
import com.ossalali.daysremaining.model.Reminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(private val reminderRepository: ReminderRepository) :
    ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    fun load(eventId: Int) {
        viewModelScope.launch {
            val remindersForEvent = reminderRepository.getRemindersForEvent(eventId)
            _state.value = State(reminders = remindersForEvent)
        }
    }

    fun updateReminders(reminders: ImmutableList<Reminder>) {
        viewModelScope.launch { reminderRepository.upsert(reminders) }
    }

    data class State(val reminders: ImmutableList<Reminder> = persistentListOf())
}
