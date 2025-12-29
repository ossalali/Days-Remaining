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
fun ImageDeletionDialog(
    showConfirmImageDeleteDialog: (Boolean) -> Unit = {},
    removeImage: () -> Unit = {},
) {
    AlertDialog(
        title = { Text(text = "Delete image?", style = MaterialTheme.typography.titleLarge) },
        text = { Text("Do you want to delete the image?") },
        confirmButton = {
            TextButton(
                onClick = {
                    removeImage()
                    showConfirmImageDeleteDialog(false)
                }
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(
                        painter = painterResource(R.drawable.delete_24px),
                        contentDescription = "Confirm Delete Image",
                    )
                    Text("OK")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { showConfirmImageDeleteDialog(false) }) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(
                        painter = painterResource(R.drawable.close_24px),
                        contentDescription = "Cancel Delete Image",
                    )
                    Text("Cancel")
                }
            }
        },
        onDismissRequest = { showConfirmImageDeleteDialog(false) },
    )
}
