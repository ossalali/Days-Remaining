package com.ossalali.daysremaining.businesslogic.debug

import com.ossalali.daysremaining.model.EventItem
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class AddDebugEventsUseCase(private val addEvents: (List<EventItem>) -> Unit) {

    operator fun invoke(numberOfEvents: Int) {
        val eventItemList = mutableListOf<EventItem>()
        for (i in 1..numberOfEvents) {
            eventItemList.add(
              EventItem(
                id = 0,
                title = "Event $i",
                description = "Event $i Description",
                date = LocalDate.now().plus(i.toLong() * 10, ChronoUnit.DAYS),
              )
            )
        }

        addEvents(eventItemList)
    }
}
