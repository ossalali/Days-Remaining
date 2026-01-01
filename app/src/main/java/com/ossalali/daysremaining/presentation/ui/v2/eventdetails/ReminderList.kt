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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDateTime

@Composable
fun ReminderList(reminders: ImmutableList<LocalDateTime> = persistentListOf()) {
    reminders.forEach {
        Card(shape = RoundedCornerShape(PaddingSize.half)) {
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
                Text(text = "${it.year}-${it.monthValue}-${it.dayOfYear} ${it.hour}:${it.minute}")
            }
        }
    }
}

@Composable
@PreviewLightDark
fun ReminderListPreview() {
    MyAppTheme { ReminderList(persistentListOf(LocalDateTime.now())) }
}
