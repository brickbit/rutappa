package com.rgr.rutappa.android.provider

import android.content.Context
import com.rgr.rutappa.domain.model.TapaResult
import com.rgr.rutappa.domain.provider.DocumentProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File

class DocumentProviderImpl(
    private val context: Context
): DocumentProvider {
    override fun writeJson(result: List<TapaResult>) {
        val jsonData = Json.encodeToJsonElement(result)
        val filename = "data.json"
        val file = File(context.filesDir, filename)
        file.writeText(jsonData.toString())
    }
}