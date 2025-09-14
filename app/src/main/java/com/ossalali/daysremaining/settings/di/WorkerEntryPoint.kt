package com.ossalali.daysremaining.settings.di

import com.ossalali.daysremaining.infrastructure.EventRepository
import com.ossalali.daysremaining.settings.SettingsRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkerEntryPoint {
    fun eventRepo(): EventRepository

    fun settingsRepository(): SettingsRepository
}
