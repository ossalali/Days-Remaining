package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.ossalali.daysremaining.R

@Composable
fun EventDeletionDialog(onDeleteConfirm: () -> Unit, showEventDeletionDialog: (Boolean) -> Unit) {
    AlertDialog(
        title = { Text(text = "Delete event?", style = MaterialTheme.typography.titleLarge) },
        text = { Text("Do you want to delete the event?") },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(
                        painter = painterResource(R.drawable.delete_24px),
                        contentDescription = "Confirm Delete Event",
                    )
                    Text("OK")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { showEventDeletionDialog(false) }) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(
                        painter = painterResource(R.drawable.close_24px),
                        contentDescription = "Cancel Delete Event",
                    )
                    Text("Cancel")
                }
            }
        },
        onDismissRequest = { showEventDeletionDialog(false) },
    )
}
