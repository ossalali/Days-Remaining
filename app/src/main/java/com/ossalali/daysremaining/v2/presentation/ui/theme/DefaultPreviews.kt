package com.ossalali.daysremaining.v2.presentation.ui.theme

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@MobilePreviews
@TabletPreviews
annotation class DefaultPreviews

@Preview(
    name = "Phone Light Mode",
    showSystemUi = true,
    device = Devices.PIXEL_7_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Phone Dark Mode",
    showSystemUi = true,
    device = Devices.PIXEL_7_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class MobilePreviews

@Preview(
    name = "Tablet Light Mode",
    showSystemUi = true,
    device = Devices.PIXEL_TABLET,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Tablet Dark Mode",
    showSystemUi = true,
    device = Devices.PIXEL_TABLET,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class TabletPreviews