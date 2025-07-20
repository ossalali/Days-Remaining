package com.ossalali.daysremaining.presentation.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteAlertDialog(
  numberOfEventsToBeDeleted: Int = 1,
  eventTitle: String = "",
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
    AlertDialog(
      onDismissRequest = onDismiss,
      title = { Text("Confirm Deletion") },
      text = {
          if (numberOfEventsToBeDeleted == 1) {
              Text("Are you sure you want to delete '$eventTitle'?")
          } else {
              Text("Are you sure you want to delete $numberOfEventsToBeDeleted events?")
          }
      },
      confirmButton = {
          TextButton(
            onClick = {
                onConfirm()
                onDismiss()
            }
          ) {
              Text("Delete")
          }
      },
      dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
