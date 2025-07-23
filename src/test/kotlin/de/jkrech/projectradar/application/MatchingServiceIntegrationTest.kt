package de.jkrech.projectradar.application

import de.jkrech.projectradar.ConfigurationHelper.Companion.configuredFreelancermapPlatformScraper
import de.jkrech.projectradar.ConfigurationHelper.Companion.configuredMarkdownProjectsImporter
import de.jkrech.projectradar.ConfigurationHelper.Companion.configuredPdfProjectsImporter
import de.jkrech.projectradar.domain.ProfileResource
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource

@SpringBootTest
class MatchingServiceIntegrationTest {

    @MockK
    private lateinit var mockedEmbeddingModel: OpenAiEmbeddingModel

    @MockK
    private lateinit var embeddingService: EmbeddingService

    @Autowired
    private lateinit var profileReadingService: ProfileReadingService

    @BeforeEach
    fun setUp() {
        every { embeddingService.embedDocuments(any()) } returns mutableListOf()
        every { mockedEmbeddingModel.embed(any(), any(), any()) } returns emptyList()
    }

    @Test
    fun `should find matches with real markdown and pdf files`() {
        // given
        val profileMarkdown = ProfileResource(ClassPathResource("profile/profile-test.md"))

        val markdownProjectsImporter = configuredMarkdownProjectsImporter()
        val pdfProjectsImporter = configuredPdfProjectsImporter()

        val matchingService = MatchingService(
            embeddingService = embeddingService,
            profileReadingService = profileReadingService,
            projectsImporters = listOf(markdownProjectsImporter, pdfProjectsImporter)
        )

        // when
        matchingService.findMatches(profileMarkdown)
    }

    @Test
    @Disabled("Just for manual testing, not part of the CI pipeline")
    fun `should find matches with real markdown files and platform scrapes`() {
        // given
        val profileMarkdown = ProfileResource(ClassPathResource("profile/profile-test.md"))
        val freelancerMapScraper = configuredFreelancermapPlatformScraper(listOf("kotlin", "devops", "cloud"))

        val matchingService = MatchingService(
            embeddingService = embeddingService,
            profileReadingService = profileReadingService,
            projectsImporters = listOf(freelancerMapScraper))

        // when
        matchingService.findMatches(profileMarkdown)
    }
}