package com.ossalali.daysremaining.presentation.mainscreen

import android.provider.CalendarContract.Colors
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithSearch(
    isSearching: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onStartSearch: () -> Unit,
    onCloseSearch: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    if (isSearching) {
        Surface(
            shadowElevation = 4.dp,
            tonalElevation = 4.dp,
            color = Color.White
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 8.dp, end = 8.dp),
                placeholder = { Text("Search events...") },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = onCloseSearch) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Search"
                        )
                    }
                }
            )
        }
    } else {
        CenterAlignedTopAppBar(
            title = { Text("Events") },
            actions = {
                IconButton(onClick = onStartSearch) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Start Search"
                    )
                }
            }
        )
    }
}