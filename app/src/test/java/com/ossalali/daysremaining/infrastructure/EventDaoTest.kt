package com.ossalali.daysremaining.infrastructure

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ossalali.daysremaining.model.EventItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class) // Using Robolectric for Android context
class EventDaoTest {

    private lateinit var eventDao: EventDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        )
            .allowMainThreadQueries() // Allowing main thread queries for simplicity in tests
            .build()
        eventDao = db.eventDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun createEvent(id: Int, title: String, date: LocalDate, isArchived: Boolean = false): EventItem {
        return EventItem(id = id, title = title, date = date, isArchived = isArchived)
    }

    @Test
    fun getEventsByIds_emptyList_returnsEmpty() = runTest {
        val events = eventDao.getEventsByIds(emptyList())
        assertTrue("Expected empty list for empty ID list", events.isEmpty())
    }

    @Test
    fun getEventsByIds_singleId_returnsCorrectEvent() = runTest {
        val event1 = createEvent(1, "Event 1", LocalDate.now().plusDays(10))
        eventDao.insertEvent(event1)

        val events = eventDao.getEventsByIds(listOf(1))
        assertEquals("Expected list of size 1", 1, events.size)
        assertEquals("Expected event1", event1, events[0])
    }

    @Test
    fun getEventsByIds_multipleIds_returnsCorrectEvents() = runTest {
        val date = LocalDate.now()
        val event1 = createEvent(1, "Event 1", date.plusDays(1))
        val event2 = createEvent(2, "Event 2", date.plusDays(2))
        val event3 = createEvent(3, "Event 3", date.plusDays(3))
        eventDao.insertEvent(event1)
        eventDao.insertEvent(event2)
        eventDao.insertEvent(event3)

        val events = eventDao.getEventsByIds(listOf(1, 3))
        assertEquals("Expected list of size 2", 2, events.size)
        // Order is not guaranteed by IN, so check for presence
        assertTrue("Expected event1 to be present", events.contains(event1))
        assertTrue("Expected event3 to be present", events.contains(event3))
    }

    @Test
    fun getEventsByIds_nonExistentIds_returnsEmptyOrPartial() = runTest {
        val event1 = createEvent(1, "Event 1", LocalDate.now().plusDays(5))
        eventDao.insertEvent(event1)

        // Test with only non-existent IDs
        var events = eventDao.getEventsByIds(listOf(99, 100))
        assertTrue("Expected empty list for non-existent IDs", events.isEmpty())

        // Test with mix of existing and non-existent IDs
        events = eventDao.getEventsByIds(listOf(1, 99))
        assertEquals("Expected list of size 1 for mixed IDs", 1, events.size)
        assertEquals("Expected event1 for mixed IDs", event1, events[0])
    }

    @Test
    fun getEventsByIds_duplicateIds_returnsUniqueEvents() = runTest {
        val event1 = createEvent(1, "Event 1", LocalDate.now().plusDays(7))
        val event2 = createEvent(2, "Event 2", LocalDate.now().plusDays(8))
        eventDao.insertEvent(event1)
        eventDao.insertEvent(event2)

        val events = eventDao.getEventsByIds(listOf(1, 2, 1, 2, 1))
        assertEquals("Expected list of size 2 even with duplicate IDs", 2, events.size)
        assertTrue("Expected event1 to be present (duplicate IDs)", events.contains(event1))
        assertTrue("Expected event2 to be present (duplicate IDs)", events.contains(event2))
    }

    @Test
    fun getEventsByIds_orderCheckWithOrderByClause() = runTest {
        // This test assumes your actual DAO query has an ORDER BY clause if order is important.
        // The current EventDao.getEventsByIds doesn't have ORDER BY, so this tests default behavior (likely by ID).
        val date = LocalDate.now()
        val event1 = createEvent(1, "Event 1", date.plusDays(1))
        val event3 = createEvent(3, "Event 3", date.plusDays(3))
        val event2 = createEvent(2, "Event 2", date.plusDays(2))
        eventDao.insertEvent(event1)
        eventDao.insertEvent(event3) // Insert out of order
        eventDao.insertEvent(event2)

        val events = eventDao.getEventsByIds(listOf(1, 2, 3))
        // Without ORDER BY in the query, the order is typically by primary key or insertion order,
        // but not guaranteed. If you need a specific order, add it to the @Query.
        // For this test, we'll just check presence.
        assertEquals(3, events.size)
        assertTrue(events.contains(event1))
        assertTrue(events.contains(event2))
        assertTrue(events.contains(event3))

        // If you added "ORDER BY id ASC" to your @Query, then you could assert order:
        // assertEquals(event1, events[0])
        // assertEquals(event2, events[1])
        // assertEquals(event3, events[2])
    }
}
