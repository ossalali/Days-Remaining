package com.ossalali.daysremaining.infrastructure

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ossalali.daysremaining.model.EventNotificationTrigger

@Dao
interface EventNotificationTriggerDao {
  @Query("SELECT * FROM event_notification_triggers WHERE eventId = :eventId AND enabled = 1")
  suspend fun getEnabledTriggersByEvent(eventId: Int): List<EventNotificationTrigger>

  @Query("SELECT * FROM event_notification_triggers WHERE eventId = :eventId")
  suspend fun getAllTriggersByEvent(eventId: Int): List<EventNotificationTrigger>

  @Upsert suspend fun upsertTriggers(triggers: List<EventNotificationTrigger>)

  @Upsert suspend fun upsertTrigger(trigger: EventNotificationTrigger)

  @Query("DELETE FROM event_notification_triggers WHERE eventId = :eventId")
  suspend fun deleteTriggersForEvent(eventId: Int)

  @Query("DELETE FROM event_notification_triggers WHERE id = :id")
  suspend fun deleteTriggerById(id: Int)

  @Query("DELETE FROM event_notification_triggers WHERE eventId IN (:eventIds)")
  suspend fun deleteTriggersForEvents(eventIds: List<Int>)
}
