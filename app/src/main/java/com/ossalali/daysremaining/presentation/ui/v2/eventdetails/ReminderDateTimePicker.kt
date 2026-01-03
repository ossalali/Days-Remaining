package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import com.ossalali.daysremaining.presentation.ui.v2.eventdetails.Constants.SELECT_DATE
import com.ossalali.daysremaining.presentation.ui.v2.eventdetails.Constants.SELECT_TIME
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object Constants {
    const val SELECT_DATE = "Select Date"
    const val SELECT_TIME = "Select Time"
}

@Composable
fun ReminderDateTimePicker(onSave: (LocalDateTime) -> Unit = {}, onDismiss: () -> Unit = {}) {
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var datePickerText by remember { mutableStateOf(SELECT_DATE) }
    var timePickerText by remember { mutableStateOf(SELECT_TIME) }

    // TODO: disable add button when date and time are not selected

    AlertDialog(
        modifier = Modifier.padding(horizontal = PaddingSize.half),
        title = { Text(text = "Add Reminders") },
        confirmButton = {
            if (datePickerText == SELECT_DATE && timePickerText == SELECT_TIME) {
                Text(
                    modifier = Modifier.padding(start = PaddingSize.half, end = 14.dp, top = 14.dp),
                    text = "Add",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            } else {
                TextButton(
                    onClick = {
                        val dateText =
                            if (datePickerText == SELECT_DATE) {
                                LocalDate.now().toString()
                            } else {
                                datePickerText
                            }
                        val timeText =
                            if (timePickerText == SELECT_TIME) {
                                LocalTime.now().toString()
                            } else {
                                timePickerText
                            }
                        val localDate = LocalDate.parse(dateText)
                        val localTime = LocalTime.parse(timeText)
                        onSave(LocalDateTime.of(localDate, localTime))
                    }
                ) {
                    Text(text = "Add")
                }
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
