package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.model.Reminder
import com.ossalali.daysremaining.navigation.ReminderRoute
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import com.ossalali.daysremaining.presentation.ui.v2.viewmodel.ReminderViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDateTime

fun EntryProviderScope<NavKey>.reminderScreen(backStack: NavBackStack<NavKey>) {
    entry<ReminderRoute> { route ->
        val reminderViewModel = hiltViewModel<ReminderViewModel>()

        LaunchedEffect(route.eventId) { reminderViewModel.load(route.eventId) }
        val reminderState by reminderViewModel.state.collectAsStateWithLifecycle()
        ReminderScreen(
            reminderState.reminders,
            route.eventId,
            onSave = { reminders, eventId ->
                reminderViewModel.replaceReminders(reminders, eventId)
                backStack.removeLastOrNull()
            },
        )
    }
}

@Composable
fun ReminderScreen(
    reminders: ImmutableList<Reminder> = persistentListOf(),
    eventId: Int = 0,
    onSave: (ImmutableList<Reminder>, Int) -> Unit = { _, _ -> },
) {
    var showNewReminderDialog by remember { mutableStateOf(false) }
    val currentReminders by remember { mutableStateOf(reminders.toMutableStateList()) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingSize.default)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(
                modifier = Modifier.padding(bottom = PaddingSize.default),
                onClick = { showNewReminderDialog = true },
            ) {
                Icon(
                    modifier = Modifier.padding(end = PaddingSize.quarter),
                    painter = painterResource(R.drawable.notification_add_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(text = "Add Reminder", color = MaterialTheme.colorScheme.primary)
            }

            if (showDatePickerDialog) {
                DatePickerDialog(showDatePickerDialog = { showDatePickerDialog = it })
            }
            ReminderList(
                contentPadding = PaddingValues(bottom = PaddingSize.fabButton),
                readOnly = false,
                reminders = currentReminders.toImmutableList(),
                deleteReminder = { idToBeDeleted ->
                    currentReminders.removeIf { reminder -> reminder.id == idToBeDeleted }
                },
            )
        }
        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = { onSave(currentReminders.toImmutableList(), eventId) },
        ) {
            Icon(painter = painterResource(R.drawable.check_24px), contentDescription = null)
        }
    }
    if (showNewReminderDialog) {
        ReminderDateTimePicker(
            onSave = { dateTime ->
                currentReminders.add(Reminder(eventItemId = eventId, dateTime = dateTime))
                showNewReminderDialog = false
            },
            onDismiss = { showNewReminderDialog = false },
        )
    }
}

@Composable
@PreviewLightDark
fun ReminderScreenPreview() {
    MyAppTheme {
        Surface {
            ReminderScreen(
                reminders =
                    persistentListOf(
                        Reminder(1, 1, LocalDateTime.now()),
                        Reminder(2, 1, LocalDateTime.now().plusDays(1)),
                        Reminder(3, 1, LocalDateTime.now().plusDays(2)),
                        Reminder(4, 1, LocalDateTime.now().plusDays(3)),
                        Reminder(5, 1, LocalDateTime.now().plusDays(4)),
                        Reminder(6, 1, LocalDateTime.now().plusDays(5)),
                        Reminder(7, 1, LocalDateTime.now().plusDays(6)),
                        Reminder(8, 1, LocalDateTime.now().plusDays(7)),
                        Reminder(9, 1, LocalDateTime.now().plusDays(8)),
                        Reminder(10, 1, LocalDateTime.now().plusDays(9)),
                    )
            )
        }
    }
}

@Composable
@PreviewLightDark
fun ReminderScreenEmptyPreview() {
    MyAppTheme { Surface { ReminderScreen(reminders = persistentListOf()) } }
}
