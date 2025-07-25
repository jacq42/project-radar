package de.jkrech.projectradar.application

import de.jkrech.projectradar.application.scoring.ProjectsImporter
import de.jkrech.projectradar.application.scoring.similarity.embedding.EmbeddingService
import de.jkrech.projectradar.application.scoring.similarity.SimilarityScoreEngine
import de.jkrech.projectradar.application.scoring.similarity.SimilarityService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verifySequence
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.ai.document.Document

@ExtendWith(MockKExtension::class)
class SimilarityScoreEngineTest {

    @MockK
    private lateinit var embeddingService: EmbeddingService

    @MockK
    private lateinit var projectsImporter: ProjectsImporter

    @MockK
    private lateinit var similarityService: SimilarityService

    private lateinit var similarityScoreEngine: SimilarityScoreEngine

    @Test
    fun `should find matches`() {
        // given
        val testDocuments = listOf(Document("Some content", mapOf("filename" to "profile-de.md")))
        val embedding = mutableListOf(
            floatArrayOf(1.0f, 2.0f, 3.0f),
            floatArrayOf(4.0f, 5.0f, 6.0f)
        )
        every { projectsImporter.import() } returns testDocuments
        every { projectsImporter.source() } returns "importer"
        every { embeddingService.embedDocuments(any()) } returns embedding
        every { similarityService.cosineSimilarity(any(), any()) } returns 0.0

        similarityScoreEngine = SimilarityScoreEngine(
            embeddingService = embeddingService,
            projectsImporters = listOf(projectsImporter),
            similarityService = similarityService
        )

        // when
        val result = similarityScoreEngine.findMostSimilarProjectsFor(testDocuments)

        // then
        assertThat(result).isNotEmpty()

        verifySequence {
            embeddingService.embedDocuments(testDocuments)
            projectsImporter.import()
            embeddingService.embedDocuments(testDocuments)
            similarityService.cosineSimilarity(embedding, embedding)
            projectsImporter.source()
        }
    }
}