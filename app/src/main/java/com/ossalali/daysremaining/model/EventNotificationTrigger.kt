package com.ossalali.daysremaining.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class NotificationTriggerType {
  RELATIVE, // "X days/weeks/months before"
  ON_COMPLETION // "When event occurs"
}

enum class RelativeUnit {
  DAYS,
  WEEKS,
  MONTHS
}

@Entity(
    tableName = "event_notification_triggers",
    indices = [Index(value = ["eventId"]), Index(value = ["enabled"])])
data class EventNotificationTrigger(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int,
    val type: NotificationTriggerType,
    val unit: RelativeUnit?,
    val step: Int?,
    val enabled: Boolean = true,
)
