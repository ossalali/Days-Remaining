package com.ossalali.daysremaining.presentation.topbar.appdrawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ossalali.daysremaining.businesslogic.debug.AddDebugEventsUseCase
import com.ossalali.daysremaining.businesslogic.debug.DebugAddEvents
import kotlinx.coroutines.launch

@Composable
fun DebugScreen(
    debugScreenViewModel: DebugScreenViewModel = hiltViewModel(),
    addEvents: DebugAddEvents = DebugAddEvents()
) {
    val scope = rememberCoroutineScope()

    var showSnackbar by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Add Events")
        Button(
            modifier = Modifier.padding(8.dp),
            onClick = {
                scope.launch {
                    addEvents(debugScreenViewModel, AddDebugEventsUseCase())
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
    if (showSnackbar) {
        Snackbar {
            Text("Events added")
        }
    }
}