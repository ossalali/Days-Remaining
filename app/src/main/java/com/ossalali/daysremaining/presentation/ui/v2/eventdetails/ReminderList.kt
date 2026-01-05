package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
    contentPadding: PaddingValues = PaddingValues(),
) {
    var reminderToDelete by remember { mutableStateOf(-1) }
    LazyColumn(contentPadding = contentPadding) {
        items(items = reminders) { item ->
            Card(
                modifier = Modifier.padding(vertical = PaddingSize.half),
                shape = RoundedCornerShape(PaddingSize.default),
                colors =
                    CardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = PaddingSize.default,
                                vertical = PaddingSize.quarter,
                            ),
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
                            item.dateTime.format(
                                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                            )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (readOnly) {
                        Spacer(modifier = Modifier.height(IconSize.double))
                    } else {
                        IconButton(onClick = { reminderToDelete = item.id }) {
                            Icon(
                                modifier = Modifier.size(IconSize.default),
                                painter = painterResource(R.drawable.delete_24px),
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
    }
    if (reminderToDelete != -1) {
        AlertDialog(
            title = { Text(text = "Delete reminder?") },
            text = { Text(text = "Are you sure you want to delete this reminder?") },
            onDismissRequest = { reminderToDelete = -1 },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteReminder(reminderToDelete)
                        reminderToDelete = -1
                    }
                ) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { reminderToDelete = -1 }) { Text(text = "No") }
            },
        )
    }
}

@Composable
@PreviewLightDark
fun ReminderListPreview() {
    MyAppTheme {
        ReminderList(
            reminders =
                persistentListOf(
                    Reminder(id = 1, eventItemId = 1, dateTime = LocalDateTime.now()),
                    Reminder(id = 2, eventItemId = 1, dateTime = LocalDateTime.now().plusDays(1)),
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
                    Reminder(id = 1, eventItemId = 1, dateTime = LocalDateTime.now()),
                    Reminder(id = 2, eventItemId = 1, dateTime = LocalDateTime.now().plusDays(1)),
                ),
        )
    }
}
