package com.ossalali.daysremaining.presentation.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ossalali.daysremaining.model.Event
import com.ossalali.daysremaining.presentation.event.EventViewModel
import com.ossalali.daysremaining.presentation.event.EventsScreen
import com.ossalali.daysremaining.presentation.eventcreation.EventCreationScreen
import com.ossalali.daysremaining.presentation.eventcreation.EventCreationViewModel

@Composable
fun HomeContent(
    eventsList: List<Event>,
    paddingValues: PaddingValues,
    eventCreationViewModel: EventCreationViewModel,
    eventViewModel: EventViewModel
) {
    val showCreateEventScreen by eventCreationViewModel.showCreateEventScreen

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
        EventsScreen(
            viewModel = eventViewModel,
            inputEvents = eventsList
        )
    }
}