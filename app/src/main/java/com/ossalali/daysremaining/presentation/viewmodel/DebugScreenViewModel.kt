package com.ossalali.daysremaining.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.infrastructure.EventRepository
import com.ossalali.daysremaining.model.EventItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugScreenViewModel @Inject constructor(private val eventRepository: EventRepository) :
    ViewModel() {
    fun insertEvents(eventItemList: List<EventItem>) {
        viewModelScope.launch { eventRepository.insertEvents(eventItemList) }
    }
}
