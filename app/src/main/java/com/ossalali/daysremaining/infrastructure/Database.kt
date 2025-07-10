package com.ossalali.daysremaining.infrastructure

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ossalali.daysremaining.model.EventItem
import java.util.concurrent.Executors

@Database(entities = [EventItem::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MyDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "myDatabase"
                )
                    .fallbackToDestructiveMigration(false)
                    .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)
                    .setQueryExecutor(Executors.newFixedThreadPool(4))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}