package de.jkrech.projectradar.application

import de.jkrech.projectradar.application.scoring.similarity.embedding.FakeEmbeddingService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.document.Document

class FakeEmbeddingServiceTest {

    private val embeddingService = FakeEmbeddingService()

    @Test
    fun `should always return empty list`() {
        // given
        val testDocuments = listOf(Document("Some content", mapOf("filename" to "profile-de.md")))

        // when
        val documents = embeddingService.embedDocuments(testDocuments)

        // then
        assertThat(documents).isEmpty()
    }
}