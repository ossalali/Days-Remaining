package com.ossalali.daysremaining.widget

import android.appwidget.AppWidgetManager
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem // Assuming EventItem is needed for EventRepo mock
import com.ossalali.daysremaining.widget.datastore.WidgetDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow // For mocking getEvents()
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when` // Direct import for when
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class WidgetPreferenceScreenViewModelTest {

    @Mock
    private lateinit var mockEventRepo: EventRepo

    @Mock
    private lateinit var mockWidgetDataStore: WidgetDataStore

    @Mock
    private lateinit var mockAppWidgetOptions: Bundle

    private val testDispatcher = StandardTestDispatcher()

    private val testAppWidgetId = 1

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Mock getEvents behavior for EventRepo, as it's called during ViewModel init
        `when`(mockEventRepo.allActiveEventsAsFlow).thenReturn(emptyFlow<List<EventItem>>())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(appWidgetId: Int = testAppWidgetId, options: Bundle? = mockAppWidgetOptions): WidgetPreferenceScreenViewModel {
        return WidgetPreferenceScreenViewModel(mockEventRepo, mockWidgetDataStore, appWidgetId, options)
    }

    // --- Tests for getMaxEvents behavior (via internal maxEventsAllowed) ---

    @Test
    fun `maxEventsAllowed is 8 when appWidgetOptions is null`() = runTest {
        val viewModel = createViewModel(options = null)
        assertEquals("Expected maxEventsAllowed to be 8 when options are null", 8, viewModel.maxEventsAllowed)
    }

    @Test
    fun `maxEventsAllowed is 2 for API S+ small widget (OPTION_APPWIDGET_SIZES)`() = runTest {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val sizes = arrayListOf(SizeF(100f, 50f)) // Small height
            `when`(mockAppWidgetOptions.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES, SizeF::class.java)).thenReturn(sizes)
            val viewModel = createViewModel()
            assertEquals("Expected maxEventsAllowed to be 2 for API S+ small widget", 2, viewModel.maxEventsAllowed)
        }
    }

    @Test
    fun `maxEventsAllowed is 8 for API S+ large widget (OPTION_APPWIDGET_SIZES)`() = runTest {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val sizes = arrayListOf(SizeF(100f, 150f)) // Large height
            `when`(mockAppWidgetOptions.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES, SizeF::class.java)).thenReturn(sizes)
            val viewModel = createViewModel()
            assertEquals("Expected maxEventsAllowed to be 8 for API S+ large widget", 8, viewModel.maxEventsAllowed)
        }
    }
    
    @Test
    fun `maxEventsAllowed uses MIN_HEIGHT when OPTION_APPWIDGET_SIZES is empty on S+`() = runTest {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            `when`(mockAppWidgetOptions.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES, SizeF::class.java)).thenReturn(arrayListOf())
            `when`(mockAppWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)).thenReturn(50) // Small min height
            val viewModel = createViewModel()
            assertEquals("Expected maxEventsAllowed to be 2 using MIN_HEIGHT fallback", 2, viewModel.maxEventsAllowed)
        }
    }


    @Test
    fun `maxEventsAllowed is 2 for API pre-S small widget (OPTION_APPWIDGET_MIN_HEIGHT)`() = runTest {
        // Simulate pre-S behavior by not mocking getParcelableArrayList or returning null
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) { // Test only on older versions OR ensure getParcelableArrayList returns null
             `when`(mockAppWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)).thenReturn(50) // Small height
             val viewModel = createViewModel()
             assertEquals("Expected maxEventsAllowed to be 2 for pre-S small widget", 2, viewModel.maxEventsAllowed)
        } else { // For S+ ensure OPTION_APPWIDGET_SIZES is null/empty for this fallback
            `when`(mockAppWidgetOptions.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES, SizeF::class.java)).thenReturn(null)
            `when`(mockAppWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)).thenReturn(50)
            val viewModel = createViewModel()
            assertEquals("Expected maxEventsAllowed to be 2 for S+ fallback to small MIN_HEIGHT", 2, viewModel.maxEventsAllowed)
        }
    }

    @Test
    fun `maxEventsAllowed is 8 for API pre-S large widget (OPTION_APPWIDGET_MIN_HEIGHT)`() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            `when`(mockAppWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)).thenReturn(150) // Large height
            val viewModel = createViewModel()
            assertEquals("Expected maxEventsAllowed to be 8 for pre-S large widget", 8, viewModel.maxEventsAllowed)
        } else {
            `when`(mockAppWidgetOptions.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES, SizeF::class.java)).thenReturn(null)
            `when`(mockAppWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)).thenReturn(150)
            val viewModel = createViewModel()
            assertEquals("Expected maxEventsAllowed to be 8 for S+ fallback to large MIN_HEIGHT", 8, viewModel.maxEventsAllowed)
        }
    }

    // --- Tests for toggleSelection behavior ---

    @Test
    fun `toggleSelection respects maxEventsAllowed = 2`() = runTest {
        // Configure options for maxEventsAllowed = 2
        // This setup depends on which API level path is taken.
        // Assuming S+ for simplicity here, or ensure consistent fallback for pre-S.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
             val sizes = arrayListOf(SizeF(100f, 50f))
            `when`(mockAppWidgetOptions.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES, SizeF::class.java)).thenReturn(sizes)
        } else {
            `when`(mockAppWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)).thenReturn(50)
        }
       
        val viewModel = createViewModel()
        assertEquals(2, viewModel.maxEventsAllowed) // Verify precondition

        viewModel.toggleSelection(1)
        assertTrue(viewModel.selectedEventIds.contains(1))
        assertEquals(1, viewModel.selectedEventIds.size)

        viewModel.toggleSelection(2)
        assertTrue(viewModel.selectedEventIds.contains(2))
        assertEquals(2, viewModel.selectedEventIds.size)

        viewModel.toggleSelection(3) // Try to add 3rd event
        assertFalse(viewModel.selectedEventIds.contains(3)) // Should not be added
        assertEquals(2, viewModel.selectedEventIds.size)    // Size should remain 2

        viewModel.toggleSelection(1) // Deselect event 1
        assertFalse(viewModel.selectedEventIds.contains(1))
        assertEquals(1, viewModel.selectedEventIds.size)

        viewModel.toggleSelection(3) // Now add event 3
        assertTrue(viewModel.selectedEventIds.contains(3))
        assertEquals(2, viewModel.selectedEventIds.size)
    }

    @Test
    fun `toggleSelection respects maxEventsAllowed = 8`() = runTest {
        // Configure options for maxEventsAllowed = 8
        `when`(mockAppWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)).thenReturn(150)
        // For S+, ensure SIZES is null or returns large sizes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            `when`(mockAppWidgetOptions.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES, SizeF::class.java)).thenReturn(null)
        }
        val viewModel = createViewModel()
        assertEquals(8, viewModel.maxEventsAllowed) // Verify precondition

        for (i in 1..8) {
            viewModel.toggleSelection(i)
            assertTrue(viewModel.selectedEventIds.contains(i))
        }
        assertEquals(8, viewModel.selectedEventIds.size)

        viewModel.toggleSelection(9) // Try to add 9th event
        assertFalse(viewModel.selectedEventIds.contains(9))
        assertEquals(8, viewModel.selectedEventIds.size)
    }

    // --- Tests for saveSelectedEvents behavior ---

    @Test
    fun `saveSelectedEvents calls widgetDataStore with correct parameters`() = runTest {
        val viewModel = createViewModel() // Default appWidgetId = 1
        viewModel.toggleSelection(10)
        viewModel.toggleSelection(20)
        val expectedIds = listOf(10, 20)

        viewModel.saveSelectedEvents()
        testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutine launched by saveSelectedEvents completes

        verify(mockWidgetDataStore).saveSelectedEventIds(testAppWidgetId, expectedIds)
    }

    @Test
    fun `saveSelectedEvents does not call widgetDataStore if appWidgetId is invalid`() = runTest {
        val viewModel = createViewModel(appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID)
        viewModel.toggleSelection(10)

        viewModel.saveSelectedEvents()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(mockWidgetDataStore, never()).saveSelectedEventIds(any(), any())
    }
}
