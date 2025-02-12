package com.ossalali.daysremaining.presentation.event

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.model.Event

@Composable
fun EventsScreen(
    viewModel: EventViewModel = hiltViewModel<EventViewModel>(),
    inputEvents: List<Event>
) {
    val confirmDeleteDialog by viewModel.confirmDeleteDialog

    EventListScreen(viewModel, inputEvents)
    if (confirmDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            title = { Text(text = "Delete Event?") },
            text = { Text(text = "Are you sure you want to delete this event?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteEvent() }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteDialog() }) {
                    Text("No")
                }
            }
        )
    }
}