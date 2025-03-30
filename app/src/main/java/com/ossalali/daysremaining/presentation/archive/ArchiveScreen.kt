package com.ossalali.daysremaining.presentation.archive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.v2.presentation.ui.EventListGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    viewModel: ArchiveViewModel = hiltViewModel()
) {
    val archivedEvents = viewModel.archivedEvents.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Archived Events") }
            )
        }
    ) { paddingValues ->
        if (archivedEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No archived events",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            EventListGrid(
                onEventItemClick = { /* Handle click */ },
                onEventItemSelection = { /* Handle selection */ },
                events = archivedEvents,
                selectedEventIds = emptyList(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
} 