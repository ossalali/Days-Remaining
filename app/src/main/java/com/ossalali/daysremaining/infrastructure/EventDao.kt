package com.ossalali.daysremaining.infrastructure

import androidx.room.Dao
import androidx.room.Insert
import com.ossalali.daysremaining.model.Event

@Dao
interface EventDao {
    @Insert
    fun insertEvent(event: Event)
}