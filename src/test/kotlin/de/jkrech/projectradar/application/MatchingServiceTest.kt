package de.jkrech.projectradar.application

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verifySequence
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.ai.document.Document
import org.springframework.ai.embedding.TokenCountBatchingStrategy
import org.springframework.ai.openai.OpenAiEmbeddingModel

@ExtendWith(MockKExtension::class)
class MatchingServiceTest {

    @MockK
    private lateinit var embeddingModel: OpenAiEmbeddingModel

    @MockK
    private lateinit var profileReader: ProfileReader

    @MockK
    private lateinit var projectsImporter: ProjectsImporter

    private lateinit var matchingService: MatchingService

    @Test
    fun `should find matches`() {
        // given
        val testDocuments = listOf(Document("Some content", mapOf("filename" to "profile-de.md")))
        every { profileReader.read() } returns testDocuments
        every { projectsImporter.import() } returns testDocuments

        // OpenAiEmbeddingModel mocken
        every { embeddingModel.embed(any(), any(), any()) } returns listOf(floatArrayOf(0.1f, 0.2f, 0.3f))

        matchingService = MatchingService(
            embeddingModel = embeddingModel,
            profileReader = profileReader,
            projectsImporters = listOf(projectsImporter)
        )

        // when
        matchingService.findMatches()

        // then
        verifySequence {
            profileReader.read()
            embeddingModel.embed(
                testDocuments,
                withArg { it.model == "text-embedding-3-small" },
                withArg { it is TokenCountBatchingStrategy }
            )
            projectsImporter.import()
            embeddingModel.embed(
                testDocuments,
                withArg { it.model == "text-embedding-3-small" },
                withArg { it is TokenCountBatchingStrategy }
            )
        }
    }
}