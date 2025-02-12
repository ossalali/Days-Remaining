package com.ossalali.daysremaining.presentation.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ossalali.daysremaining.presentation.topbar.options.VertOptions

@Composable
fun MoreVertMenu(
    onOptionSelected: (VertOptions) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More options"
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    VertOptions.Archive.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onClick = {
                onOptionSelected(VertOptions.Archive)
                expanded = false
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    VertOptions.Delete.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onClick = {
                onOptionSelected(VertOptions.Delete)
                expanded = false
            }
        )
    }
}