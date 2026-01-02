package com.ossalali.daysremaining.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Immutable
@Entity(
    foreignKeys =
        [
            ForeignKey(
                entity = EventItem::class,
                parentColumns = ["id"],
                childColumns = ["eventItemId"],
            )
        ],
    indices = [Index(value = ["id"]), Index(value = ["eventItemId"])],
)
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventItemId: Int,
    val dateTime: LocalDateTime,
)
