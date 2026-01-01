package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.presentation.ui.theme.IconSize
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerChip(
    modifier: Modifier = Modifier,
    clearFocus: () -> Unit = {},
    chipText: String = "",
    onTimeChanged: (LocalTime) -> Unit = {},
) {
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val timePickerState by remember {
        mutableStateOf(TimePickerState(initialHour = 12, initialMinute = 0, is24Hour = true))
    }
    AssistChip(
        leadingIcon = {
            Icon(
                modifier = Modifier.size(IconSize.inline),
                painter = painterResource(R.drawable.alarm_24px),
                contentDescription = null,
            )
        },
        modifier = modifier,
        onClick = {
            clearFocus()
            showTimePickerDialog = true
        },
        label = { Text(text = chipText) },
    )

    if (showTimePickerDialog) {
        TimePickerDialog(
            title = { Text(text = "Select Time") },
            onDismissRequest = {},
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeChanged(LocalTime.of(timePickerState.hour, timePickerState.minute))
                        showTimePickerDialog = false
                    }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) { Text(text = "Cancel") }
            },
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
@PreviewLightDark
fun TimePickerChipPreview() {
    MyAppTheme { TimePickerChip(chipText = "Select Time") }
}
