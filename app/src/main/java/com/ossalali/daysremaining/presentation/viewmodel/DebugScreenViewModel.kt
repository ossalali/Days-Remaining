package com.ossalali.daysremaining.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugScreenViewModel
@Inject
constructor(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val eventRepo: EventRepo,
) : ViewModel() {
    fun insertEvents(eventItemList: List<EventItem>) {
        viewModelScope.launch(ioDispatcher) { eventRepo.insertEvents(eventItemList) }
    }
}
