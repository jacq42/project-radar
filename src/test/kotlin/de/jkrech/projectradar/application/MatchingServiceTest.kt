package de.jkrech.projectradar.application

import de.jkrech.projectradar.domain.ProfileResource
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verifySequence
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.ai.document.Document
import org.springframework.core.io.ClassPathResource

@ExtendWith(MockKExtension::class)
class MatchingServiceTest {

    @MockK
    private lateinit var embeddingService: EmbeddingService

    @MockK
    private lateinit var profileReadingService: ProfileReadingService

    @MockK
    private lateinit var projectsImporter: ProjectsImporter

    @MockK
    private lateinit var similarityService: SimilarityService

    private lateinit var matchingService: MatchingService

    @Test
    fun `should find matches`() {
        // given
        val testDocuments = listOf(Document("Some content", mapOf("filename" to "profile-de.md")))
        val embedding = mutableListOf(
            floatArrayOf(1.0f, 2.0f, 3.0f),
            floatArrayOf(4.0f, 5.0f, 6.0f)
        )
        every { profileReadingService.analyze(any()) } returns testDocuments
        every { projectsImporter.import() } returns testDocuments
        every { embeddingService.embedDocuments(any()) } returns embedding
        every { similarityService.cosineSimilarity(any(), any()) } returns 0.0

        val profileResource = ProfileResource(ClassPathResource("profile/profile-de.md"))

        matchingService = MatchingService(
            embeddingService = embeddingService,
            profileReadingService = profileReadingService,
            projectsImporters = listOf(projectsImporter),
            similarityService = similarityService
        )

        // when
        matchingService.findMatches(profileResource)

        // then
        verifySequence {
            profileReadingService.analyze(profileResource)
            embeddingService.embedDocuments(testDocuments)
            projectsImporter.import()
            embeddingService.embedDocuments(testDocuments)
            similarityService.cosineSimilarity(embedding, embedding)
        }
    }
}