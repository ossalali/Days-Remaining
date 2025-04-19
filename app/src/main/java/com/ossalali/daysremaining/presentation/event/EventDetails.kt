package com.ossalali.daysremaining.presentation.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ossalali.daysremaining.model.EventItem
import com.ossalali.daysremaining.v2.presentation.ui.theme.DefaultPreviews
import com.ossalali.daysremaining.v2.presentation.ui.theme.Dimensions
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetails(
    event: EventItem,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = event.title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimensions.default) // Add horizontal padding only
        ) {
            val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            Text(
                text = "Date: ${event.date.format(dateFormatter)}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = Dimensions.half)
            )
            Text(
                text = "Description: ${event.description}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = Dimensions.half)
            )
            Text(
                text = "Days Remaining: ${event.getNumberOfDays()}",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@DefaultPreviews
@Composable
fun EventDetailsPreview() {
    EventDetails(
        event = EventItem(
            id = 1,
            title = "Sample Event Title",
            date = LocalDate.now().plusDays(10),
            description = "This is a sample event description."
        ),
        onBackClick = {}
    )
}