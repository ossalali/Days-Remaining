package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ImagePickerDialog(
    showImagePickerDialog: (Boolean) -> Unit = {},
    imageChosen: () -> Unit = {},
    openCamera: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = { showImagePickerDialog(false) },
        confirmButton = {
            TextButton(
                onClick = {
                    showImagePickerDialog(false)
                    imageChosen()
                }
            ) {
                Text("Choose photo")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showImagePickerDialog(false)
                    openCamera()
                }
            ) {
                Text("Take photo")
            }
        },
        title = { Text("Add image") },
        text = { Text("Take or Choose an photo") },
    )
}
