package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import java.time.LocalDate

@Composable
fun DatePickerChip(
    modifier: Modifier = Modifier,
    clearFocus: () -> Unit = {},
    chipText: String = "",
    onDateChanged: (LocalDate) -> Unit = {},
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }
    AssistChip(
        leadingIcon = {
            Icon(
                modifier = Modifier.size(IconSize.inline),
                painter = painterResource(R.drawable.calendar_today_24px),
                contentDescription = null,
            )
        },
        modifier = modifier,
        onClick = {
            clearFocus()
            showDatePickerDialog = true
        },
        label = { Text(text = chipText) },
    )

    if (showDatePickerDialog) {
        DatePickerDialog(
            showDatePickerDialog = { showDatePickerDialog = it },
            onDateChanged = onDateChanged,
        )
    }
}

@PreviewLightDark
@Composable
fun DatePickerChipPreview() {
    MyAppTheme { DatePickerChip(chipText = LocalDate.now().toString()) }
}
