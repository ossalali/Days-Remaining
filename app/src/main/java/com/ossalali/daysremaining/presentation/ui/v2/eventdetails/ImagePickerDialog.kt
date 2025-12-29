package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.ossalali.daysremaining.R

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
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(
                        painter = painterResource(R.drawable.photo_library_24px),
                        contentDescription = "Choose photo",
                    )
                    Text("Choose photo")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showImagePickerDialog(false)
                    openCamera()

                }
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(
                        painter = painterResource(R.drawable.photo_camera_24px),
                        contentDescription = "Take photo",
                    )
                    Text("Take photo")
                }
            }
        },
        title = { Text("Add image") },
        text = { Text("Take or Choose an photo") },
    )
}
