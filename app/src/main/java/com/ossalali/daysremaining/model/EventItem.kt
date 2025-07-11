package com.ossalali.daysremaining.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity(
  indices =
    [
      Index(value = ["isArchived"]),
      Index(value = ["date"]),
      Index(value = ["title"]),
      Index(value = ["description"]),
    ]
)
data class EventItem(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val title: String,
  val date: LocalDate,
  val description: String,
  val isArchived: Boolean = false,
) {
    val numberOfDays: Long by lazy {
        val today = LocalDate.now()
        ChronoUnit.DAYS.between(today, date)
    }
}
