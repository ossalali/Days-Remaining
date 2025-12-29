package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R

@Composable
fun EventListEmpty(onAddEvent: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .clickable(onClick = onAddEvent),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Add your first Event!")
            Icon(painter = painterResource(R.drawable.add_24px), contentDescription = "add event")
        }
    }
}

@Composable
@PreviewLightDark
fun EventListEmptyPreview() {
    MyAppTheme { Surface { EventListEmpty() } }
}
