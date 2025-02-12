package com.ossalali.daysremaining.presentation.topbar.appdrawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugScreenViewModel @Inject constructor(
    private val eventRepo: EventRepo,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    fun insertEvents(eventList: MutableList<Event>) {
        viewModelScope.launch(ioDispatcher) {
            eventRepo.insertEvents(eventList)
        }
    }

    suspend fun getNumberOfEvents(): Int {
        return eventRepo.getAllEvents().size
    }
}