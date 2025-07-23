package de.jkrech.projectradar.application

import de.jkrech.projectradar.domain.ProfileResource
import de.jkrech.projectradar.ports.profile.ProfileReaderFactory
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verifySequence
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.ai.document.Document
import org.springframework.ai.embedding.TokenCountBatchingStrategy
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.core.io.ClassPathResource

@ExtendWith(MockKExtension::class)
class MatchingServiceTest {

    @MockK
    private lateinit var embeddingModel: OpenAiEmbeddingModel

    @MockK
    private lateinit var profileReader: ProfileReader

    @MockK
    private lateinit var profileReaderFactory: ProfileReaderFactory

    @MockK
    private lateinit var projectsImporter: ProjectsImporter

    private lateinit var matchingService: MatchingService

    @Test
    fun `should find matches`() {
        // given
        val testDocuments = listOf(Document("Some content", mapOf("filename" to "profile-de.md")))
        every { profileReader.read(any()) } returns testDocuments
        every { profileReaderFactory.findBy(any()) } returns profileReader
        every { projectsImporter.import() } returns testDocuments
        every { embeddingModel.embed(any(), any(), any()) } returns listOf(floatArrayOf(0.1f, 0.2f, 0.3f))

        val profileResource = ProfileResource(ClassPathResource("profile/profile-de.md"))

        matchingService = MatchingService(
            embeddingModel = embeddingModel,
            profileReaderFactory = profileReaderFactory,
            projectsImporters = listOf(projectsImporter)
        )

        // when
        matchingService.findMatches(profileResource)

        // then
        verifySequence {
            profileReader.read(profileResource)
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