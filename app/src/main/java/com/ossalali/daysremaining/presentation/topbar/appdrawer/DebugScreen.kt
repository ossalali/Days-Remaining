package com.ossalali.daysremaining.presentation.topbar.appdrawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.model.Event
import kotlinx.coroutines.launch
import java.time.LocalDate

suspend fun addEvents(
    debugScreenViewModel: DebugScreenViewModel,
) {
    val allEvents = debugScreenViewModel.getNumberOfEvents()
    val eventList = mutableListOf<Event>()
    for (i in allEvents + 1..allEvents + 5) {
        eventList.add(
            Event(
                id = 0,
                title = "Event $i",
                description = "Event $i Description",
                date = LocalDate.now()
            )
        )
    }
    debugScreenViewModel.insertEvents(eventList)
}

@Composable
fun DebugScreen(
    debugScreenViewModel: DebugScreenViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    var showSnackbar by remember { mutableStateOf(false) }
    Column {
        Row {
            Text("Add Events")
            IconButton(
                onClick = {
                    scope.launch {
                        addEvents(debugScreenViewModel)
                        showSnackbar = true
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Events"
                )
            }
        }
    }
    if (showSnackbar) {
        Snackbar() {
            Text("Events added")
        }
    }
}