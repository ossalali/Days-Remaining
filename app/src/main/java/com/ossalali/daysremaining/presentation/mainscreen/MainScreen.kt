package com.ossalali.daysremaining.presentation.mainscreen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.presentation.event.EventScreen
import com.ossalali.daysremaining.presentation.event.EventViewModel
import com.ossalali.daysremaining.presentation.eventcreation.EventCreationScreen
import com.ossalali.daysremaining.presentation.eventcreation.EventCreationViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    eventCreationViewModel: EventCreationViewModel = hiltViewModel()
) {
    val showCreateEventScreen by eventCreationViewModel.showCreateEventScreen
    val searchText by eventCreationViewModel.searchText.collectAsState()
    val isSearching by eventCreationViewModel.isSearching.collectAsState()
    val eventsList by eventCreationViewModel.eventsList.collectAsState()

    Scaffold(
        topBar = {
            if (isSearching) {
                SearchAppBar(
                    searchText = searchText,
                    onSearchTextChange = eventCreationViewModel::onSearchTextChange,
                    onClose = { eventCreationViewModel.onToggleSearch() }
                )
            } else {
                TopAppBar(
                    title = { Text("Events") },
                    actions = {
                        IconButton(onClick = { eventCreationViewModel.onToggleSearch() }) {
                            Icon(Icons.Default.Search, contentDescription = "Search Events")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    eventCreationViewModel.toggleCreateEventScreen(true)
                    eventCreationViewModel.resetEventCreatedState()
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Event")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showCreateEventScreen) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    EventCreationScreen(
                        onEventCreated = {
                            eventCreationViewModel.toggleCreateEventScreen(false)
                        },
                        onClose = { eventCreationViewModel.toggleCreateEventScreen(false) }
                    )
                }
            }
            EventScreen(
                inputEvents = eventsList
            )
        }
    }
}