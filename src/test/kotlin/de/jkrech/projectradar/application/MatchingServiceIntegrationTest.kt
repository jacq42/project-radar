package de.jkrech.projectradar.application

import de.jkrech.projectradar.ConfigurationHelper.Companion.configuredFreelancermapPlatformScraper
import de.jkrech.projectradar.ConfigurationHelper.Companion.configuredMarkdownProjectsImporter
import de.jkrech.projectradar.ConfigurationHelper.Companion.configuredPdfProjectsImporter
import de.jkrech.projectradar.domain.ProfileResource
import de.jkrech.projectradar.ports.profile.ProfileReaderFactory
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.ResourceLoader

@SpringBootTest
class MatchingServiceIntegrationTest {

    @MockK
    private lateinit var mockedEmbeddingModel: OpenAiEmbeddingModel

    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @Autowired
    lateinit var profileReaderFactory: ProfileReaderFactory

    @BeforeEach
    fun setUp() {
        every { mockedEmbeddingModel.embed(any(), any(), any()) } returns emptyList()
    }

    @Test
    fun `should find matches with real markdown and pdf files`() {
        // given
        val profileMarkdown = ProfileResource(ClassPathResource("profile/profile-test.md"))

        val markdownProjectsImporter = configuredMarkdownProjectsImporter(resourceLoader)
        val pdfProjectsImporter = configuredPdfProjectsImporter(resourceLoader)

        val matchingService = MatchingService(
            embeddingModel = mockedEmbeddingModel,
            profileReaderFactory = profileReaderFactory,
            projectsImporters = listOf(markdownProjectsImporter, pdfProjectsImporter)
        )

        // when
        matchingService.findMatches(profileMarkdown)
    }

    @Test
    fun `should find matches with real markdown files and platform scrapes`() {
        // given
        val profileMarkdown = ProfileResource(ClassPathResource("profile/profile-test.md"))
        val freelancerMapScraper = configuredFreelancermapPlatformScraper(listOf("kotlin", "devops", "cloud"))

        val matchingService = MatchingService(
            embeddingModel = mockedEmbeddingModel,
            profileReaderFactory = profileReaderFactory,
            projectsImporters = listOf(freelancerMapScraper))

        // when
        matchingService.findMatches(profileMarkdown)
    }
}