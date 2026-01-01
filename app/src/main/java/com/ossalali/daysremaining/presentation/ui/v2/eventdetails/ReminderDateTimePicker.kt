package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.window.DialogProperties
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun ReminderDateTimePicker(onSave: (LocalDateTime) -> Unit = {}, onDismiss: () -> Unit = {}) {
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var datePickerText by remember { mutableStateOf("Select Date") }
    var timePickerText by remember { mutableStateOf("Select Time") }

    // TODO: disable add button when date and time are not selected

    AlertDialog(
        modifier = Modifier.padding(horizontal = PaddingSize.half),
        title = { Text(text = "Reminders") },
        confirmButton = {
            TextButton(
                onClick = {
                    val localDate = LocalDate.parse(datePickerText)
                    val localTime = LocalTime.parse(timePickerText)
                    onSave(LocalDateTime.of(localDate, localTime))
                }
            ) {
                Text(text = "Add")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = "Cancel") } },
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "When would you like to be reminded?")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    DatePickerChip(
                        chipText = datePickerText,
                        onDateChanged = { datePickerText = it.toString() },
                    )
                    Spacer(modifier = Modifier.width(PaddingSize.default))
                    TimePickerChip(
                        chipText = timePickerText,
                        onTimeChanged = { timePickerText = it.toString() },
                    )
                }

                if (showDatePickerDialog) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(showDatePickerDialog = { showDatePickerDialog = it })
                }
            }
        },
    )
}

@Composable
@PreviewLightDark
fun ReminderDateTimePickerPreview() {
    MyAppTheme { ReminderDateTimePicker() }
}
