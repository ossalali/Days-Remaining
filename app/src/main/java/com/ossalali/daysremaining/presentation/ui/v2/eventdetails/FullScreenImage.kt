package com.ossalali.daysremaining.presentation.ui.v2.eventdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.ossalali.daysremaining.R
import com.ossalali.daysremaining.presentation.ui.theme.PaddingDimension

@Composable
fun FullScreenImage(showFullScreenImage: (Boolean) -> Unit = {}, imageUri: String? = null) {
    Dialog(
        onDismissRequest = { showFullScreenImage(false) },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = imageUri,
                contentDescription = "Event image fullscreen",
                contentScale = ContentScale.Fit,
            )
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(PaddingDimension.default),
                onClick = { showFullScreenImage(false) },
            ) {
                Icon(
                    painter = painterResource(R.drawable.close_24px),
                    contentDescription = "Close",
                    tint = Color.White,
                )
            }
        }
    }
}
