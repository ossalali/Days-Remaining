package com.ossalali.daysremaining.infrastructure

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ossalali.daysremaining.model.NotificationTriggerType
import com.ossalali.daysremaining.model.RelativeUnit

@Entity(
    tableName = "eventnotificationtrigger",
    indices = [Index(value = ["eventId"]), Index(value = ["enabled"])]
)
data class EventNotificationTrigger(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int,
    val type: NotificationTriggerType,
    val unit: RelativeUnit? = null,
    val step: Int? = null,
    val enabled: Boolean = true,
)


