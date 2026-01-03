package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.model.Reminder
import com.ossalali.daysremaining.presentation.ui.theme.IconSize
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ReminderList(
    readOnly: Boolean = false,
    reminders: ImmutableList<Reminder> = persistentListOf(),
    deleteReminder: (Int) -> Unit = {},
) {
    var showDeleteReminderDialog by remember { mutableStateOf(false) }
    reminders.forEach { stableLocalDateTime ->
        Card(
            modifier = Modifier.padding(vertical = PaddingSize.half),
            shape = RoundedCornerShape(PaddingSize.default),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PaddingSize.default, vertical = PaddingSize.quarter),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.notifications_active_24px),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(PaddingSize.half))
                Text(
                    text =
                        stableLocalDateTime.dateTime.format(
                            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        )
                )
                Spacer(modifier = Modifier.weight(1f))
                if (readOnly) {
                    Spacer(modifier = Modifier.height(IconSize.double))
                } else {
                    IconButton(onClick = { showDeleteReminderDialog = true }) {
                        Icon(
                            modifier = Modifier.size(IconSize.default),
                            painter = painterResource(R.drawable.delete_24px),
                            contentDescription = null,
                        )
                    }
                }
                if (showDeleteReminderDialog) {
                    AlertDialog(
                        title = { Text(text = "Delete reminder?") },
                        onDismissRequest = { showDeleteReminderDialog = false },
                        confirmButton = {
                            TextButton(onClick = { deleteReminder(stableLocalDateTime.id) }) {
                                Text(text = "Delete")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteReminderDialog = false }) {
                                Text(text = "Cancel")
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
fun ReminderListPreview() {
    MyAppTheme {
        ReminderList(
            reminders =
                persistentListOf(
                    Reminder(1, 1, LocalDateTime.now()),
                    Reminder(1, 2, LocalDateTime.now().plusDays(1)),
                )
        )
    }
}

@Composable
@PreviewLightDark
fun ReminderListReadOnlyPreview() {
    MyAppTheme {
        ReminderList(
            readOnly = true,
            reminders =
                persistentListOf(
                    Reminder(1, 1, LocalDateTime.now()),
                    Reminder(1, 2, LocalDateTime.now().plusDays(1)),
                ),
        )
    }
}
