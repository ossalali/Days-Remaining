package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.ossalali.daysremaining.R

@Composable
fun EventDeletionDialog(onDeleteConfirm: () -> Unit, showEventDeletionDialog: (Boolean) -> Unit) {
    AlertDialog(
        title = { Text(text = "Delete event?", style = MaterialTheme.typography.titleLarge) },
        text = { Text("Are you sure you want to delete this event?") },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete_24px),
                        contentDescription = "Confirm Delete Event",
                    )
                    Text("Yes")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { showEventDeletionDialog(false) }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close_24px),
                        contentDescription = "Cancel Delete Event",
                    )
                    Text("No")
                }
            }
        },
        onDismissRequest = { showEventDeletionDialog(false) },
    )
}
