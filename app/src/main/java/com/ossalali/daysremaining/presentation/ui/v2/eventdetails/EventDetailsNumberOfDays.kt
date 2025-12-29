package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize
import com.ossalali.daysremaining.presentation.ui.theme.TextSize

@Composable
fun EventDetailsNumberOfDays(numberOfDays: String) {
    Text(text = numberOfDays, fontSize = TextSize.triple)
    Text(
        modifier = Modifier.padding(bottom = PaddingSize.default),
        text = stringResource(R.string.days_remaining),
    )
}
