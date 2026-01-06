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
                onDelete = ForeignKey.CASCADE
            )
        ],
    indices = [Index(value = ["id"]), Index(value = ["eventItemId"])],
)
data class Reminder(
    @PrimaryKey val id: Int = generateId(),
    val eventItemId: Int,
    val dateTime: LocalDateTime,
) {
    companion object {
        private var lastTimestamp: Long = 0
        private var sequence: Int = 0
        private const val EPOCH = 1767225600000L // 2026-01-01 00:00:00 UTC -> NEVER CHANGE

        // written by Claude Opus 4.5
        @Synchronized
        fun generateId(): Int {
            var timestamp = System.currentTimeMillis() - EPOCH
            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) and 0xFFF // 12 bits for sequence
                if (sequence == 0) {
                    while (timestamp <= lastTimestamp) {
                        timestamp = System.currentTimeMillis() - EPOCH
                    }
                }
            } else {
                sequence = 0
            }
            lastTimestamp = timestamp
            // Combine lower 19 bits of timestamp with 12 bits of sequence
            // Result is always positive (31 bits available in Int)
            return ((timestamp.toInt() and 0x7FFFF) shl 12) or sequence
        }
    }
}
