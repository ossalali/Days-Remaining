package com.ossalali.daysremaining.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.ossalali.daysremaining.model.EventItem

/** Item displayed in search results */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchEventItem(event: EventItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
      shape = MaterialTheme.shapes.medium,
      modifier = modifier.padding(8.dp).clickable(onClick = onClick),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Row(
          modifier = Modifier.fillMaxSize().padding(8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                  text = event.title,
                  style = MaterialTheme.typography.titleLarge,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                )
                Text(
                  text = event.numberOfDays.toString(),
                  fontSize = TextUnit(16f, TextUnitType.Em),
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                )
                Text(
                  text = "Days",
                  style = MaterialTheme.typography.bodyMediumEmphasized,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                )
            }
        }
    }
}
