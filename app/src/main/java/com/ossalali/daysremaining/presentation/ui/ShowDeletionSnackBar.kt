package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.presentation.ui.theme.DefaultPreviewsNoSystemUI
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions

@Composable
fun ShowDeletionSnackBar(
  onUndoDelete: () -> Unit,
  onDismissSnackBar: () -> Unit,
  snackBarMessage: String,
) {
    Snackbar(
      modifier = Modifier.padding(all = Dimensions.default),
      action = {
          TextButton(onClick = onUndoDelete) {
              Text("Undo", color = Color.hsl(267.44f, 0.9707f, 0.598f))
          }
      },
      dismissAction = {
          IconButton(onClick = onDismissSnackBar) {
              Icon(imageVector = Icons.Filled.Close, contentDescription = "Close snackbar")
          }
      },
    ) {
        Text(text = snackBarMessage)
    }
}

@DefaultPreviewsNoSystemUI()
@Composable
fun SnackBarPreview() {
    MyAppTheme {
        ShowDeletionSnackBar(onUndoDelete = {}, onDismissSnackBar = {}, snackBarMessage = "Test")
    }
}
