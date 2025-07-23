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

    private lateinit var matchingService: MatchingService

    @Test
    fun `should find matches`() {
        // given
        val testDocuments = listOf(Document("Some content", mapOf("filename" to "profile-de.md")))
        every { profileReadingService.analyze(any()) } returns testDocuments
        every { projectsImporter.import() } returns testDocuments
        every { embeddingService.embedDocuments(any()) } returns mutableListOf()

        val profileResource = ProfileResource(ClassPathResource("profile/profile-de.md"))

        matchingService = MatchingService(
            embeddingService = embeddingService,
            profileReadingService = profileReadingService,
            projectsImporters = listOf(projectsImporter)
        )

        // when
        matchingService.findMatches(profileResource)

        // then
        verifySequence {
            profileReadingService.analyze(profileResource)
            embeddingService.embedDocuments(testDocuments)
            projectsImporter.import()
            embeddingService.embedDocuments(testDocuments)
        }
    }
}