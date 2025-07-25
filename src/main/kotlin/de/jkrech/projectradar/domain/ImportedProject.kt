package de.jkrech.projectradar.domain

import org.springframework.ai.document.Document

data class ImportedProject(
    val importerSource: String,
    val documents: List<Document>,
    val embeddings: List<FloatArray>? = emptyList(),
    val similarity: Double? = null,
    val relevance: Int? = null
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

    fun calculateScore(): Int {
        if (this.similarity != null) {
            return (this.similarity * 100.0).toInt()
        } else if (this.relevance != null) {
            return this.relevance
        }
        return 0
    }
}