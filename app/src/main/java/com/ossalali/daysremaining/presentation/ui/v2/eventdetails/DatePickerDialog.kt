package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun DatePickerDialog(
    showDatePickerDialog: (Boolean) -> Unit = {},
    onDateChanged: (LocalDate) -> Unit = {},
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = { showDatePickerDialog(false) },
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    val selectedDate =
                        if (selectedDateMillis != null) {
                            Instant.ofEpochMilli(selectedDateMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        } else {
                            LocalDate.now()
                        }
                    showDatePickerDialog(false)
                    onDateChanged(selectedDate)
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = { TextButton(onClick = { showDatePickerDialog(false) }) { Text("Cancel") } },
    ) {
        DatePicker(state = datePickerState, modifier = Modifier.sizeIn(maxWidth = 350.dp))
    }
}

@Composable
@PreviewLightDark
fun DatePickerDialogPreview() {
    MyAppTheme { DatePickerDialog() }
}
