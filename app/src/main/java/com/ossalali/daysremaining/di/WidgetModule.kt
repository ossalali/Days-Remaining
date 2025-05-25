package com.ossalali.daysremaining.di

import android.content.Context
import com.ossalali.daysremaining.widget.datastore.WidgetDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WidgetModule {

    @Provides
    @Singleton
    fun provideWidgetDataStore(@ApplicationContext context: Context): WidgetDataStore {
        return WidgetDataStore(context)
    }
}
