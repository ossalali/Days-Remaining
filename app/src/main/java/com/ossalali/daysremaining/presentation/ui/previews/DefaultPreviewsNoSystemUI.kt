package com.ossalali.daysremaining.presentation.ui.previews

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@MobilePreviewsNoSystemUI @TabletPreviewsNoSystemUI annotation class DefaultPreviewsNoSystemUI

@Preview(
    name = "Phone Light Mode",
    showSystemUi = false,
    device = Devices.PIXEL_7_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Phone Dark Mode",
    showSystemUi = false,
    device = Devices.PIXEL_7_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
annotation class MobilePreviewsNoSystemUI

@Preview(
    name = "Tablet Light Mode",
    showSystemUi = false,
    device = Devices.PIXEL_TABLET,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Tablet Dark Mode",
    showSystemUi = false,
    device = Devices.PIXEL_TABLET,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
annotation class TabletPreviewsNoSystemUI
