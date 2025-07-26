@file:OptIn(ExperimentalGlancePreviewApi::class)

package com.ossalali.daysremaining.presentation.ui.previews

import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview

@TwoWide @ThreeWide @FourWide @FiveWide annotation class DefaultWidgetPreviews

@Preview(150, 50)
@Preview(150, 100)
@Preview(150, 150)
@Preview(150, 200)
@Preview(150, 250)
annotation class TwoWide

@Preview(150, 50)
@Preview(150, 50)
@Preview(150, 50)
@Preview(150, 50)
@Preview(150, 50)
annotation class ThreeWide

@Preview(150, 50)
@Preview(150, 50)
@Preview(150, 50)
@Preview(150, 50)
@Preview(150, 50)
annotation class FourWide

@Preview(150, 50)
@Preview(150, 50)
@Preview(150, 50)
@Preview(150, 50)
@Preview(150, 50)
annotation class FiveWide
