package com.ossalali.daysremaining.infrastructure

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ossalali.daysremaining.model.EventItem
import java.util.concurrent.Executors

@Database(entities = [EventItem::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MyDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        @Volatile private var INSTANCE: MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase {
            return INSTANCE
                ?: synchronized(this) {
                    val instance =
                        Room.databaseBuilder(
                                context.applicationContext,
                                MyDatabase::class.java,
                                "myDatabase",
                            )
                            .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING)
                            .setQueryExecutor(Executors.newFixedThreadPool(4))
                            .addMigrations(MIGRATION_1_2)
                            .build()
                    INSTANCE = instance
                    instance
                }
        }

        private val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE eventitem ADD COLUMN imageUri TEXT")
                }
            }
    }
}
