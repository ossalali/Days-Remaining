package com.ossalali.daysremaining.infrastructure

import com.ossalali.daysremaining.model.EventItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class) // Use MockitoJUnitRunner for @Mock annotation
class EventRepoTest {

    @Mock // Annotation to create a mock EventDao
    private lateinit var mockEventDao: EventDao

    private lateinit var eventRepo: EventRepo

    @Before
    fun setUp() {
        // No need to initialize mockEventDao if using @Mock and MockitoJUnitRunner
        eventRepo = EventRepo(mockEventDao)
    }

    private fun createDummyEvent(id: Int, title: String, date: LocalDate): EventItem {
        return EventItem(id = id, title = title, date = date, isArchived = false)
    }

    @Test
    fun getEventsByIds_callsDaoWithCorrectParameters() = runTest {
        val date = LocalDate.now()
        val eventIds = listOf(1, 2, 3)
        val dummyEvents = listOf(
            createDummyEvent(1, "Event 1", date.plusDays(1)),
            createDummyEvent(2, "Event 2", date.plusDays(2)),
            createDummyEvent(3, "Event 3", date.plusDays(3))
        )

        // Stub the DAO method to return some dummy data when called with eventIds
        whenever(mockEventDao.getEventsByIds(eventIds)).thenReturn(dummyEvents)

        // Call the repository method
        val result = eventRepo.getEventsByIds(eventIds)

        // Verify that the DAO method was called with the correct parameters
        verify(mockEventDao).getEventsByIds(eventIds)

        // Optionally, assert that the result from the repo is what the DAO returned
        assertEquals(dummyEvents, result)
    }

    @Test
    fun getEventsByIds_emptyList_callsDaoAndReturnsEmpty() = runTest {
        val emptyEventIds = emptyList<Int>()
        val emptyDummyEvents = emptyList<EventItem>()

        // Stub the DAO method
        whenever(mockEventDao.getEventsByIds(emptyEventIds)).thenReturn(emptyDummyEvents)

        // Call the repository method
        val result = eventRepo.getEventsByIds(emptyEventIds)

        // Verify DAO call
        verify(mockEventDao).getEventsByIds(emptyEventIds)

        // Assert result
        assertTrue("Result should be empty for empty ID list", result.isEmpty())
    }
}

// Simple assertEquals for lists; for more complex scenarios, consider specific list matchers
private fun <T> assertEquals(expected: List<T>, actual: List<T>) {
    org.junit.Assert.assertEquals(expected.size, actual.size)
    for (i in expected.indices) {
        org.junit.Assert.assertEquals(expected[i], actual[i])
    }
}

// Simple assertTrue for boolean conditions
private fun assertTrue(message: String, condition: Boolean) {
    org.junit.Assert.assertTrue(message, condition)
}
