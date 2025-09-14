package com.ossalali.daysremaining.infrastructure

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object ImageStorage {

    fun createImageFileInAppStorage(context: Context, extension: String = ".jpg"): File {
        val imagesDir = File(context.filesDir, "images")
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
        return File.createTempFile("event_", extension, imagesDir)
    }

    fun persistImageFromUri(context: Context, sourceUri: Uri): Uri? {
        return try {
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                val destination = createImageFileInAppStorage(context)
                destination.outputStream().use { output -> input.copyTo(output) }
                FileProvider.getUriForFile(
                    context,
                    context.packageName + ".fileprovider",
                    destination,
                )
            }
        } catch (_: Exception) {
            null
        }
    }
}
