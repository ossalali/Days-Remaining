package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.window.DialogProperties
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDialog(onSave: (Reminder) -> Unit = {}, onDismiss: () -> Unit = {}) {
    val timePickerState by remember {
        mutableStateOf(TimePickerState(initialHour = 12, initialMinute = 0, is24Hour = true))
    }
    var eventType by remember { mutableStateOf(EventType.ON_EVENT_DAY) }

    @Composable
    fun textPropsFor(type: EventType): TextProps {
        return if (eventType == type) {
            TextProps(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        } else {
            TextProps(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Normal,
            )
        }
    }

    AlertDialog(
        title = { Text(text = "Reminders") },
        confirmButton = {
            TextButton(
                onClick = {
                    if (eventType == EventType.BEFORE_EVENT) {
                    }

                    if (eventType == EventType.ON_EVENT_DAY) {
                        onSave(
                            Reminder(
                                type = eventType,
                                dateTime =
                                    LocalDateTime.of(
                                        LocalDate.now(),
                                        LocalTime.of(timePickerState.hour, timePickerState.minute),
                                    ),
                            )
                        )
                    }
                }
            ) {
                Text(text = "OK")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = "Cancel") } },
        onDismissRequest = onDismiss,
        properties = DialogProperties(),
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "When would you like to be reminded?")

                Spacer(modifier = Modifier.height(PaddingSize.default))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(onClick = { eventType = EventType.BEFORE_EVENT }) {
                        val props = textPropsFor(EventType.BEFORE_EVENT)
                        Text(
                            text = "Before Event",
                            color = props.color,
                            fontWeight = props.fontWeight,
                        )
                    }

                    TextButton(onClick = { eventType = EventType.ON_EVENT_DAY }) {
                        val props = textPropsFor(EventType.ON_EVENT_DAY)
                        Text(
                            text = "On Event Day",
                            color = props.color,
                            fontWeight = props.fontWeight,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(PaddingSize.default))

                when (eventType) {
                    EventType.BEFORE_EVENT -> BeforeEventContent()
                    EventType.ON_EVENT_DAY -> {
                        Text(text = "What time would you like to be reminded?")
                        Spacer(modifier = Modifier.height(PaddingSize.default))
                        TimePicker(state = timePickerState)
                    }
                }
            }
        },
    )
}

@Composable
fun BeforeEventContent() {
    Text(text = "How many days before the event would you like to be reminded?")
}

@Composable
@PreviewLightDark
fun ReminderDialogPreview() {
    MyAppTheme { ReminderDialog() }
}

data class TextProps(val color: Color, val fontWeight: FontWeight)

enum class EventType {
    BEFORE_EVENT,
    ON_EVENT_DAY,
}

data class Reminder(
    val type: EventType = EventType.BEFORE_EVENT,
    val dateTime: LocalDateTime? = null,
)
