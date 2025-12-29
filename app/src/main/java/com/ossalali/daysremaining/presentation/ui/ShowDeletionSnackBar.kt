package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.presentation.ui.previews.DefaultPreviewsNoSystemUI
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize

@Composable
fun ShowDeletionSnackBar(
    onUndoDelete: () -> Unit,
    onDismissSnackBar: () -> Unit,
    snackBarMessage: String,
) {
    Snackbar(
        modifier = Modifier.padding(all = PaddingSize.default),
        action = {
            TextButton(onClick = onUndoDelete) {
                Text("Undo", color = Color.hsl(267.44f, 0.9707f, 0.598f))
            }
        },
        dismissAction = {
            IconButton(onClick = onDismissSnackBar) {
                Icon(
                    painter = painterResource(R.drawable.close_24px),
                    contentDescription = "Close snackbar"
                )
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
