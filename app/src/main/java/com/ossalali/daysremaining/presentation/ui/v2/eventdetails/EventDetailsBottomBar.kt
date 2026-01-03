package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize

@Composable
fun EventDetailsBottomBar(
    modifier: Modifier = Modifier,
    hasChanges: Boolean = false,
    isSaving: Boolean = false,
    isDeleting: Boolean = false,
    isArchived: Boolean = false,
    leftButtonDrawable: Int,
    leftButtonContentDescription: String = "",
    rightButtonDrawable: Int,
    rightButtonContentDescription: String = "",
    onSaveClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingSize.default),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            onClick = onDeleteClick,
        ) {
            if (isDeleting) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onError)
            } else {
                Icon(
                    painter = painterResource(leftButtonDrawable),
                    contentDescription = leftButtonContentDescription,
                )
            }
        }
        if (!isArchived) {
            FloatingActionButton(
                onClick = onSaveClick,
                containerColor =
                    if (hasChanges) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface,
                contentColor =
                    if (hasChanges) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    Icon(
                        painter = painterResource(rightButtonDrawable),
                        contentDescription = rightButtonContentDescription,
                    )
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
fun EventDetailsBottomBarPreview() {
    MyAppTheme {
        EventDetailsBottomBar(
            leftButtonDrawable = R.drawable.delete_24px,
            rightButtonDrawable = R.drawable.check_24px,
        )
    }
}

@Composable
@PreviewLightDark
fun EventDetailsBottomBarWithChangesPreview() {
    MyAppTheme {
        EventDetailsBottomBar(
            hasChanges = true,
            leftButtonDrawable = R.drawable.delete_24px,
            rightButtonDrawable = R.drawable.check_24px,
        )
    }
}

@Composable
@PreviewLightDark
fun EventDetailsBottomBarArchivedPreview() {
    MyAppTheme {
        EventDetailsBottomBar(
            leftButtonDrawable = R.drawable.delete_24px,
            rightButtonDrawable = R.drawable.check_24px,
            isArchived = true,
        )
    }
}

@Composable
@PreviewLightDark
fun EventDetailsBottomBarAddModePreview() {
    MyAppTheme {
        EventDetailsBottomBar(
            leftButtonDrawable = R.drawable.close_24px,
            rightButtonDrawable = R.drawable.add_24px,
        )
    }
}

@Composable
@PreviewLightDark
fun EventDetailsBottomBarSavingPreview() {
    MyAppTheme {
        EventDetailsBottomBar(
            isSaving = true,
            hasChanges = true,
            leftButtonDrawable = R.drawable.close_24px,
            rightButtonDrawable = R.drawable.add_24px,
        )
    }
}

@Composable
@PreviewLightDark
fun EventDetailsBottomBarDeletingPreview() {
    MyAppTheme {
        EventDetailsBottomBar(
            isDeleting = true,
            leftButtonDrawable = R.drawable.close_24px,
            rightButtonDrawable = R.drawable.add_24px,
        )
    }
}
