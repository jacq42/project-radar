package de.jkrech.projectradar.application

import org.springframework.ai.document.Document
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.TokenCountBatchingStrategy
import org.springframework.ai.openai.OpenAiEmbeddingOptions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["service.embedding.type"], havingValue = "openai", matchIfMissing = false)
class OpenAiEmbeddingService(
    private val embeddingModel: EmbeddingModel,
): EmbeddingService {

    private val embeddingOptions = OpenAiEmbeddingOptions.builder().build()
    private val batchingStrategy= TokenCountBatchingStrategy()

    override fun embedDocuments(documents: List<Document>): MutableList<FloatArray> {
        return embeddingModel.embed(
            documents,
            embeddingOptions,
            batchingStrategy
        )
    }
}