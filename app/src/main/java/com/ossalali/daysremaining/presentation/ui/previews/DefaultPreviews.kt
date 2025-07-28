package com.ossalali.daysremaining.presentation.ui.previews

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@MobilePreviews @TabletPreviews @SmallPhonePreviews annotation class DefaultPreviews

@Preview(
  name = "Phone Light Mode",
  showSystemUi = true,
  device = Devices.PIXEL_7_PRO,
  uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
  name = "Phone Dark Mode",
  showSystemUi = true,
  device = Devices.PIXEL_7_PRO,
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
annotation class MobilePreviews

@Preview(
  name = "Small Phone Light Mode",
  showSystemUi = true,
  device = "spec:width=360dp,height=640dp,dpi=480",
  uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
  name = "Small Phone Dark Mode",
  showSystemUi = true,
  device = "spec:width=360dp,height=640dp,dpi=480",
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
annotation class SmallPhonePreviews

@Preview(
  name = "Tablet Light Mode",
  showSystemUi = true,
  device = Devices.PIXEL_TABLET,
  uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
  name = "Tablet Dark Mode",
  showSystemUi = true,
  device = Devices.PIXEL_TABLET,
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
annotation class TabletPreviews
