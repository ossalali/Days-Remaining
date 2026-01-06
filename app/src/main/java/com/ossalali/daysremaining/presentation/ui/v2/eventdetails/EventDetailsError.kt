package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.presentation.ui.theme.IconSize
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize

@Composable
fun EventDetailsError() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Error while loading data")
            Spacer(modifier = Modifier.height(PaddingSize.default))
            Icon(
                modifier = Modifier.size(IconSize.double),
                painter = painterResource(R.drawable.warning_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
@PreviewLightDark
fun EventDetailsErrorPreview() {
    MyAppTheme { Surface { EventDetailsError() } }
}
