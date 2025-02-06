package com.ossalali.daysremaining.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val date: LocalDate,
    val description: String
) {
    fun getNumberOfDays(): Long {
        val today = LocalDate.now()
        return ChronoUnit.DAYS.between(today, date)
    }
}
