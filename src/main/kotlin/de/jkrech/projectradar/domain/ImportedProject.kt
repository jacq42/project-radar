package de.jkrech.projectradar.domain

import org.springframework.ai.document.Document

data class ImportedProject(
    val importerSource: String,
    val documents: List<Document>,
    val embeddings: List<FloatArray>,
    val similarity: Double
) {
    fun title(): String {
        return documents.firstOrNull { it.metadata["title"] != null }
            ?.metadata?.get("title") as? String
            ?: importerSource
    }

    fun source(): String {
        return documents.firstOrNull { it.metadata["url"] != null }
            ?.metadata?.get("url") as? String
            ?: importerSource
    }
}