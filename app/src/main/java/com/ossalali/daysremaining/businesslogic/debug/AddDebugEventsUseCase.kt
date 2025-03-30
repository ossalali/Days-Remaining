package com.ossalali.daysremaining.businesslogic.debug

import com.ossalali.daysremaining.model.EventItem
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class AddDebugEventsUseCase @Inject constructor() {
    operator fun invoke(startNumber: Int, numberOfEvents: Int): List<EventItem> {
        val eventItemList = mutableListOf<EventItem>()
        for (i in numberOfEvents..numberOfEvents + startNumber) {
            eventItemList.add(
                EventItem(
                    id = 0,
                    title = "Event $i",
                    description = "Event $i Description",
                    date = LocalDate.now().plus(i.toLong() * 10, ChronoUnit.DAYS),
                )
            )
        }
        return eventItemList
    }
}