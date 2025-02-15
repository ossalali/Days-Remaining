package com.ossalali.daysremaining.businesslogic.Debug

import com.ossalali.daysremaining.model.Event
import com.ossalali.daysremaining.presentation.topbar.appdrawer.DebugScreenViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class DebugAddEvents @Inject constructor() {
    suspend operator fun invoke(
        debugScreenViewModel: DebugScreenViewModel,
    ) {
        val allEvents = debugScreenViewModel.getNumberOfEvents()
        val eventList = mutableListOf<Event>()
        for (i in allEvents + 1..allEvents + 5) {
            eventList.add(
                Event(
                    id = 0,
                    title = "Event $i",
                    description = "Event $i Description",
                    date = LocalDate.now().plus(i.toLong() * 10, ChronoUnit.DAYS),
                )
            )
        }
        debugScreenViewModel.insertEvents(eventList)
    }
}