package com.ossalali.daysremaining.presentation.ui.v2

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ossalali.daysremaining.BuildConfig
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.presentation.ui.theme.PaddingDimension

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    onBackClick: () -> Unit = {},
    navigateToDebugScreen: () -> Unit = {},
    navigateToSettingsScreen: () -> Unit = {},
    showTopAppBarButtons: Boolean = false,
    showBackButton: Boolean = false,
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back_24px),
                        contentDescription = stringResource(R.string.back),
                    )
                }
            } else {
                if (BuildConfig.DEBUG) {
                    IconButton(
                        modifier = Modifier.padding(horizontal = PaddingDimension.quarter),
                        onClick = { navigateToDebugScreen() },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.bug_report_24px),
                            contentDescription = stringResource(R.string.open_debug_screen),
                        )
                    }
                }
            }
        },
        actions = {
            if (showTopAppBarButtons) {
                IconButton(onClick = { navigateToSettingsScreen() }) {
                    Icon(
                        painter = painterResource(R.drawable.settings_24px),
                        contentDescription = stringResource(R.string.open_settings_screen),
                    )
                }
            }
        },
    )
}

@PreviewLightDark
@Composable
fun TopAppBarPreview() {
    MyAppTheme { Surface { TopAppBar() } }
}

@PreviewLightDark
@Composable
fun TopAppBarWithButtonsPreview() {
    MyAppTheme { Surface { TopAppBar(showTopAppBarButtons = true, showBackButton = true) } }
}
