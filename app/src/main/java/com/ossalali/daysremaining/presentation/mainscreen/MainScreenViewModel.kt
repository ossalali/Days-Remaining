package com.ossalali.daysremaining.presentation.mainscreen

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.EventItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val eventRepo: EventRepo,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    // Search functionality
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _filteredEventsList = MutableStateFlow<List<EventItem>>(emptyList())

    // Event selection
    private val _selectedEventIds = mutableStateListOf<Int>()
    val selectedEventIds: List<Int>
        get() = _selectedEventIds

    // Current events
    private val _currentEventItems = MutableStateFlow<List<EventItem>>(emptyList())

    // Initialize the view model by loading events
    init {
        loadEvents()
    }

    fun updateSearchText(text: String) {
        _searchText.value = text
        filterEvents()
    }

    fun toggleSearch(value: Boolean) {
        _isSearching.value = value
        if (!value) {
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

    // Methods for handling events
    fun loadEvents() {
        viewModelScope.launch(ioDispatcher) {
            val events = eventRepo.getAllActiveEvents()
            _currentEventItems.value = events
            filterEvents() // Update filtered list when raw data changes
        }
    }
}