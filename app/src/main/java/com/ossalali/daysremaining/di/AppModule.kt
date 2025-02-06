package com.ossalali.daysremaining.di

import android.content.Context
import com.ossalali.daysremaining.infrastructure.EventDao
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.infrastructure.MyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MyDatabase {
        return MyDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideMyDao(database: MyDatabase): EventDao {
        return database.eventDao()
    }

    @Provides
    @Singleton
    fun provideMyRepository(eventDao: EventDao): EventRepo {
        return EventRepo(eventDao)
    }
}