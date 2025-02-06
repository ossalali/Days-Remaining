package com.ossalali.daysremaining.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardWithDatePicker() {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // Initialize the date picker state with either the selected date or current time.
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.atStartOfDay(ZoneId.systemDefault())
            ?.toInstant()?.toEpochMilli() ?: System.currentTimeMillis()
    )


        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Event Date",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = selectedDate?.toString() ?: "Tap to select a date",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable { showDatePicker = true }
            )
        }

    // Display the DatePickerDialog when requested.
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Convert the selected millis to a LocalDate.
                        val instant = Instant.ofEpochMilli(millis)
                        selectedDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardWithDatePickerPreview() {
    CardWithDatePicker()
}
