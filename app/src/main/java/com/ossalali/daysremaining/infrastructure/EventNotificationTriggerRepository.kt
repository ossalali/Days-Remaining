package com.ossalali.daysremaining.infrastructure

import javax.inject.Inject

class EventNotificationTriggerRepository
@Inject
constructor(
    private val dao: EventNotificationTriggerDao,
) {

    suspend fun upsertTriggers(triggers: List<EventNotificationTrigger>) {
        dao.upsertTriggers(triggers)
    }

    suspend fun getEnabledTriggersByEvent(eventId: Int): List<EventNotificationTrigger> {
        return dao.getEnabledTriggersByEvent(eventId)
    }

    suspend fun getEnabledTriggersByEventIds(eventIds: List<Int>): List<EventNotificationTrigger> {
        if (eventIds.isEmpty()) return emptyList()
        return dao.getEnabledTriggersByEventIds(eventIds)
    }

    suspend fun deleteTriggerById(triggerId: Int) {
        dao.deleteTriggerById(triggerId)
    }

    suspend fun deleteTriggersForEvent(eventId: Int) {
        dao.deleteTriggersForEvent(eventId)
    }
}
