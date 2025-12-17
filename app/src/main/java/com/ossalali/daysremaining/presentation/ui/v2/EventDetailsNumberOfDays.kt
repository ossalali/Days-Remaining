package com.ossalali.daysremaining.presentation.ui.v2

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.presentation.ui.theme.Dimensions

@Composable
fun EventDetailsNumberOfDays(numberOfDays: String) {
    Text(text = numberOfDays)
    Text(
        modifier = Modifier.padding(bottom = Dimensions.default),
        text = stringResource(R.string.days_remaining),
    )
}
