package com.ossalali.daysremaining.widget.di

import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.widget.datastore.WidgetDataStore // Add this import
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetRepositoryEntryPoint {
    fun eventRepo(): EventRepo
    fun widgetDataStore(): WidgetDataStore // Add this function
}