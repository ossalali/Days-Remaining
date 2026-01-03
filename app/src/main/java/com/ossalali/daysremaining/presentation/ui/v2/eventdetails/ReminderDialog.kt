package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.model.Reminder
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDateTime

@Composable
fun ReminderDialog(
    readOnly: Boolean = false,
    onSave: (ImmutableList<Reminder>) -> Unit = {},
    showReminderDialog: (Boolean) -> Unit = {},
    reminders: ImmutableList<Reminder> = persistentListOf(),
    currentEventItemId: Int = 0,
) {
    var showNewReminderDialog by remember { mutableStateOf(false) }
    val currentReminders by remember { mutableStateOf(reminders.toMutableStateList()) }
    AlertDialog(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Reminders")
                if (!readOnly) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { showNewReminderDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.add_24px),
                            contentDescription = null,
                        )
                    }
                }
            }
        },
        onDismissRequest = { showReminderDialog(false) },
        confirmButton = {
            TextButton(onClick = { onSave(currentReminders.toImmutableList()) }) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { showReminderDialog(false) }) { Text(text = "Cancel") }
        },
        text = {
            if (currentReminders.isEmpty()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    TextButton(onClick = { showNewReminderDialog = true }) {
                        Icon(
                            modifier = Modifier.padding(end = PaddingSize.quarter),
                            painter = painterResource(R.drawable.add_24px),
                            contentDescription = null,
                        )
                        Text(text = "Add reminder")
                    }
                }
            } else {

                ReminderList(
                    readOnly = readOnly,
                    reminders = currentReminders.toImmutableList(),
                    deleteReminder = { idToBeDeleted ->
                        currentReminders.removeIf { reminder -> reminder.id == idToBeDeleted }
                    },
                )
            }
        },
    )

    if (showNewReminderDialog) {
        ReminderDateTimePicker(
            onSave = { dateTime ->
                currentReminders.add(
                    Reminder(eventItemId = currentEventItemId, dateTime = dateTime)
                )
                showNewReminderDialog = false
            },
            onDismiss = { showNewReminderDialog = false },
        )
    }
}

@Composable
@PreviewLightDark
fun ReminderDialogPreview() {
    MyAppTheme {
        ReminderDialog(
            reminders =
                persistentListOf(Reminder(id = 1, eventItemId = 1, dateTime = LocalDateTime.now()))
        )
    }
}

@Composable
@PreviewLightDark
fun ReminderDialogReadOnlyPreview() {
    MyAppTheme {
        ReminderDialog(
            readOnly = true,
            reminders =
                persistentListOf(Reminder(id = 1, eventItemId = 1, dateTime = LocalDateTime.now())),
        )
    }
}

@Composable
@PreviewLightDark
fun ReminderDialogEmptyPreview() {
    MyAppTheme { ReminderDialog() }
}
