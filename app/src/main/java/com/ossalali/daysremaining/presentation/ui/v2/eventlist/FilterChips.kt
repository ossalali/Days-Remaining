package com.ossalali.daysremaining.presentation.ui.v2.eventlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize

@Composable
fun FilterChips(
    modifier: Modifier = Modifier,
    activeFilterEnabled: Boolean = true,
    archivedFilterEnabled: Boolean = false,
    onToggleActiveFilter: () -> Unit = {},
    onToggleArchivedFilter: () -> Unit = {},
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Start) {
        FilterChip(
            selected = activeFilterEnabled,
            onClick = onToggleActiveFilter,
            label = { Text(text = "Active") },
            leadingIcon =
                if (activeFilterEnabled) {
                    {
                        Icon(
                            painter = painterResource(R.drawable.check_24px),
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    }
                } else {
                    null
                },
        )

        Spacer(Modifier.width(PaddingSize.quarter))

        FilterChip(
            selected = archivedFilterEnabled,
            onClick = onToggleArchivedFilter,
            label = { Text(text = "Archived") },
            leadingIcon =
                if (archivedFilterEnabled) {
                    {
                        Icon(
                            painter = painterResource(R.drawable.check_24px),
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    }
                } else {
                    null
                },
        )
    }
}

@Composable
@PreviewLightDark
fun FilterChipsPreview() {
    MyAppTheme { FilterChips() }
}
