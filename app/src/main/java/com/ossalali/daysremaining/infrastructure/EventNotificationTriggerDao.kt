package com.ossalali.daysremaining.infrastructure

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface EventNotificationTriggerDao {

    @Upsert
    suspend fun upsertTrigger(trigger: EventNotificationTrigger)

    @Upsert
    suspend fun upsertTriggers(triggers: List<EventNotificationTrigger>)

    @Query("SELECT * FROM eventnotificationtrigger WHERE eventId = :eventId AND enabled = 1")
    suspend fun getEnabledTriggersByEvent(eventId: Int): List<EventNotificationTrigger>

    @Query("SELECT * FROM eventnotificationtrigger WHERE eventId IN (:eventIds) AND enabled = 1")
    suspend fun getEnabledTriggersByEventIds(eventIds: List<Int>): List<EventNotificationTrigger>

    @Query("DELETE FROM eventnotificationtrigger WHERE eventId = :eventId")
    suspend fun deleteTriggersForEvent(eventId: Int)

    @Query("DELETE FROM eventnotificationtrigger WHERE eventId IN (:eventIds)")
    suspend fun deleteTriggersForEvents(eventIds: List<Int>)

    @Query("UPDATE eventnotificationtrigger SET enabled = 0 WHERE id IN (:triggerIds)")
    suspend fun disableTriggers(triggerIds: List<Int>)

    @Query("DELETE FROM eventnotificationtrigger WHERE id = :triggerId")
    suspend fun deleteTriggerById(triggerId: Int)
}


