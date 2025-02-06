package com.ossalali.numbered.presentation

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    // State to control if the event creation dialog is open.
    var showEventCreationDialog by remember { mutableStateOf(false) }

    // Example event details; these could be updated based on your application's logic.
    val eventName by remember { mutableStateOf("My Event") }
    val eventDate by remember { mutableStateOf(LocalDate.now().plusDays(10)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Countdown") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showEventCreationDialog = true }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Event")
            }
        }
    ) { paddingValues ->
        // Content of your app (e.g., countdown display)
        CountdownContent(eventName, eventDate, Modifier.padding(paddingValues))
    }

    // Show the EventCreationScreen inside a Dialog if showEventCreationDialog is true.
    if (showEventCreationDialog) {
        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardColors(
                contentColor = Color.Black,
                containerColor = Color.LightGray,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.LightGray
            ),
            modifier = Modifier
                .padding(
                    top = 80.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            EventCreationScreen(
                onEventCreated = { event ->
                    // Handle the created event (e.g., store it, update state, etc.)

                    // Dismiss the dialog after the event is created.
                    showEventCreationDialog = false
                },
                onClose = { showEventCreationDialog = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}