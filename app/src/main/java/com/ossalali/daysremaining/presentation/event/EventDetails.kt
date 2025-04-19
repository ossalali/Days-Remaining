package com.ossalali.daysremaining.presentation.event

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.v2.presentation.ui.theme.DefaultPreviews
import com.ossalali.daysremaining.v2.presentation.ui.theme.Dimensions
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetails(
    eventId: Int? = null,
    event: EventItem? = null,
    onBackClick: () -> Unit,
    viewModel: EventDetailsViewModel = hiltViewModel()
) {
    // If eventId is provided, load the event from the database
    if (eventId != null) {
        LaunchedEffect(eventId) {
            viewModel.loadEventById(eventId)
        }
    }

    // Use the provided event or the one from the ViewModel
    val eventState by viewModel.event.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val displayEvent = event ?: eventState
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = displayEvent?.title ?: "Event Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimensions.default),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (displayEvent != null) {
                EventContent(event = displayEvent)
            } else {
                Text(
                    text = "Event not found",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun EventContent(event: EventItem) {
    Column {
        val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        Text(
            text = "Date: ${event.date.format(dateFormatter)}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = Dimensions.half)
        )
        Text(
            text = "Description: ${event.description}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = Dimensions.half)
        )
        Text(
            text = "Days Remaining: ${event.getNumberOfDays()}",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@DefaultPreviews
@Composable
fun EventDetailsPreview() {
    EventDetails(
        event = EventItem(
            id = 1,
            title = "Sample Event Title",
            date = LocalDate.now().plusDays(10),
            description = "This is a sample event description."
        ),
        onBackClick = {}
    )
}