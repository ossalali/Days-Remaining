package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ossalali.daysremaining.MyAppTheme
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.presentation.ui.theme.PaddingSize

@Composable
fun ImagePicker(
    readOnly: Boolean = false,
    imageUri: String? = null,
    showFullScreenImage: (Boolean) -> Unit = {},
    showImagePickerDialog: (Boolean) -> Unit = {},
    showConfirmImageDeleteDialog: (Boolean) -> Unit = {},
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = PaddingSize.default, bottom = PaddingSize.default)
                    .height(180.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(PaddingSize.default),
                    )
                    .clickable {
                        if (!imageUri.isNullOrBlank()) {
                            showFullScreenImage(true)
                        } else {
                            if (readOnly) return@clickable
                            showImagePickerDialog(true)
                        }
                    },
            contentAlignment = Alignment.Center,
        ) {
            if (!imageUri.isNullOrBlank()) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.small),
                    model = imageUri,
                    contentDescription = "Event image",
                    contentScale = ContentScale.Crop,
                )
            } else {
                Text(
                    text = "Tap to add an image",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (!imageUri.isNullOrBlank() && !readOnly) {
            Column(
                modifier =
                    Modifier
                        .padding(
                            top = PaddingSize.default,
                            bottom = PaddingSize.default,
                            start = PaddingSize.half,
                        )
                        .height(180.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(
                    modifier =
                        Modifier.background(
                            shape = ShapeDefaults.Small,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    onClick = {
                        if (readOnly) return@IconButton

                        showFullScreenImage(true)
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.fullscreen_24px),
                        contentDescription = "View fullscreen",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(
                    modifier =
                        Modifier.background(
                            shape = ShapeDefaults.Small,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    onClick = {
                        if (readOnly) return@IconButton
                        showImagePickerDialog(true)
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.photo_library_24px),
                        contentDescription = "Change image",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(
                    modifier =
                        Modifier.background(
                            shape = ShapeDefaults.Small,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    onClick = {
                        if (readOnly) return@IconButton
                        showConfirmImageDeleteDialog(true)
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete_24px),
                        contentDescription = "Remove image",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
fun ImagePickerPreview() {
    MyAppTheme { ImagePicker(imageUri = "asd") }
}

@Composable
@PreviewLightDark
fun ImagePickerArchivedPreview() {
    MyAppTheme { ImagePicker(imageUri = "asd", readOnly = true) }
}
