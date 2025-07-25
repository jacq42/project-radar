package de.jkrech.projectradar.application.scoring.similarity.embedding

import org.springframework.ai.document.Document

interface EmbeddingService {

    fun embedDocuments(documents: List<Document>): List<FloatArray>
}