package com.ossalali.daysremaining.presentation.mainscreen

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.presentation.topbar.options.AppDrawerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val eventRepo: EventRepo,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    // Current screen
    private val _currentScreen = MutableStateFlow(AppDrawerOptions.Home.name)
    val currentScreen: StateFlow<String> = _currentScreen

    // Search functionality
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _filteredEventsList = MutableStateFlow<List<EventItem>>(emptyList())
    val filteredEventsList: StateFlow<List<EventItem>> = _filteredEventsList

    // Event selection
    private val _selectedEventIds = mutableStateListOf<Int>()
    val selectedEventIds: List<Int>
        get() = _selectedEventIds

    // Current events
    private val _currentEventItems = MutableStateFlow<List<EventItem>>(emptyList())
    val currentEventItems: StateFlow<List<EventItem>> = _currentEventItems

    // Dialog states
    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog

    private val _showArchiveDialog = MutableStateFlow(false)
    val showArchiveDialog: StateFlow<Boolean> = _showArchiveDialog

    // Methods for handling screen navigation
    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    // Methods for handling search
    fun onSearchTextChange(text: String) {
        _searchText.value = text
        filterEvents()
    }

    fun toggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            _searchText.value = ""
            filterEvents()
        }
    }

    private fun filterEvents() {
        if (_searchText.value.isEmpty()) {
            _filteredEventsList.value = _currentEventItems.value
        } else {
            _filteredEventsList.value = _currentEventItems.value.filter {
                it.title.contains(_searchText.value, ignoreCase = true)
            }
        }
    }

    // Methods for handling selected events
    fun toggleSelection(eventId: Int) {
        if (_selectedEventIds.contains(eventId)) {
            _selectedEventIds.remove(eventId)
        } else {
            _selectedEventIds.add(eventId)
        }
    }

    fun clearSelections() {
        _selectedEventIds.clear()
    }

    // Methods for handling dialogs
    fun showDeleteDialog() {
        _showDeleteDialog.value = true
    }

    fun dismissDeleteDialog() {
        _showDeleteDialog.value = false
    }

    fun showArchiveDialog() {
        _showArchiveDialog.value = true
    }

    fun dismissArchiveDialog() {
        _showArchiveDialog.value = false
    }

    // Methods for handling events
    fun loadEvents() {

    }

    fun loadArchivedEvents() {

    }

    fun deleteSelectedEvents() {

    }

    fun archiveSelectedEvents() {

    }
} 