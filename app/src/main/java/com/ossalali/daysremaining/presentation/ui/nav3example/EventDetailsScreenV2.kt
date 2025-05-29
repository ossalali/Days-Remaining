package com.ossalali.daysremaining.presentation.ui.nav3example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ossalali.daysremaining.model.EventItem

@Composable
fun EventDetailsScreenV2(eventItem: EventItem) {
    Surface(color = Color.Blue, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ID: ${eventItem.id}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "TITLE: ${eventItem.title}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "DESCRIPTION: ${eventItem.description}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "DATE: ${eventItem.date}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "IS_ARCHIVED: ${eventItem.isArchived}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
