package de.jkrech.projectradar.application

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.ai.document.Document
import org.springframework.ai.embedding.TokenCountBatchingStrategy
import org.springframework.ai.openai.OpenAiEmbeddingModel

@ExtendWith(MockKExtension::class)
class OpenAiEmbeddingServiceTest {

    @MockK
    private lateinit var embeddingModel: OpenAiEmbeddingModel

    @InjectMockKs
    private lateinit var embeddingService: OpenAiEmbeddingService

    @Test
    fun `should embed documents`() {
        // given
        val testDocuments = listOf(Document("Some content", mapOf("filename" to "profile-de.md")))
        every { embeddingModel.embed(any(), any(), any()) } returns listOf(floatArrayOf(0.1f, 0.2f, 0.3f))

        // when
        embeddingService.embedDocuments(testDocuments)

        // then
        verify {
            embeddingModel.embed(
                testDocuments,
                withArg { it.model == "text-embedding-3-small" },
                withArg { it is TokenCountBatchingStrategy }
            )
        }
    }

}