package com.ossalali.daysremaining.widget.di

import com.ossalali.daysremaining.infrastructure.EventRepository
import com.ossalali.daysremaining.settings.SettingsRepository
import com.ossalali.daysremaining.widget.datastore.WidgetDataStore
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetRepositoryEntryPoint {
  fun eventRepo(): EventRepository

  fun settingsRepo(): SettingsRepository

  fun widgetDataStore(): WidgetDataStore
}
