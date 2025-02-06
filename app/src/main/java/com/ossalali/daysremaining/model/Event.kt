package com.ossalali.daysremaining.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Event(
    val id: Int,
    val title: String,
    val date: LocalDate,
    val description: String
) {
    fun getNumberOfDays(): Long {
        val today = LocalDate.now()
        return ChronoUnit.DAYS.between(today, date)
    }
}
