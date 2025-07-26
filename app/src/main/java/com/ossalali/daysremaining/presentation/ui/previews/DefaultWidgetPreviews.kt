@file:OptIn(ExperimentalGlancePreviewApi::class)

package com.ossalali.daysremaining.presentation.ui.previews

import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview

/** A meta-annotation that combines all defined widget size previews for a comprehensive look. */
@TinySquarePreview
@SmallHorizontalRectanglePreview
@HorizontalRectanglePreview
@WideRectanglePreview
@VerticalRectanglePreview
@TallRectanglePreview
@SmallSquarePreview
@LargeRectanglePreview
@MediumSquarePreview
@BigSquarePreview
@ExtraBigSquarePreview
annotation class DefaultWidgetPreviews

/** Previews the widget at its ~2x2 grid size. */
@Preview(widthDp = 120, heightDp = 120) annotation class TinySquarePreview

/** Previews the widget at its ~3x2 grid size. */
@Preview(widthDp = 250, heightDp = 120) annotation class SmallHorizontalRectanglePreview

/** Previews the widget at its ~4x2 grid size. */
@Preview(widthDp = 320, heightDp = 120) annotation class HorizontalRectanglePreview

/** Previews the widget at its ~5x2 grid size. */
@Preview(widthDp = 400, heightDp = 120) annotation class WideRectanglePreview

/** Previews the widget at its ~2x3 grid size. */
@Preview(widthDp = 120, heightDp = 250) annotation class VerticalRectanglePreview

/** Previews the widget at its ~2x4 or 2x5 grid size. */
@Preview(widthDp = 120, heightDp = 400) annotation class TallRectanglePreview

/** Previews the widget at its ~3x3 grid size. */
@Preview(widthDp = 250, heightDp = 250) annotation class SmallSquarePreview

/** Previews the widget at its ~4x3 grid size. */
@Preview(widthDp = 320, heightDp = 250) annotation class LargeRectanglePreview

/** Previews the widget at its ~4x4 grid size. */
@Preview(widthDp = 320, heightDp = 320) annotation class MediumSquarePreview

/** Previews the widget at its ~5x4 grid size. */
@Preview(widthDp = 400, heightDp = 400) annotation class BigSquarePreview

/** Previews the widget at its ~5x5+ grid size. */
@Preview(widthDp = 500, heightDp = 500) annotation class ExtraBigSquarePreview
