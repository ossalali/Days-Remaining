package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.model.EventNotificationTrigger
import javax.inject.Inject

class EventNotificationTriggerRepository
@Inject
constructor(private val dao: EventNotificationTriggerDao) {
  suspend fun getEnabledTriggersByEvent(eventId: Int): List<EventNotificationTrigger> =
      dao.getEnabledTriggersByEvent(eventId)

  suspend fun getAllTriggersByEvent(eventId: Int): List<EventNotificationTrigger> =
      dao.getAllTriggersByEvent(eventId)

  suspend fun upsertTriggers(triggers: List<EventNotificationTrigger>) =
      dao.upsertTriggers(triggers)

  suspend fun deleteTriggersForEvent(eventId: Int) = dao.deleteTriggersForEvent(eventId)
}
