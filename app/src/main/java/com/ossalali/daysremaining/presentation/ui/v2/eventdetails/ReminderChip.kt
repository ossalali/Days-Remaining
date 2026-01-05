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
import com.ossalali.daysremaining.model.Reminder
import com.ossalali.daysremaining.presentation.ui.theme.IconSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDateTime

@Composable
fun ReminderChip(
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    onSave: (ImmutableList<Reminder>) -> Unit = {},
    clearFocus: () -> Unit = {},
    reminders: ImmutableList<Reminder> = persistentListOf(),
    currentEventItemId: Int = 0,
    onReminderChipClick: (Int) -> Unit = {},
) {
    var showReminderDialog by remember { mutableStateOf(false) }
    val iconDrawable =
        if (reminders.isEmpty()) {
            R.drawable.notification_add_24px
        } else {
            R.drawable.notifications_active_24px
        }
    val chipText =
        when (reminders.size) {
            0 -> {
                "Add reminder"
            }

            1 -> {
                "${reminders.size} reminder"
            }

            else -> {
                "${reminders.size} reminders"
            }
        }

    AssistChip(
        leadingIcon = {
            Icon(
                modifier = Modifier.size(IconSize.inline),
                painter = painterResource(iconDrawable),
                contentDescription = null,
            )
        },
        modifier = modifier,
        onClick = {
            onReminderChipClick(currentEventItemId)
            //showReminderDialog = true
        },
        label = { Text(text = chipText) },
    )

    if (showReminderDialog) {
        ReminderDialog(
            readOnly = readOnly,
            onSave = {
                onSave(it)
                showReminderDialog = false
            },
            showReminderDialog = { showReminderDialog = it },
            reminders = reminders,
            currentEventItemId = currentEventItemId,
        )
    }
}

@Composable
@PreviewLightDark
fun ReminderChipPreview() {
    MyAppTheme { ReminderChip() }
}

@Composable
@PreviewLightDark
fun ReminderChipOnePreview() {
    MyAppTheme {
        ReminderChip(
            reminders =
                persistentListOf(Reminder(id = 1, eventItemId = 1, dateTime = LocalDateTime.now()))
        )
    }
}

@Composable
@PreviewLightDark
fun ReminderChipMultiplePreview() {
    MyAppTheme {
        ReminderChip(
            reminders =
                persistentListOf(
                    Reminder(id = 1, eventItemId = 1, dateTime = LocalDateTime.now()),
                    Reminder(id = 1, eventItemId = 1, dateTime = LocalDateTime.now()),
                )
        )
    }
}
