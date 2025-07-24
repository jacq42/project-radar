package de.jkrech.projectradar.application

import org.springframework.ai.document.Document
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["service.embedding.type"], havingValue = "fake", matchIfMissing = false)
class FakeEmbeddingService: EmbeddingService {

    override fun embedDocuments(documents: List<Document>): List<FloatArray> {
        return listOf()
    }
}