package com.rgr.rutappa.android.provider

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.rgr.rutappa.domain.model.TapaResult
import com.rgr.rutappa.domain.provider.DocumentProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File
import java.net.URL

class DocumentProviderImpl(
    private val context: Context
): DocumentProvider {
    override fun writeJson(result: List<TapaResult>) {
        val jsonData = Json.encodeToJsonElement(result)
        val filename = "data.json"
        val file = File(context.filesDir, filename)
        file.writeText(jsonData.toString())
        saveFileUsingMediaStore(context,file.toURL().toString(),filename)
    }

    private fun saveFileUsingMediaStore(context: Context, url: String, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            URL(url).openStream().use { input ->
                resolver.openOutputStream(uri).use { output ->
                    input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                }
            }
        }
    }

}