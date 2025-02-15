package com.ossalali.daysremaining.presentation.event

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.ossalali.daysremaining.model.Event

@Composable
fun EventsScreen(
    viewModel: EventViewModel,
    inputEvents: List<Event>
) {
    val deleteDialog by viewModel.confirmDeleteDialog
    val archiveDialog by viewModel.confirmArchiveDialog

    EventListScreen(viewModel, inputEvents)
    if (deleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            title = { Text(text = "Delete Event?") },
            text = { Text(text = "Are you sure you want to delete this event?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteEvents() }) {
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
    if (archiveDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissArchiveDialog() },
            title = { Text(text = "Archive Event?") },
            text = { Text(text = "Are you sure you want to archive this event?") },
            confirmButton = {
                TextButton(onClick = { viewModel.archiveEvents() }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissArchiveDialog() }) {
                    Text("No")
                }
            }
        )
    }
}