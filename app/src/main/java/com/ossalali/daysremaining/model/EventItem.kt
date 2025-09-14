package com.ossalali.daysremaining.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

@Entity(
    indices =
        [
            Index(value = ["isArchived"]),
            Index(value = ["date"]),
            Index(value = ["title"]),
            Index(value = ["description"]),
        ])
data class EventItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: LocalDate,
    val description: String,
    val imageUri: String? = null,
    val isArchived: Boolean = false,
) {
  fun getNumberOfDays(customDateNotation: Boolean): String {
    val today = LocalDate.now()
    return if (customDateNotation) {
      getDateNotation(today, date)
    } else {
      ChronoUnit.DAYS.between(today, date).toString()
    }
  }

  companion object {
    fun getDateNotation(from: LocalDate, to: LocalDate): String {
      val start = if (to.isBefore(from)) to else from
      val end = if (to.isBefore(from)) from else to
      val period: Period = Period.between(start, end)

      val years: Int = period.years
      val months: Int = period.months
      val totalDays: Int = period.days
      var weeks: Int = totalDays / 7
      var remainingDays: Int = totalDays % 7

      // - Round up to a week for year+days when months are zero and days are close to a week
      if (years > 0 && months == 0 && weeks == 0 && remainingDays >= 5) {
        weeks = 1
        remainingDays = 0
      }

      // - For exactly one month with no additional days, display "1 month and 1 day"
      if (years == 0 && months == 1 && totalDays == 0) {
        weeks = 0
        remainingDays = 1
      }

      // - For one month plus at least one week, drop leftover days when there are 2 or more
      if (years == 0 && months == 1 && weeks >= 1 && remainingDays >= 2) {
        remainingDays = 0
      }

      fun unit(value: Int, suffix: String): String? {
        if (value <= 0) return null
        return "$value$suffix"
      }

      val parts =
          listOfNotNull(
              unit(years, "y"),
              unit(months, "m"),
              unit(weeks, "w"),
              unit(remainingDays, "d"),
          )

      if (parts.isEmpty()) return "0d"

      val isPast = to.isBefore(from)
      val suffix = if (isPast) " ago" else ""

      return parts.joinToString(separator = " ") + suffix
    }
  }
}
