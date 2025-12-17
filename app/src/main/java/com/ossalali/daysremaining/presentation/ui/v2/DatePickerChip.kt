package com.ossalali.daysremaining.presentation.ui.v2

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun DatePickerChip(
    modifier: Modifier = Modifier,
    clearFocus: () -> Unit = {},
    chipText: String = "",
    onDateChanged: (LocalDate) -> Unit = {},
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }
    AssistChip(
        modifier = modifier,
        onClick = {
            clearFocus()
            showDatePickerDialog = true
        },
        label = { Text(text = chipText) },
    )

    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
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
                        onDateChanged(selectedDate)
                        showDatePickerDialog = false
                    }
                ) {
                    Text(stringResource(R.string.ok), color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.primary)
                }
            },
        ) {
            DatePicker(state = datePickerState, modifier = Modifier.sizeIn(maxWidth = 350.dp))
        }
    }
}

@PreviewLightDark
@Composable
fun DatePickerChipPreview() {
    MyAppTheme { Surface { DatePickerChip(chipText = LocalDate.now().toString()) } }
}
