package com.ossalali.daysremaining.presentation.eventcreation

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ossalali.daysremaining.di.IoDispatcher
import com.ossalali.daysremaining.infrastructure.EventRepo
import com.ossalali.daysremaining.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventCreationViewModel @Inject constructor(
    private val eventRepo: EventRepo,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    var title by mutableStateOf("")
    private var date by mutableStateOf("")
    var description by mutableStateOf("")
    private var isEventCreated by mutableStateOf(false)

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _allEvents: Flow<List<Event>> = eventRepo.allEvents

    val eventsList = searchText
        .combine(_allEvents) { text, events ->
            if (text.isBlank()) {
                @Suppress("UNUSED_EXPRESSION")
                events
            }
            events.filter { event ->
                event.title.contains(text.trim(), ignoreCase = true)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onToggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            onSearchTextChange("")
        }
    }

    private val _showCreateEventScreen = mutableStateOf(false)
    val showCreateEventScreen: State<Boolean> = _showCreateEventScreen

    fun toggleCreateEventScreen(show: Boolean) {
        _showCreateEventScreen.value = show
    }

    fun onTitleChange(newTitle: String) {
        title = newTitle
    }

    fun onDateChange(newDate: String) {
        date = newDate
    }

    fun onDescriptionChange(newDescription: String) {
        description = newDescription
    }

    fun createEvent() {
        if (title.isBlank() || date.isBlank()) {
            return
        }

        viewModelScope.launch(ioDispatcher) {
            val eventDate = LocalDate.parse(date)
            val event = Event(
                title = title,
                date = eventDate,
                description = description
            )
            eventRepo.insertEvent(event)

            isEventCreated = true
        }
    }

    fun resetEventCreatedState() {
        title = ""
        date = ""
        description = ""
    }
}
