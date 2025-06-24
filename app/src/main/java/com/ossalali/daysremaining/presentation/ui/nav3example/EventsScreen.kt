package com.ossalali.daysremaining.presentation.ui.nav3example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ossalali.daysremaining.model.EventItem
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(backStack: SnapshotStateList<Any>) {
    Scaffold { paddingValues ->
        Surface(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(), color = Color.Red) {
            Column {
                Text("THIS IS THE EVENTS SCREEN")
                Button(
                    onClick = {
                        backStack.add(
                            EventDetailsScreen(
                                EventItem(
                                    title = "NAV EXAMPLE",
                                    date = LocalDate.now().plusDays(12),
                                    description = "THIS IS THE NAV EXAMPLE EVENT"
                                ),
                                paddingValues
                            )
                        )
                    }) {
                    Text("OPEN DETAILS")
            }
            }
    }
    }
}
