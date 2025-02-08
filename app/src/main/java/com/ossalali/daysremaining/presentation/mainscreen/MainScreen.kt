package com.ossalali.daysremaining.presentation.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.presentation.event.EventScreen
import com.ossalali.daysremaining.presentation.event.EventViewModel
import com.ossalali.daysremaining.presentation.eventcreation.EventCreationScreen
import com.ossalali.daysremaining.presentation.eventcreation.EventCreationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    eventViewModel: EventViewModel = hiltViewModel(),
    eventCreationViewModel: EventCreationViewModel = hiltViewModel()
) {
    val showCreateEventScreen by eventViewModel.showCreateEventScreen

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Countdown") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    eventViewModel.toggleCreateEventScreen(true)
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
                            eventViewModel.toggleCreateEventScreen(false)
                        },
                        onClose = { eventViewModel.toggleCreateEventScreen(false) }
                    )
                }
            }
            EventScreen(viewModel = eventViewModel)
        }
    }
}