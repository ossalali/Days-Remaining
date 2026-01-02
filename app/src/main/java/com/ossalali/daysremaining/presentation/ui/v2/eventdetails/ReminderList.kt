package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ReminderList(reminders: ImmutableList<Reminder> = persistentListOf()) {
    reminders.forEach { stableLocalDateTime ->
        Card(
            modifier = Modifier.padding(vertical = PaddingSize.half),
            shape = RoundedCornerShape(PaddingSize.half),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingSize.default),
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
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(R.drawable.delete_24px),
                        contentDescription = null,
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
            persistentListOf(
                Reminder(1, 1, LocalDateTime.now()),
                Reminder(1, 2, LocalDateTime.now().plusDays(1)),
            )
        )
    }
}
