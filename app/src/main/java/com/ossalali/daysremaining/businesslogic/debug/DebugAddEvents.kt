package com.ossalali.daysremaining.businesslogic.debug

import com.ossalali.daysremaining.presentation.viewmodel.DebugScreenViewModel
import javax.inject.Inject

class DebugAddEvents @Inject constructor() {
    suspend operator fun invoke(
        debugScreenViewModel: DebugScreenViewModel,
        addDebugEventsUseCase: AddDebugEventsUseCase
    ) {
        val allEvents = debugScreenViewModel.getNumberOfEvents()
        val eventList = addDebugEventsUseCase(allEvents, 5)
        debugScreenViewModel.insertEvents(eventList)
    }
}