package com.ossalali.daysremaining.infrastructure

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.model.EventNotificationTrigger
import java.util.concurrent.Executors

@Database(
    entities = [EventItem::class, EventNotificationTrigger::class],
    version = 3,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class MyDatabase : RoomDatabase() {

  abstract fun eventDao(): EventDao

  abstract fun triggerDao(): EventNotificationTriggerDao

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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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

    private val MIGRATION_2_3 =
        object : Migration(2, 3) {
          override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS event_notification_triggers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    eventId INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    unit TEXT,
                    step INTEGER,
                    enabled INTEGER NOT NULL DEFAULT 1
                )
                """
                    .trimIndent())
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_event_notification_triggers_eventId ON event_notification_triggers(eventId)")
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_event_notification_triggers_enabled ON event_notification_triggers(enabled)")
          }
        }
  }
}
